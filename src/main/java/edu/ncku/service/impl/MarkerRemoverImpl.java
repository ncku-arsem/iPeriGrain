package edu.ncku.service.impl;

import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import edu.ncku.Utils;
import edu.ncku.service.MakerRemover;
import javafx.scene.image.Image;

@Component
public class MarkerRemoverImpl implements MakerRemover{
	
	@Override
	public Image removeMaker(double x, double y, Image image) {
		Mat mat = Utils.imageToMat(image);
		Mat gray = new Mat();
		Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGRA2GRAY);
		Mat resultMat = Utils.floodFill(gray, x, y);
		Mat dst = new Mat();
		Imgproc.threshold(resultMat, dst, 1.0, 1.0, Imgproc.THRESH_BINARY_INV);
		
		ArrayList<Mat> mv = new ArrayList<>(mat.channels());
		Core.split(mat, mv);
		Mat [] resultArray = new Mat[4];
		for(int i=0;i<mv.size();i++) {
			Mat m = Mat.zeros(mat.rows(), mat.cols(), CvType.CV_8U);
			Core.multiply(mv.get(i), dst, m);
			resultArray[i] = m;
		}
		Mat [] newArray = new Mat[4];
		for(int i=0;i<resultArray.length;i++) {
			int index = i-1<0 ? resultArray.length-1:i-1;
			newArray[i] = resultArray[index];
		}
		Core.merge(Arrays.asList(newArray), mat);
		image = Utils.mat2Image(mat);
		return image;
	}
}
