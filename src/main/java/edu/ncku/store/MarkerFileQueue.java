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
		File seedDefault = getDefalutSeedFile(workspaceFolder);
		File shadowDefault = getDefalutShadowFile(workspaceFolder);
		boolean copySeed = copy(seedDefault, seed);
		boolean copyShadow = copy(shadowDefault, shadow);
		if(copySeed || copyShadow) {
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
		boolean copySeed = copy(seed, seedDefault);
		boolean copyShadow = copy(shadow, shadowDefault);
		return copySeed || copyShadow;
	}

	private boolean copy(File src, File dst){
		if(!src.exists())
			return false;
		try {
			Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
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
