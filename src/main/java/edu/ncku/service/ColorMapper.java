package edu.ncku.service;

import javafx.scene.image.Image;
import org.opencv.core.Mat;

public interface ColorMapper {	
	Image convertColor(ColorMap colorMap, Mat mat);
	Mat convertColorMat(ColorMap colorMap, Mat mat);
}
