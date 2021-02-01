package edu.ncku.model.grain.vo;

import org.opencv.core.MatOfPoint;
import org.opencv.core.RotatedRect;

public class GrainResultVO {
	private MatOfPoint contour;
	private RotatedRect ellipse;
	
	public MatOfPoint getContour() {
		return contour;
	}
	public void setContour(MatOfPoint contour) {
		this.contour = contour;
	}
	public RotatedRect getEllipse() {
		return ellipse;
	}
	public void setEllipse(RotatedRect ellipse) {
		this.ellipse = ellipse;
	}
}
