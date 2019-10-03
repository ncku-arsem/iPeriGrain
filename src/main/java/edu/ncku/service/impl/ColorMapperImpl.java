package edu.ncku.service.impl;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import edu.ncku.Utils;
import edu.ncku.service.ColorMap;
import edu.ncku.service.ColorMapper;
import javafx.scene.image.Image;

@Component
public class ColorMapperImpl implements ColorMapper{
	
	@Override
	public Image convertColor(ColorMap colorMap, Mat mat) {
		if(colorMap.getColor()<0)
			return Utils.mat2Image(mat);
		Mat colorMat = new Mat();
        Imgproc.applyColorMap(mat, colorMat, colorMap.getColor());
		return Utils.mat2Image(colorMat);
	}

	@Override
	public Mat convertColorMat(ColorMap colorMap, Mat mat) {
		if(colorMap.getColor()<0)
			return mat;
		Mat colorMat = new Mat();
		Imgproc.applyColorMap(mat, colorMat, colorMap.getColor());
		return colorMat;
	}
}
