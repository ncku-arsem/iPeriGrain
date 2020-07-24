package edu.ncku.service.grain.impl;

import edu.ncku.model.grain.dao.GrainConfigDAO;
import edu.ncku.model.grain.vo.GrainConfig;
import edu.ncku.model.grain.vo.GrainPointVO;
import edu.ncku.model.grain.vo.GrainStatus;
import edu.ncku.service.grain.GrainConfigService;
import edu.ncku.service.grain.GrainGeoInfoService;
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
    public void setGrainOriPoint(GrainConfig config, Optional<File> oriImageOptional) {
        if (config.getOriPoint() != null || !oriImageOptional.isPresent())
            return;
        GrainPointVO pointVO = geoInfoService.getOriPoint(oriImageOptional.get());
        config.setOriPoint(pointVO);
        saveGrainConfig(config);
    }

    @Override
    public void saveGrainConfig(GrainConfig config) {
        configDAO.saveGrainConfig(config);
    }
}
