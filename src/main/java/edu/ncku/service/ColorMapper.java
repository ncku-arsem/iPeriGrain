package edu.ncku.service;

import org.opencv.core.Mat;

import javafx.scene.image.Image;

public interface ColorMapper {	
	public Image convertColor(ColorMap colorMap, Mat mat);
}
