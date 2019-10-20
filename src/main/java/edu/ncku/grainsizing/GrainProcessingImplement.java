package edu.ncku.grainsizing;

import edu.ncku.model.grainimage.GrainResultVO;
import edu.ncku.model.grainimage.GrainService;
import edu.ncku.model.grainimage.GrainVO;
import edu.ncku.model.tempmarker.TempMarkerService;
import edu.ncku.model.tempmarker.TempMarkerVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.*;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class GrainProcessingImplement implements GrainProcessing{
	private final Logger logger = LogManager.getLogger(GrainProcessingImplement.class.getClass());
	private final static int OPENCV_FITELLIPSE_LIMIT = 5;
	
	@Autowired
	private GrainService grainService;
	
	@Autowired
	private TempMarkerService tempMarkerService;

	@Override
	public GrainVO doGrainProcessing(String workspace, GrainParam grainParam) {
		GrainVO vo = grainService.getGrainVO(workspace);
		vo.setSmoothImg(smoothGrain(vo));
		vo.setNonGrainImg(identifyNonGrain(vo, grainParam));
		vo.setDisMapImg(generateDistanceMap(vo));
		vo.setMarkImg(generateMarker(vo));
		vo.setIndexImg(segmentGrain(vo, null));
		vo.setSegmentedImg(getBinarySegmentResut(vo.getIndexImg()));
		vo.setLatestSegmentedImg(vo.getSegmentedImg());
		vo.setEllipseImg(null);
		return vo;
	}
	
	@Override
	public Mat smoothGrain(GrainVO vo) {
		if(vo==null || vo.getOriginalImg()==null) return null;
		Mat smooth = new Mat();
		Imgproc.GaussianBlur(vo.getOriginalImg(), smooth, new Size(3, 3), 0.0, 0.0, Core.BORDER_DEFAULT);
		return smooth;
	}

	@Override
	public Mat identifyNonGrain(GrainVO vo, GrainParam grainParam) {
		if(vo==null || vo.getSmoothImg()==null) return null;
		Mat edge = new Mat();
		Imgproc.Canny(vo.getSmoothImg(), edge, grainParam.getCannyMinThreshold(), grainParam.getCannyMaxThreshold());
		return edge;
	}
	
	@Override
	public Mat generateDistanceMap(GrainVO vo) {
		Mat invertNonGrain = new Mat();
		Core.bitwise_not(vo.getNonGrainImg(), invertNonGrain);
		Mat dis = new Mat();
		Imgproc.distanceTransform(invertNonGrain, dis, Imgproc.CV_DIST_L2, 5);
		invertNonGrain.release();
		return dis;
	}
	
	@Override
	public Mat generateMarker(GrainVO vo) {
		Mat grainMark = new Mat();
		Mat dis8bit = this.convertTo8UC1(vo.getDisMapImg());
		Imgproc.threshold(dis8bit, grainMark, 20, 255, Imgproc.THRESH_BINARY);
		dis8bit.release();
		return grainMark;
	}
	
	@Override
	public Mat segmentGrain(GrainVO vo, TempMarkerVO shadowVO) {
		List<MatOfPoint> contours = new ArrayList<>(1000);
        Mat hierarchy = new Mat();
        Imgproc.findContours(vo.getMarkImg(), contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        // Create the marker image for the watershed algorithm
        Mat markers = Mat.zeros(vo.getMarkImg().size(), CvType.CV_32S);
        // Draw the foreground markers
        for (int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(markers, contours, i, new Scalar(i + 1), -1);
        }
        vo.getConfig().setMaxIndex(contours.size());
        if(shadowVO != null) {
        	List<MatOfPoint> shadowContours = new ArrayList<>(100);
            Imgproc.findContours(shadowVO.getTemp(), shadowContours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            for (int i = 0; i < shadowContours.size(); i++) {
                Imgproc.drawContours(markers, shadowContours, i, new Scalar(vo.getConfig().getMaxIndex()+1), -1);
            }
        }
        Mat m;
        if(vo.getSmoothImg().channels()==1) {
			m = new Mat();
			Imgproc.cvtColor(vo.getSmoothImg(), m, Imgproc.COLOR_GRAY2BGR);
		}else {
        	m = vo.getSmoothImg();
		}
        Imgproc.watershed(m, markers);
        vo.setOverlayImg(generateOverlay(markers, contours.size()));
		return markers;
	}
	
	private Mat generateOverlay(Mat markers, int maxIndex) {
		Random rng = new Random(System.currentTimeMillis());
        List<Scalar> colors = new ArrayList<>(maxIndex);
        for (int i = 0; i < maxIndex; i++) {
            int b = rng.nextInt(256);
            int g = rng.nextInt(256);
            int r = rng.nextInt(256);
            colors.add(new Scalar(b, g, r));
        }
        Mat dst = Mat.zeros(markers.size(), CvType.CV_8UC4);
        byte[] dstData = new byte[(int) (dst.total() * dst.channels())];
        int[] markersData = new int[(int) (markers.total() * markers.channels())];
        markers.get(0, 0, markersData);
        for (int i = 0; i < markers.rows(); i++) {
            for (int j = 0; j < markers.cols(); j++) {
                int index = markersData[i * markers.cols() + j];
                if (index > 0 && index <= maxIndex) {
                    dstData[(i * dst.cols() + j) * 4 + 1] = (byte) colors.get(index - 1).val[0];
                    dstData[(i * dst.cols() + j) * 4 + 2] = (byte) colors.get(index - 1).val[1];
                    dstData[(i * dst.cols() + j) * 4 + 3] = (byte) colors.get(index - 1).val[2];
                    dstData[(i * dst.cols() + j) * 4 + 0] = (byte) 50;
                } else {
                    dstData[(i * dst.cols() + j) * 4 + 0] = (byte) 200;
                    dstData[(i * dst.cols() + j) * 4 + 1] = (byte) 255;
                    dstData[(i * dst.cols() + j) * 4 + 2] = (byte) 255;
                    dstData[(i * dst.cols() + j) * 4 + 3] = (byte) 255;
                }
            }
        }
        dst.put(0, 0, dstData);
        return dst;
	}
	
	private Mat convertTo8UC1(Mat mat) {
		if(mat==null) return mat;
		if(mat.channels()==1 && mat.depth()==CvType.CV_8UC1) return mat;
		MinMaxLocResult minMax = Core.minMaxLoc(mat);
		double max = minMax.maxVal;
		double min = minMax.minVal;
		Mat m = new Mat(mat.height(),mat.width(),CvType.CV_8UC1);
		mat.convertTo(m, CvType.CV_8UC1, 256.0/(max-min), -256.0*min/(max-min));
		return m;
	}
	
	private Mat floodFillAsBlack(Mat marker, Mat floodFillImg) {
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(floodFillImg, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		if(contours.size()==0)
			return marker;
		Mat newMarker = marker.clone();
		Mat mask = new Mat(floodFillImg.height()+2, floodFillImg.width()+2 , CvType.CV_8UC1);
		mask.setTo(new Scalar(0));
		Scalar color = new Scalar(0);
		logger.info("floodFillAsBlack:{}", contours.size());
		for(MatOfPoint seeds:contours) {
			Point seed = seeds.toArray()[0];
			double [] colors = newMarker.get((int)seed.y, (int)seed.x);
			if(colors[0] != 0) 
				Imgproc.floodFill(newMarker, mask, seed, color);
		}
		return newMarker;
	}
	
	@Override
	public GrainVO doReSegmentGrainProcessing(GrainVO vo) {
		TempMarkerVO seedVO = tempMarkerService.getSeedMarker(vo.getConfig().getWorkspace());
		Mat newMarker = generateMergeMarker(vo, seedVO);
		vo.setMarkImg(newMarker);
		newMarker = generateSplitMarker(vo, seedVO);
		vo.setMarkImg(newMarker);
		
		TempMarkerVO shadowVO = tempMarkerService.getShadowMarker(vo.getConfig().getWorkspace());
		newMarker = floodFillAsBlack(newMarker, shadowVO.getTemp());

		vo.setMarkImg(newMarker);
		vo.setIndexImg(segmentGrain(vo, shadowVO));
		vo.setSegmentedImg(getBinarySegmentResut(vo.getIndexImg()));
		vo.setLatestSegmentedImg(vo.getSegmentedImg());
		vo.setEllipseImg(null);

		return vo;
	}
	
	@Override
	public Mat generateMergeMarker(GrainVO vo, TempMarkerVO mergeVO) {
		if(mergeVO==null || mergeVO.getTemp()==null)
			return vo.getLatestSegmentedImg(); //modify to return original segmented result
		Mat resultMat = new Mat();
		Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(2,2), new Point(1, 1));
		Mat markImg = new Mat();
		Imgproc.erode(vo.getLatestSegmentedImg(), markImg, element);
		Core.bitwise_or(markImg, mergeVO.getTemp(), resultMat);
		markImg.release();
		return resultMat;
	}
	
	@Override
	public Mat generateSplitMarker(GrainVO vo, TempMarkerVO splitVO) {
		if(splitVO==null || splitVO.getTemp()==null)
			 return vo.getMarkImg();
		Mat newMarker = floodFillAsBlack(vo.getMarkImg(), splitVO.getTemp());
		Mat resultMat = new Mat();
		Core.bitwise_or(newMarker, splitVO.getTemp(), resultMat);
		newMarker.release();
		return resultMat;
	}
	
	@Override
	public void findGrainContours(GrainVO vo) {
		if(vo==null || vo.getIndexImg()==null)
			return;
		List<MatOfPoint> allContours = new LinkedList<>();
		Mat dst = Mat.zeros(vo.getIndexImg().size(), CvType.CV_8UC1);
		Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(3, 3), new Point(1, 1));
		Mat dstDilate = Mat.zeros(vo.getIndexImg().size(), CvType.CV_8UC1);
		Mat indexMat = vo.getIndexImg();
		int cols = indexMat.cols();
		int maxIndex = vo.getConfig().getMaxIndex();
		int [] indexArray = new int[(int)indexMat.total()];
		vo.getIndexImg().get(0, 0, indexArray);
		LinkedList<Integer>[] ary = new LinkedList[maxIndex+1];
		for(int i=0;i<=maxIndex;i++)
			ary[i] = new LinkedList<>();
		for (int i = 0; i < indexMat.rows(); i++) {
			for (int j = 0; j < indexMat.cols(); j++) {
				int index = indexArray[(i * cols) + j];
				if(index<0 || index>maxIndex)
					continue;
				ary[index].add((i * cols) + j);
			}
		}
		for(int v = 0; v<=maxIndex;v++) {
			byte [] imageArray = new byte[(int)indexMat.total()];
			LinkedList<Integer> list = ary[v];
			for(int i:list){
				imageArray[i]=(byte) 255;
			}
			dst.put(0, 0, imageArray);
			Imgproc.dilate(dst, dstDilate, element);
			List<MatOfPoint> contours = new ArrayList<>();
			Imgproc.findContours(dstDilate, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
			allContours.addAll(contours);
			dstDilate.release();
		}
		dst.release();
		vo.setResults(allContours.stream().filter(contours->{
			return contours.size().height>=OPENCV_FITELLIPSE_LIMIT || contours.size().width>=OPENCV_FITELLIPSE_LIMIT;
		}).map(m->{
			GrainResultVO v = new GrainResultVO();
			v.setContour(m);
			return v;
		}).collect(Collectors.toList()));
		allContours.clear();
	}
	
	@Override
	public void fitEllipse(GrainVO vo) {
		List<GrainResultVO> list = vo.getResults();
		list.forEach(v->{
			v.setEllipse(Imgproc.fitEllipse(new MatOfPoint2f(v.getContour().toArray())));
		});
	}
	
	@Override
	public GrainVO doFitEllipse(GrainVO vo) {
		findGrainContours(vo);
		fitEllipse(vo);
		vo.setEllipseImg(drawingEllipse(vo));
		return vo;
	}
	
	private Mat drawingEllipse(GrainVO vo) {
		List<GrainResultVO> list = vo.getResults();
		List<RotatedRect> rects = list.stream().map(v->v.getEllipse()).collect(Collectors.toList());
		Mat mat = new Mat(vo.getSegmentedImg().size(), CvType.CV_8UC4);
		Scalar color = new Scalar(255, 0, 255, 127);
		for(RotatedRect rect:rects)
			Imgproc.ellipse(mat, rect, color);
		return mat;
	}
	
	@Override
	public GrainVO enhaceToShow(GrainVO vo) {
		if(vo.getDisplayImg()==null || vo.getConfig()==null)
			return vo;
		Mat newImage = Mat.zeros(vo.getDisplayImg().size(), vo.getDisplayImg().type());
		vo.getDisplayImg().convertTo(newImage, -1, vo.getConfig().getAlpha(), vo.getConfig().getBeta());
		vo.setEnhanceImg(newImage);
		return vo;
	}
	/**
	 * Turn index into binary image
	 * @param indexMat
	 * @return
	 */
	private Mat getBinarySegmentResut(Mat indexMat) {
		Mat binaryMarker = Mat.zeros(indexMat.size(), CvType.CV_8UC1);
		indexMat.convertTo(binaryMarker, CvType.CV_8UC1);
        Imgproc.threshold(binaryMarker, binaryMarker, 0, 255, Imgproc.THRESH_BINARY);
        return binaryMarker;
	}
}
