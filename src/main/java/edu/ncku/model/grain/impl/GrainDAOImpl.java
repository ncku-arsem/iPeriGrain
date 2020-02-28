package edu.ncku.model.grain.impl;

import edu.ncku.model.grain.vo.GrainConfig;
import edu.ncku.model.grain.dao.GrainDAO;
import edu.ncku.model.grain.vo.GrainVO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class GrainDAOImpl implements GrainDAO {
	private final Logger logger = LogManager.getLogger(GrainDAOImpl.class);

	private final static String FILE_PATTERN = "%s"+File.separator+"%s";
	private final static String GRAIN_ORI = "original";
	private final static String GRAIN_ORI_8Bit = "01_original.png";
	private final static String GRAIN_SMOOTH = "02_smooth.png";
	private static final String GRAIN_NON = "03_non-grain.png";
	private static final String GRAIN_DIS_MAP = "04_distance-map.png";
	private static final String GRAIN_MARK = "05_mark.png";
	private static final String GRAIN_RESULT = "06_result.png";
	private static final String GRAIN_OVERLAY = "07_overlay.png";
	private static final String GRAIN_ELLIPSE = "08_ellipse.png";
	
	@Override
	public GrainVO getGrainVO(GrainConfig config) {
		Mat orgImg = getOriginalImageFromFile(config);
		if(orgImg==null) return null;
		GrainVO vo = new GrainVO();
		vo.setConfig(config);
		vo.setOriginalImg(orgImg);
		vo.setDisplayImg(orgImg);
		vo.setSmoothImg(getImageFromFile(config, GRAIN_SMOOTH));
		vo.setNonGrainImg(getImageFromFile(config, GRAIN_NON));
		vo.setDisMapImg(getImageFromFile(config, GRAIN_DIS_MAP));
		vo.setMarkImg(getImageFromFile(config, GRAIN_MARK));
		vo.setOriMarkImg(vo.getMarkImg());
		vo.setOverlayImg(getImageFromFile(config, GRAIN_OVERLAY));
		vo.setSegmentedImg(getImageFromFile(config, GRAIN_RESULT));
		//can't read 32S image
		//vo.setIndexImg(getImageFromFile(config, GRAIN_INDEX));
		vo.setEllipseImg(getImageFromFile(config, GRAIN_ELLIPSE));
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
		String ori = GRAIN_ORI+"."+getOriginalImageExtend(new File(cfg.getWorkspace()));
		return getImageFromFile(cfg, ori);
	}

	private String getOriginalImageExtend(File f){
		if(!f.exists() || !f.isDirectory())
			return "";
		File [] files = f.listFiles((dir, name) -> StringUtils.startsWith(name, GRAIN_ORI));
		if(files==null || files.length<1)
			return "";
		return FilenameUtils.getExtension(files[0].getPath());
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
	}
	
	private boolean saveImageToFile(Mat mat, String fileName, GrainConfig cfg) {
		if(cfg==null || StringUtils.isBlank(fileName) || mat==null) return false;
		boolean s = saveImageToFile(new File(getFileFormatString(cfg.getWorkspace(), fileName)), mat);
		return s;
	}

	private Mat getImageFromFile(GrainConfig config, String fileName) {
		File f = new File(getFileFormatString(config.getWorkspace(), fileName));
		if(!f.exists()) return null;
		return readFromFile(f);
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
}
