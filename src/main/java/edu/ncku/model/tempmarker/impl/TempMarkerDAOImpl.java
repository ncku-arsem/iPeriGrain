package edu.ncku.model.tempmarker.impl;

import edu.ncku.model.tempmarker.vo.TempMarkerVO;
import edu.ncku.model.tempmarker.dao.TempMarkerDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class TempMarkerDAOImpl implements TempMarkerDAO {
	private final Logger logger = LogManager.getLogger(TempMarkerDAOImpl.class);
	
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

	@Override
	public boolean saveTeamMarker(String workspace, String name, TempMarkerVO vo) {
		if(vo==null || vo.getTemp()==null)
			return false;
		File f = new File(workspace + File.separator + name);
		return saveImageToFile(f, vo.getTemp());
	}

	private boolean saveImageToFile(File file, Mat mat) {
		return Imgcodecs.imwrite(file.getAbsolutePath(), mat);
	}
}
