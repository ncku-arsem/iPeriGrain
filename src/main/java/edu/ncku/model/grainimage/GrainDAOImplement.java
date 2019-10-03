package edu.ncku.model.grainimage;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class GrainDAOImplement implements GrainDAO{
	private final Logger logger = LogManager.getLogger(GrainDAOImplement.class.getClass());
	
	private Gson gson = new Gson();
	private static final String GRAIN_CONFIG = "config.json";
	private final static String FILE_PATTERN = "%s"+File.separator+"%s";
	private final static String GRAIN_ORI = "original.tif";
	private final static String GRAIN_ORI_8Bit = "01_original.png";
	private final static String GRAIN_SMOOTH = "02_smooth.png";
	private static final String GRAIN_NON = "03_non-grain.png";
	private static final String GRAIN_DIS_MAP = "04_distance-map.png";
	private static final String GRAIN_MARK = "05_mark.png";
	private static final String GRAIN_RESULT = "06_result.png";
	private static final String GRAIN_OVERLAY = "07_overlay.png";
	private static final String GRAIN_ELLIPSE = "08_ellipse.png";
	private static final String GRAIN_ORI_SEGMENT = "09_ori_segmented.png";

	
	@Override
	public GrainVO getGrainVO(GrainConfig config) {
		Mat orgImg = getOriginalImageFromFile(config);
		if(orgImg==null) return null;
		GrainVO vo = new GrainVO();
		vo.setConfig(config);
		vo.setOriginalImg(orgImg);
		vo.setSmoothImg(getImageFromFile(config, GRAIN_SMOOTH));
		vo.setNonGrainImg(getImageFromFile(config, GRAIN_NON));
		vo.setDisMapImg(getImageFromFile(config, GRAIN_DIS_MAP));
		vo.setMarkImg(getImageFromFile(config, GRAIN_MARK));
		vo.setOverlayImg(getImageFromFile(config, GRAIN_OVERLAY));
		vo.setSegmentedImg(getImageFromFile(config, GRAIN_RESULT));
		//can't read 32S image
		//vo.setIndexImg(getImageFromFile(config, GRAIN_INDEX));
		vo.setEllipseImg(getImageFromFile(config, GRAIN_ELLIPSE));
		vo.setOriSegmentedImg(getImageFromFile(config, GRAIN_ORI_SEGMENT));
		if(vo.getOriginalImg()!=null) {
			config.setHeight(vo.getOriginalImg().height());
			config.setWidth(vo.getOriginalImg().width());
		}
		return vo;
	}
	
	private Mat getOriginalImageFromFile(GrainConfig cfg) {
		logger.info("getOriginalImageFromFile:{}", cfg);
		Mat m = getImageFromFile(cfg, GRAIN_ORI_8Bit);
		if(m!=null) {
			logger.info("getOriginalImageFromFile:{}", cfg);
			return m;
		}
		return getImageFromFile(cfg, GRAIN_ORI);
	}

	@Override
	public GrainConfig getGrainConfig(String workspace) {
		File configFile = getConfigFile(workspace);
		GrainConfig config = null;
		if(!configFile.exists()) {
			logger.info("getGrainConfig config file not exists.");
			return config;
		}
		FileReader fr = null;
		try {
			fr = new FileReader(configFile);
			config = gson.fromJson(fr, GrainConfig.class);
			if(config==null || StringUtils.isBlank(config.getWorkspace()))
				return null;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Can't find config file.");
		} finally {
			try {if(fr!=null) fr.close();} catch (IOException e) {}
		}
		return config;
	}

	@Override
	public void saveGrainVO(GrainVO vo) {
		logger.info("saveGrainVO");
		GrainConfig cfg = vo.getConfig();
		saveImageToFile(vo.getOriginalImg(), GRAIN_ORI_8Bit, cfg);
		saveImageToFile(vo.getSmoothImg(), GRAIN_SMOOTH, cfg);
		saveImageToFile(vo.getNonGrainImg(), GRAIN_NON, cfg);
		saveImageToFile(vo.getDisMapImg(), GRAIN_DIS_MAP, cfg);
		saveImageToFile(vo.getMarkImg(), GRAIN_MARK, cfg);
		saveImageToFile(vo.getSegmentedImg(), GRAIN_RESULT, cfg);
		saveImageToFile(vo.getOverlayImg(), GRAIN_OVERLAY, cfg);
		//can't save 32S image
		//saveImageToFile(vo.getIndexImg(), GRAIN_INDEX, cfg);
		saveImageToFile(vo.getEllipseImg(), GRAIN_ELLIPSE, cfg);
		saveImageToFile(vo.getOriSegmentedImg(), GRAIN_ORI_SEGMENT, cfg);
		saveGrainConfig(vo.getConfig());
	}
	
	private boolean saveImageToFile(Mat mat, String fileName, GrainConfig cfg) {
		if(cfg==null || StringUtils.isBlank(fileName) || mat==null) return false;
		boolean s = saveImageToFile(new File(getFileFormatString(cfg.getWorkspace(), fileName)), mat);
		return s;
	}

	@Override
	public void saveGrainConfig(GrainConfig vo) {
		File configFile = getConfigFile(vo.getWorkspace());
		FileWriter fw = null;
		try {
			if(!configFile.exists())
				configFile.createNewFile();
			fw = new FileWriter(configFile);
			fw.write(gson.toJson(vo));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {if(fw!=null) fw.close();} catch (IOException e) {}
		}
	}
	
	private Mat getImageFromFile(GrainConfig config, String fileName) {
		File f = new File(getFileFormatString(config.getWorkspace(), fileName));
		if(!f.exists()) return null;
		return readFromFile(f);
	}
	
	private File getConfigFile(String workspace) {
		File f = new File(getFileFormatString(workspace, GRAIN_CONFIG));
		return f;
	}
	
	private String getFileFormatString(String workspace, String fileName) {
		return String.format(FILE_PATTERN, workspace, fileName);
	}
	
	private Mat readFromFile(File file) {
		Mat mat = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_UNCHANGED);
		return mat;
	}
	
	private boolean saveImageToFile(File file, Mat mat) {
		return Imgcodecs.imwrite(file.getAbsolutePath(), mat);
	}

	@Override
	public GrainConfig getInitGrainConfig(String workspace) {
		GrainConfig vo = new GrainConfig();
		vo.setStatus(GrainStatus.UNSEGMENTED);
		vo.setWorkspace(workspace);
		vo.setMaxIndex(-1);
		return vo;
	}
}
