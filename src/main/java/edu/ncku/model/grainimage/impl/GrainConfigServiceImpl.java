package edu.ncku.model.grainimage.impl;

import edu.ncku.model.grainimage.*;
import edu.ncku.model.grainimage.vo.GrainPointVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public class GrainConfigServiceImpl implements GrainConfigService {

    private GrainConfigDAO configDAO;
    private GrainGeoInfoService geoInfoService;

    @Autowired
    public GrainConfigServiceImpl(GrainGeoInfoService geoInfoService, GrainConfigDAO configDAO){
        this.configDAO = configDAO;
        this.geoInfoService = geoInfoService;
    }

    @Override
    public GrainConfig getGrainConfig(String workspace) {
        return this.getInitIfNotExistConfig(workspace);
    }

    private GrainConfig getInitIfNotExistConfig(String workspace) {
        GrainConfig config = configDAO.getGrainConfig(workspace);
        if(config != null)
            return config;
        config = new GrainConfig();
        config.setWorkspace(workspace);
        config.setStatus(GrainStatus.UNSEGMENTED);
        this.saveGrainConfig(config);
        return config;
    }

    @Override
    public void setGrainOriPoint(GrainConfig grainConfig, Optional<File> oriImageOptional) {
        if (!oriImageOptional.isPresent())
            return;
        GrainPointVO pointVO = geoInfoService.getOriPoint(oriImageOptional.get());
        grainConfig.setOriPoint(pointVO);
    }

    @Override
    public void saveGrainConfig(GrainConfig config) {
        configDAO.saveGrainConfig(config);
    }
}
