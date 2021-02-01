package edu.ncku.service.grain;

import edu.ncku.model.grain.vo.GrainPointVO;

import java.io.File;

public interface GrainGeoInfoService {
    GrainPointVO getOriPoint(File impPath);
}
