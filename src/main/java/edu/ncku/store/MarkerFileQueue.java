package edu.ncku.store;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static edu.ncku.store.MarkerFile.SEED_FILE_NAME;
import static edu.ncku.store.MarkerFile.SHADOW_FILE_NAME;

@Component
public class MarkerFileQueue {
	private final Logger logger = LogManager.getLogger(MarkerFileQueue.class);

	private static final String TEMP_FOLDER = "_tmp"+File.separator;
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMddHHmmss");
	private static final String SEED_PATTERN = "_seed%s.png";
	private static final String SHADOW_PATTERN = "_shadow%s.png";
	private int capacity = 30;
	private NextPreviousList<String> nextPreviousList = new NextPreviousList<>(capacity);

	private File getDefaultSeedFile(File workspaceFolder) {
		return new File(workspaceFolder, SEED_FILE_NAME);
	}

	private File getDefaultShadowFile(File workspaceFolder) {
		return new File(workspaceFolder, SHADOW_FILE_NAME);
	}

	public void clearTemp(File workspaceFolder){
		File folder = new File(workspaceFolder, TEMP_FOLDER);
		if(!folder.exists())
			return;
		try {
			FileUtils.cleanDirectory(folder);
			add(workspaceFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean hasNext(){
		return nextPreviousList.hasNext();
	}

	public boolean hasPrevious(){
		return nextPreviousList.hasPrevious();
	}
	
	public void add(File workspaceFolder) {
		checkAndCreateFolder(workspaceFolder);
		String index = LocalDateTime.now().format(dateTimeFormatter);
		File seed = getTmpFile(workspaceFolder, String.format(SEED_PATTERN, index));
		File shadow = getTmpFile(workspaceFolder, String.format(SHADOW_PATTERN, index));

		boolean copySeed = copy(getDefaultSeedFile(workspaceFolder), seed);
		boolean copyShadow = copy(getDefaultShadowFile(workspaceFolder), shadow);

		if(copySeed || copyShadow) {
			if(nextPreviousList.isFull()) {
				Optional<String> removeOptional = nextPreviousList.getLast();
				removeOptional.ifPresent(s -> removeFile(workspaceFolder, s));
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

		boolean copySeed = copy(seed, getDefaultSeedFile(workspaceFolder));
		boolean copyShadow = copy(shadow, getDefaultShadowFile(workspaceFolder));
		return copySeed || copyShadow;
	}

	private boolean copy(File src, File dst){
		if(!src.exists())
			return false;
		try {
			Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	private void checkAndCreateFolder(File folder){
		File f = new File(folder, TEMP_FOLDER);
		if(!f.exists())
			f.mkdirs();
	}

	private void removeFile(File folder, String name){
		File f = getTmpFile(folder, name);
		f.delete();
	}
	
	private File getTmpFile(File folder, String name) {
		return new File(folder, TEMP_FOLDER+name);
	}
}
