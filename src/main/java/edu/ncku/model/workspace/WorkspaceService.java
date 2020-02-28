package edu.ncku.model.workspace;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public class WorkspaceService {
	
	@Autowired
	private WorkspaceDAO workspaceDAO;
	
	public boolean createWorkspace(String workspace) {
		if(StringUtils.isBlank(workspace))
			return false;
		return workspaceDAO.ceateWorkspace(workspace);
	}
	
	public boolean openWorkspace(String workspace) {
		if(StringUtils.isBlank(workspace))
			return false;
		return workspaceDAO.openWorkspace(workspace);
	}
	
	public Optional<File> importImageToWorkspace(String workspace, String filePath) {
		return workspaceDAO.importImageToWorkspace(workspace, filePath);
	}
}
