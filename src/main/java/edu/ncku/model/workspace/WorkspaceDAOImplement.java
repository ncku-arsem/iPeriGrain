package edu.ncku.model.workspace;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Component
public class WorkspaceDAOImplement implements WorkspaceDAO{
	
	private final static String ORIGINAL = "original";

	@Override
	public boolean ceateWorkspace(String workspace) {
		File folder = new File(workspace);
		if(folder==null || !folder.isDirectory())
			return false;
		return folder.exists() ? true:folder.mkdirs();
	}

	@Override
	public boolean openWorkspace(String workspace) {
		File folder = new File(workspace);
		if(folder==null || !folder.isDirectory())
			return false;
		return folder.exists();
	}

	@Override
	public Optional<File> importImageToWorkspace(String workspace, String filePath) {
		File folder = new File(workspace);
		if (!folder.isDirectory())
			return Optional.empty();
		File src = new File(filePath);
		String extension = FilenameUtils.getExtension(filePath);
		File dst = new File(folder, ORIGINAL+"."+extension);
		try {
			FileUtils.copyFile(src, dst);
			return Optional.of(dst);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
}
