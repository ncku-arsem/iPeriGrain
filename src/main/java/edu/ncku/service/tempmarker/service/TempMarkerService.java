package edu.ncku.service.tempmarker.service;

import edu.ncku.model.tempmarker.vo.TempMarkerVO;

public interface TempMarkerService {
	TempMarkerVO getSeedMarker(String workspace);
	
	TempMarkerVO getShadowMarker(String workspace);
}
