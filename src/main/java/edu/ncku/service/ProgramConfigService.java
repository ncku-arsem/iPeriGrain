package edu.ncku.service;

import edu.ncku.model.program.vo.ProgramConfigVO;

public interface ProgramConfigService {
    ProgramConfigVO getProgramConfig();
    void saveConfig(ProgramConfigVO configVO);
    ProgramConfigVO addRecentWorkspace(ProgramConfigVO configVO, String workspace);
}
