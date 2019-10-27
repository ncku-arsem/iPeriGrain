package edu.ncku.model.tempmarker;

public interface TempMarkerDAO {
	TempMarkerVO getTempMarker(String workspace, String name);
	boolean saveTeamMarker(String workspace, String name, TempMarkerVO vo);
}
