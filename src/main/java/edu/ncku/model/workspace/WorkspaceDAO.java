package edu.ncku.model.workspace;

public interface WorkspaceDAO {
	public boolean openWorkspace(String workspace);
	public boolean ceateWorkspace(String workspace);
	public boolean importImageToWorkspace(String workspace, String filePath);
}
