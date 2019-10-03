package edu.ncku.service;

import org.opencv.core.Mat;

import javafx.scene.image.Image;

public interface ColorMapper {	
	Image convertColor(ColorMap colorMap, Mat mat);
	Mat convertColorMat(ColorMap colorMap, Mat mat);
}
