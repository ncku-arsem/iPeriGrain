package edu.ncku.model.grainimage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrainService {
	private final Logger logger = LogManager.getLogger(GrainService.class.getClass());
	@Autowired
	private GrainDAO grainDAO;
	
	public GrainVO getGrainVO(String workspace) {
		logger.info("getGrainVO:{}", workspace);
		GrainConfig config = grainDAO.getGrainConfig(workspace);
		if(config==null)
			config = grainDAO.getInitGrainConfig(workspace);
		logger.info("getGrainVO:{}", config);
		GrainVO vo = grainDAO.getGrainVO(config);
		if(vo==null)
			return null;
		vo.setOriginalImg(convertTo8UC1(vo.getOriginalImg()));
		return vo;
	}
	
	public GrainConfig getGrainConfig(String workspace) {
		return grainDAO.getGrainConfig(workspace);
	}

	public GrainConfig getInitIfNotExistConfig(String workspace) {
		GrainConfig config = this.getGrainConfig(workspace);
		if(config!=null) return config;
		config = new GrainConfig();
		config.setWorkspace(workspace);
		config.setStatus(GrainStatus.UNSEGMENTED);
		this.saveGrainConfig(config);
		return config;
	}

	public void saveGrainConfig(GrainConfig vo) {
		if(vo.getStatus()==null) 
			vo.setStatus(GrainStatus.UNSEGMENTED);
		grainDAO.saveGrainConfig(vo);
	}
	
	public void saveImage(GrainVO vo) {
		grainDAO.saveGrainVO(vo);
	}
	
	private Mat convertTo8UC1(Mat mat) {
		if(mat==null) return mat;
		if(mat.channels()==1 && mat.depth()==CvType.CV_8UC1) return mat;
		if(mat.depth()!=CvType.CV_8U) {
			MinMaxLocResult minMax = Core.minMaxLoc(mat);
			double max = minMax.maxVal;
			double min = minMax.minVal;
			Mat m = new Mat(mat.height(), mat.width(), CvType.CV_8UC1);
			mat.convertTo(m, CvType.CV_8UC1, 256.0 / (max - min), -256.0 * min / (max - min));
			return m;
		}
		Mat m = new Mat(mat.height(), mat.width(), CvType.CV_8UC1);
		mat.convertTo(m, CvType.CV_8UC1);
		return m;
	}
}
