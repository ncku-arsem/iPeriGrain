package edu.ncku.model.grainimage;

public interface GrainDAO {
	GrainVO getGrainVO(GrainConfig config);
	GrainConfig getInitGrainConfig(String workspace);
	GrainConfig getGrainConfig(String workspace);
	void saveGrainConfig(GrainConfig cfg);
	void saveGrainVO(GrainVO vo);
}
