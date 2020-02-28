package edu.ncku.service.grain;

import edu.ncku.model.grain.vo.GrainVO;

public interface GrainService {
	GrainVO getGrainVO(String workspace);
	void saveImage(GrainVO vo);
}
