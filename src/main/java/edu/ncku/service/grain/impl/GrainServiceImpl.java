package edu.ncku.service.grain.impl;

import edu.ncku.model.grain.dao.GrainDAO;
import edu.ncku.model.grain.vo.GrainConfig;
import edu.ncku.model.grain.vo.GrainVO;
import edu.ncku.service.grain.GrainConfigService;
import edu.ncku.service.grain.GrainGeoInfoService;
import edu.ncku.service.grain.GrainService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrainServiceImpl implements GrainService {
	private final Logger logger = LogManager.getLogger(GrainServiceImpl.class);
	private GrainGeoInfoService grainGeoInfoService;
	private GrainConfigService configService;
	private GrainDAO grainDAO;

	@Autowired
	public GrainServiceImpl(GrainGeoInfoService grainGeoInfoService, GrainDAO grainDAO, GrainConfigService configService){
		this.grainDAO = grainDAO;
		this.configService = configService;
		this.grainGeoInfoService = grainGeoInfoService;
	}
	
	public GrainVO getGrainVO(String workspace) {
		logger.info("getGrainVO:{}", workspace);
		GrainConfig config = configService.getGrainConfig(workspace);
		logger.info("getGrainVO config:{}", config);
		GrainVO vo = grainDAO.getGrainVO(config);
		if(vo == null)
			return null;
		vo.setOriginalImg(convertTo8UC1(vo.getOriginalImg()));
		return vo;
	}
	
	public void saveImage(GrainVO vo) {
		grainDAO.saveGrainVO(vo);
	}
	
	private Mat convertTo8UC1(Mat mat) {
		if(mat==null) return mat;
		if(mat.channels()==1 && mat.depth()==CvType.CV_8UC1) return mat;
		if(mat.depth()!=CvType.CV_8U) {
			MinMaxLocResult minMax = Core.minMaxLoc(mat);
			double max = minMax.maxVal;
			double min = minMax.minVal;
			Mat m = new Mat(mat.height(), mat.width(), CvType.CV_8UC1);
			mat.convertTo(m, CvType.CV_8UC1, 256.0 / (max - min), -256.0 * min / (max - min));
			return m;
		}
		Mat m = new Mat(mat.height(), mat.width(), CvType.CV_8UC1);
		mat.convertTo(m, CvType.CV_8UC1);
		return m;
	}
}
