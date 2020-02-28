package edu.ncku.model.workspace;

import java.io.File;
import java.util.Optional;

public interface WorkspaceDAO {
	boolean openWorkspace(String workspace);
	boolean ceateWorkspace(String workspace);
	Optional<File> importImageToWorkspace(String workspace, String filePath);
}
