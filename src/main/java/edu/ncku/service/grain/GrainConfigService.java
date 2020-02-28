package edu.ncku.service.grain;

import edu.ncku.model.grain.vo.GrainConfig;

import java.io.File;
import java.util.Optional;

public interface GrainConfigService {
    GrainConfig getGrainConfig(String workspace);
    void setGrainOriPoint(GrainConfig grainConfig, Optional<File> oriImageOptional);
    void saveGrainConfig(GrainConfig config);
}
