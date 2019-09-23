package edu.ncku.model.tempmarker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class TempMarkerDAOImplement implements TempMarkerDAO{
	private final Logger logger = LogManager.getLogger(TempMarkerDAOImplement.class.getClass());
	
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
