package edu.ncku.service.grain;

import edu.ncku.model.grain.vo.GrainVO;

import java.io.File;
import java.util.Optional;

public interface GrainService {
	GrainVO getGrainVO(String workspace, Optional<File> imgOptional);
	void saveImage(GrainVO vo);
}
