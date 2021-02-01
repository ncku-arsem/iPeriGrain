package edu.ncku.grainsizing.export;

import java.util.List;

public interface GrainExport {
	void doExportGrain(List<GrainShape> grainShapes, String target);
}
