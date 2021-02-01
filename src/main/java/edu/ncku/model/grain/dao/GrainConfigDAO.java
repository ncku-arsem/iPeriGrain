package edu.ncku.model.grain.dao;

import edu.ncku.model.grain.vo.GrainConfig;

public interface GrainConfigDAO {
	GrainConfig getInitGrainConfig(String workspace);
	GrainConfig getGrainConfig(String workspace);
	void saveGrainConfig(GrainConfig vo);
}
