package edu.ncku.model.grainimage;

import edu.ncku.model.grainimage.vo.GrainVO;

public interface GrainDAO {
	GrainVO getGrainVO(GrainConfig config);
	void saveGrainVO(GrainVO vo);
}
