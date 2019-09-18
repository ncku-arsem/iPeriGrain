package edu.ncku.model.grainimage;

public interface GrainDAO {
	public GrainVO getGrainVO(GrainConfig config);
	public GrainConfig getInitGrainConfig(String workspace);
	public GrainConfig getGrainConfig(String workspace);
	public void saveGrainConfig(GrainConfig cfg);
	public void saveGrainVO(GrainVO vo);
}
