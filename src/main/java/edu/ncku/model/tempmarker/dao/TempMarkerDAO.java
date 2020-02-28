package edu.ncku.model.tempmarker.dao;

import edu.ncku.model.tempmarker.vo.TempMarkerVO;

public interface TempMarkerDAO {
	TempMarkerVO getTempMarker(String workspace, String name);
	boolean saveTeamMarker(String workspace, String name, TempMarkerVO vo);
}
