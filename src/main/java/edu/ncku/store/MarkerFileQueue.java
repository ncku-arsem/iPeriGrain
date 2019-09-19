package edu.ncku.store;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import static edu.ncku.store.MarkerFile.SEED_FILE_NAME;
import static edu.ncku.store.MarkerFile.SHADOW_FILE_NAME;

@Component
public class MarkerFileQueue {
	private static final String TEMP_FOLDER = "_tmp"+File.separator;
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMddHHmmss");
	private static final String SEED_PATTERN = "_seed%s.png";
	private static final String SHADOW_PATTERN = "_shadow%s.png";
	private int capacity = 30;
	private NextPreviousList<String> nextPreviousList = new NextPreviousList<>(capacity);

	public File getDefalutSeedFile(File workspaceFolder) {
		return new File(workspaceFolder, SEED_FILE_NAME);
	}
	
	public File getDefalutShadowFile(File workspaceFolder) {
		return new File(workspaceFolder, SHADOW_FILE_NAME);
	}

	public void clearTemp(File workspaceFolder){
		File folder = new File(workspaceFolder, TEMP_FOLDER);
		if(!folder.exists()){
			folder.mkdir();
			return;
		}
		try {
			FileUtils.cleanDirectory(folder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void add(File workspaceFolder) {
		String index = LocalDateTime.now().format(dateTimeFormatter);
		File seed = getTmpFile(workspaceFolder, String.format(SEED_PATTERN, index));
		File shadow = getTmpFile(workspaceFolder, String.format(SHADOW_PATTERN, index));
		File seedDefault = getDefalutSeedFile(workspaceFolder);
		File shadowDefault = getDefalutShadowFile(workspaceFolder);
		if(copy(seedDefault, seed) || copy(shadowDefault, shadow)) {
			if(nextPreviousList.isFull()) {
				Optional<String> removeOptional = nextPreviousList.getLast();
				if(removeOptional.isPresent())
					removeFile(workspaceFolder, removeOptional.get());
			}
			nextPreviousList.add(index);
		}
	}

	public boolean restorePrevious(File workspaceFolder){
		Optional<String> index = nextPreviousList.previous();
		if(index.isPresent())
			return restoreFromIndex(workspaceFolder, index.get());
		return false;
	}

	public boolean restoreNext(File workspaceFolder){
		Optional<String> index = nextPreviousList.next();
		if(index.isPresent())
			return restoreFromIndex(workspaceFolder, index.get());
		return false;
	}

	private boolean restoreFromIndex(File workspaceFolder, String index){
		File seed = getTmpFile(workspaceFolder, String.format(SEED_PATTERN, index));
		File shadow = getTmpFile(workspaceFolder, String.format(SHADOW_PATTERN, index));
		File seedDefault = getDefalutSeedFile(workspaceFolder);
		File shadowDefault = getDefalutShadowFile(workspaceFolder);
		return copy(seed, seedDefault) || copy(shadow, shadowDefault);
	}

	private boolean copy(File src, File dst){
		try {
			Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void removeFile(File folder, String name){
		File f = getTmpFile(folder, name);
		f.delete();
	}
	
	private File getTmpFile(File folder, String name) {
		return new File(folder, TEMP_FOLDER+name);
	}
}
