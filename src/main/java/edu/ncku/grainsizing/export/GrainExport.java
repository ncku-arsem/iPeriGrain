package edu.ncku.grainsizing.export;

import java.util.List;

public interface GrainExport {
	public void doExportGrain(List<GrainShape> grainShapes, String target);
}
