package edu.ncku.model.grain.dao;

import edu.ncku.model.grain.vo.GrainConfig;
import edu.ncku.model.grain.vo.GrainVO;

public interface GrainDAO {
	GrainVO getGrainVO(GrainConfig config);
	void saveGrainVO(GrainVO vo);
}
