package edu.ncku.model.program.impl;

import com.google.gson.Gson;
import edu.ncku.model.program.dao.ProgramConfigDAO;
import edu.ncku.model.program.vo.ProgramConfigVO;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;

@Service
public class ProgramConfigDAOImpl implements ProgramConfigDAO {
    private final Logger logger = LogManager.getLogger(ProgramConfigDAOImpl.class);
    private static final String PROGRAM_CONFIG = "config.json";
    private final static String FILE_PATTERN = "%s"+File.separator+"%s";
    private Gson gson = new Gson();

    @Override
    public ProgramConfigVO getProgramConfig(){
        File currentFolder = new File(System.getProperty("user.dir"));
        if (!currentFolder.exists() || !currentFolder.isDirectory())
            return new ProgramConfigVO(new LinkedHashSet<>());
        File configFile = new File(getFileFormatString(currentFolder.getAbsolutePath(), PROGRAM_CONFIG));
        try (FileReader fr = new FileReader(configFile)) {
            ProgramConfigVO configVO = gson.fromJson(fr, ProgramConfigVO.class);
            if (configVO != null)
                return configVO;
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return new ProgramConfigVO(new LinkedHashSet<>());
    }

    @Override
    public void saveConfig(ProgramConfigVO configVO) {
        File currentFolder = new File(".");
        if (!currentFolder.exists() || !currentFolder.isDirectory())
            return;
        File configFile = new File(getFileFormatString(currentFolder.getAbsolutePath(), PROGRAM_CONFIG));
        FileWriter fw = null;
        try {
            if(!configFile.exists())
                configFile.createNewFile();
            fw = new FileWriter(configFile);
            fw.write(gson.toJson(configVO));
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {if(fw!=null) fw.close();} catch (IOException ignored) {}
        }
    }

    private String getFileFormatString(String workspace, String fileName) {
        return String.format(FILE_PATTERN, workspace, fileName);
    }
}
