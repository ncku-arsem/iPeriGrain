package edu.ncku.model.tempmarker;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TempMarkerService {
	private static final String TEMP_SEED = "_seed.png";
	private static final String TEMP_SHADOW = "_shadow.png";
	
	@Autowired
	private TempMarkerDAO tempMarkerDAO;

	public TempMarkerVO getSeedMarker(String workspace) {
		TempMarkerVO vo = tempMarkerDAO.getTempMarker(workspace, TEMP_SEED);
		if(vo!=null && vo.getTemp()!=null) 
			vo.setTemp(convertToBinary(vo.getTemp()));
		return vo;
	}
	
	public TempMarkerVO getShadowMarker(String workspace) {
		TempMarkerVO vo = tempMarkerDAO.getTempMarker(workspace, TEMP_SHADOW);
		if(vo!=null && vo.getTemp()!=null) 
			vo.setTemp(convertToBinary(vo.getTemp()));
		return vo;
	}
	
	private Mat convertToBinary(Mat mat) {
		if(mat.channels()==1)
			return mat;
		Mat gray = new Mat();
		Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGRA2GRAY);
		Mat result = new Mat();
		Imgproc.threshold(gray, result, 1, 255, Imgproc.THRESH_BINARY);
		return result;
	}
}
