package edu.ncku.model.grainimage;

import java.io.File;
import java.util.Optional;

public interface GrainConfigService {
    GrainConfig getGrainConfig(String workspace);
    void setGrainOriPoint(GrainConfig grainConfig, Optional<File> oriImageOptional);
    void saveGrainConfig(GrainConfig config);
}
