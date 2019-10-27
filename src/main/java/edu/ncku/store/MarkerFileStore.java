package edu.ncku.store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static edu.ncku.store.MarkerFile.*;

@Component
public class MarkerFileStore {
	private final Logger logger = LogManager.getLogger(MarkerFileStore.class);
	private static final String STORE_FOLDER = "_store"+File.separator;
	private static final String SEED_STORE_PATTERN = "_seed%s.png";
	private static final String SHADOW_STORE_PATTERN = "_shadow%s.png";
	private static final String CONFIRMED_STORE_PATTERN = "_confirmed%s.png";
	private static final String LAST_STORE_PATTERN = "_last%s.png";

	private File getDefaultSeedFile(File workspaceFolder) {
		return new File(workspaceFolder, SEED_FILE_NAME);
	}
	
	private File getDefaultShadowFile(File workspaceFolder) {
		return new File(workspaceFolder, SHADOW_FILE_NAME);
	}

	private File getDefaultConfirmedFile(File workspaceFolder) {
		return new File(workspaceFolder, CONFIRMED_FILE_NAME);
	}

	private File getDefaultLastFile(File workspaceFolder) {
		return new File(workspaceFolder, LAST_FILE_NAME);
	}
	
	public void copyDefaultToSpecific(File workspaceFolder, int index) {
		File seedSource = getDefaultSeedFile(workspaceFolder);
		copyDefaultToSpecific(seedSource, workspaceFolder, SEED_STORE_PATTERN, index);

		File shadowSource = getDefaultShadowFile(workspaceFolder);
		copyDefaultToSpecific(shadowSource, workspaceFolder, SHADOW_STORE_PATTERN, index);

		File confirmedSource = getDefaultConfirmedFile(workspaceFolder);
		copyDefaultToSpecific(confirmedSource, workspaceFolder, CONFIRMED_STORE_PATTERN, index);

		File lastSource = getDefaultLastFile(workspaceFolder);
		copyDefaultToSpecific(lastSource, workspaceFolder, LAST_STORE_PATTERN, index);
	}

	private void copyDefaultToSpecific(File source, File workspaceFolder, String pattern, int index){
		if(source.canRead()) {
			try {
				File target = getStoreFile(workspaceFolder, String.format(pattern, index));
				if(!target.getParentFile().exists())
					target.getParentFile().mkdirs();
				Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ex) {
				logger.error(ex.getMessage());
			}
		}
	}
	
	public void copyIndexToDefault(File workspaceFolder, int index) {
		File seed = getStoreFile(workspaceFolder, String.format(SEED_STORE_PATTERN, index));
		File shadow = getStoreFile(workspaceFolder, String.format(SHADOW_STORE_PATTERN, index));
		File last = getStoreFile(workspaceFolder, String.format(LAST_STORE_PATTERN, index));
		File confirmed = getStoreFile(workspaceFolder, String.format(SHADOW_STORE_PATTERN, index));
		if(!seed.canRead() && !shadow.canRead() && !last.canRead() &&!confirmed.canRead())
			return;

		copyIndexToDefault(seed, getDefaultSeedFile(workspaceFolder));
		copyIndexToDefault(shadow, getDefaultShadowFile(workspaceFolder));
		copyIndexToDefault(last, getDefaultLastFile(workspaceFolder));
		copyIndexToDefault(confirmed, getDefaultConfirmedFile(workspaceFolder));
	}

	private boolean copyIndexToDefault(File source, File target){
		try {
			if(!source.canRead())
				return false;
			Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private File getStoreFile(File folder, String name) {
		return new File(folder, STORE_FOLDER+name);
	}
}
