package edu.ncku.store;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static edu.ncku.store.MarkerFile.SEED_FILE_NAME;
import static edu.ncku.store.MarkerFile.SHADOW_FILE_NAME;

@Component
public class MarkerFileStore {
	private static final String STORE_FOLDER = "_store"+File.separator;
	private static final String SEED_STORE_PATTERN = "_seed%s.png";
	private static final String SHADOW_STORE_PATTERN = "_shadow%s.png";

	public File getDefaultSeedFile(File workspaceFolder) {
		return new File(workspaceFolder, SEED_FILE_NAME);
	}
	
	public File getDefaultShadowFile(File workspaceFolder) {
		return new File(workspaceFolder, SHADOW_FILE_NAME);
	}
	
	public void copyDefaultToSpecific(File workspaceFolder, int index) {
		File seedSource = getDefaultSeedFile(workspaceFolder);
		if(seedSource.canRead()) {
			try {
				File seed = getStoreFile(workspaceFolder, String.format(SEED_STORE_PATTERN, index));
				if(!seed.getParentFile().exists())
					seed.getParentFile().mkdirs();
				Files.copy(seedSource.toPath(), seed.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		File shadowSource = getDefaultShadowFile(workspaceFolder);
		if(shadowSource.canRead()) {
			try {
				File shadow = getStoreFile(workspaceFolder, String.format(SHADOW_STORE_PATTERN, index));
				if(!shadow.getParentFile().exists())
					shadow.getParentFile().mkdirs();
				Files.copy(shadowSource.toPath(), shadow.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public boolean copyIndexToDefault(File workspaceFolder, int index) {
		File seed = getStoreFile(workspaceFolder, String.format(SEED_STORE_PATTERN, index));
		File shadow = getStoreFile(workspaceFolder, String.format(SHADOW_STORE_PATTERN, index));
		if(!seed.canRead() && !shadow.canRead())
			return false;
		boolean success = false;
		try {
			if(seed.canRead()) {
				File defaultSeed = getDefaultSeedFile(workspaceFolder);
				Files.copy(seed.toPath(), defaultSeed.toPath(), StandardCopyOption.REPLACE_EXISTING);
				success = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if(shadow.canRead()) {
				File defaultShadow = getDefaultShadowFile(workspaceFolder);
				Files.copy(shadow.toPath(), defaultShadow.toPath(), StandardCopyOption.REPLACE_EXISTING);
				success = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}
	
	private File getStoreFile(File folder, String name) {
		return new File(folder, STORE_FOLDER+name);
	}
}
