package edu.ncku.model.tempmarker;

import org.opencv.core.Mat;

public class TempMarkerVO {
	public TempMarkerVO(){}
	public TempMarkerVO(Mat temp){
		this.temp = temp;
	}
	private Mat temp;
	public Mat getTemp() {
		return temp;
	}

	public void setTemp(Mat temp) {
		this.temp = temp;
	}
}
