package edu.ncku;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

public final class Utils{
	
	public static Mat imageToMat(Image image) {
	    int width = (int) image.getWidth();
	    int height = (int) image.getHeight();
	    byte[] buffer = new byte[width * height * 4];

	    PixelReader reader = image.getPixelReader();
	    WritablePixelFormat<ByteBuffer> format = WritablePixelFormat.getByteBgraInstance();
	    reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);
	    Mat mat = new Mat(height, width, CvType.CV_8UC4);
	    mat.put(0, 0, buffer);
	    return mat;
	}
	
	public static Mat floodFill(Mat img, double x, double y){
	    Mat floodfilled = Mat.zeros(img.rows() + 2, img.cols() + 2, CvType.CV_8U);
	    Imgproc.floodFill(img, floodfilled, new Point(x, y), new Scalar(0), new Rect(2,2,1,1), new Scalar(0,0,0), new Scalar(0,0,0), 4 + (255 << 8) + Imgproc.FLOODFILL_MASK_ONLY);
	    Mat temp = new Mat();
	    Rect roi = new Rect(1, 1, img.width(), img.height());
	    floodfilled.submat(roi).copyTo(temp);
	    img = temp;
	    return img;
	}
	
	public static Image mat2Image(Mat frame){
		try{
			Image image = SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
			return image;
		}catch (Exception e){
			System.err.println("Cannot convert the Mat obejct: " + e.getMessage());
			return null;
		}
	}
	
	private static BufferedImage matToBufferedImage(Mat original){
		// init
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);
		
		if (original.channels() == 4){
			image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		}else if(original.channels() > 1){
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		}else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
		return image;
	}
}