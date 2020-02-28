package edu.ncku.model.grainimage;

import edu.ncku.model.grainimage.vo.GrainVO;

public interface GrainDAO {
	GrainVO getGrainVO(GrainConfig config);
	GrainConfig getInitGrainConfig(String workspace);
	GrainConfig getGrainConfig(String workspace);
	void saveGrainConfig(GrainConfig cfg);
	void saveGrainVO(GrainVO vo);
}
