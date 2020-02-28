package edu.ncku.model.grainimage;

import edu.ncku.model.grainimage.vo.GrainPointVO;

import java.io.File;

public interface GrainGeoInfoService {
    GrainPointVO getOriPoint(File impPath);
}
