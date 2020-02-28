package edu.ncku.model.grainimage;

public interface GrainConfigDAO {
	GrainConfig getInitGrainConfig(String workspace);
	GrainConfig getGrainConfig(String workspace);
	void saveGrainConfig(GrainConfig vo);
}
