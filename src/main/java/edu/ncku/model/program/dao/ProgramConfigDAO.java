package edu.ncku.model.program.dao;

import edu.ncku.model.program.vo.ProgramConfigVO;

public interface ProgramConfigDAO {
    ProgramConfigVO getProgramConfig();
    void saveConfig(ProgramConfigVO configVO);
}
