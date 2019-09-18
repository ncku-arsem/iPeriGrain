package edu.ncku.model.tempmarker;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TempMarkerDAOImplement implements TempMarkerDAO{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public TempMarkerVO getTempMarker(String workspace, String name) {
		File f = new File(workspace + File.separator + name);
		if(!f.exists()) {
			logger.info("getTempMarker:{} not exist.", f.getPath());
			return null;
		}
		Mat mat = Imgcodecs.imread(f.getAbsolutePath(), Imgcodecs.IMREAD_UNCHANGED);
		if(mat==null) {
			logger.info("getTempMarker:{} read failed.", f.getPath());
			return null;
		}
		TempMarkerVO vo = new TempMarkerVO();
		vo.setTemp(mat);
		return vo;
	}
}
