package edu.ncku.model.grain.impl;

import com.google.gson.Gson;
import edu.ncku.model.grain.dao.GrainConfigDAO;
import edu.ncku.model.grain.vo.GrainConfig;
import edu.ncku.model.grain.vo.GrainStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class GrainConfigDAOImpl implements GrainConfigDAO {
	private final Logger logger = LogManager.getLogger(GrainConfigDAOImpl.class);
	private static final String GRAIN_CONFIG = "config.json";
	private final static String FILE_PATTERN = "%s"+File.separator+"%s";
	private Gson gson = new Gson();

	@Override
	public GrainConfig getInitGrainConfig(String workspace) {
		GrainConfig vo = new GrainConfig();
		vo.setStatus(GrainStatus.UNSEGMENTED);
		vo.setWorkspace(workspace);
		vo.setMaxIndex(-1);
		return vo;
	}

	@Override
	public GrainConfig getGrainConfig(String workspace) {
		File configFile = getConfigFile(workspace);
		GrainConfig config;
		if(!configFile.exists()) {
			logger.info("getGrainConfig config file not exists.");
			return null;
		}
		try (FileReader fr = new FileReader(configFile)) {
			config = gson.fromJson(fr, GrainConfig.class);
			if (config == null || StringUtils.isBlank(config.getWorkspace()))
				return null;
			config.setWorkspace(workspace);
		} catch (IOException e) {
			throw new RuntimeException("Can't read config file.");
		}
		return config;
	}

	@Override
	public void saveGrainConfig(GrainConfig vo) {
		File configFile = getConfigFile(vo.getWorkspace());
		FileWriter fw = null;
		try {
			if(!configFile.exists())
				configFile.createNewFile();
			fw = new FileWriter(configFile);
			fw.write(gson.toJson(vo));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {if(fw!=null) fw.close();} catch (IOException ignored) {}
		}
	}

	private File getConfigFile(String workspace) {
		return new File(getFileFormatString(workspace, GRAIN_CONFIG));
	}

	private String getFileFormatString(String workspace, String fileName) {
		return String.format(FILE_PATTERN, workspace, fileName);
	}
}
