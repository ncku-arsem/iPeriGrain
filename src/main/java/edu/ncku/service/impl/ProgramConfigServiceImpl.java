package edu.ncku.service.impl;

import edu.ncku.model.program.dao.ProgramConfigDAO;
import edu.ncku.model.program.vo.ProgramConfigVO;
import edu.ncku.service.ProgramConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashSet;

@Service
public class ProgramConfigServiceImpl implements ProgramConfigService {
    private ProgramConfigDAO programConfigDAO;

    @Autowired
    public ProgramConfigServiceImpl(ProgramConfigDAO programConfigDAO){
        this.programConfigDAO = programConfigDAO;
    }

    @Override
    public ProgramConfigVO getProgramConfig() {
        ProgramConfigVO configVO = programConfigDAO.getProgramConfig();
        if (CollectionUtils.isEmpty(configVO.getResentFiles()))
            configVO.setResentFiles(new LinkedHashSet<>());
        return configVO;
    }

    @Override
    public void saveConfig(ProgramConfigVO configVO) {
        programConfigDAO.saveConfig(configVO);
    }

    @Override
    public ProgramConfigVO addRecentWorkspace(ProgramConfigVO configVO, String workspace) {
        if (configVO == null)
            return configVO;
        if (CollectionUtils.isEmpty(configVO.getResentFiles()))
            configVO.setResentFiles(new LinkedHashSet<>());
        if (!(new File(workspace)).isDirectory())
            return configVO;
        configVO.getResentFiles().remove(workspace);
        configVO.getResentFiles().add(workspace);
        if (configVO.getResentFiles().size() > 3) {
            Iterator<String> iterator = configVO.getResentFiles().iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
        return configVO;
    }
}
