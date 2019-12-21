package edu.ncku.grainsizing;

import org.opencv.core.Mat;

import edu.ncku.model.grainimage.GrainVO;
import edu.ncku.model.tempmarker.TempMarkerVO;

public interface GrainProcessing {
	
	GrainVO doGrainProcessing(String workspace, GrainParam param);
	Mat smoothGrain(GrainVO vo);
	Mat identifyNonGrain(GrainVO vo, GrainParam grainParam);
	Mat generateDistanceMap(GrainVO vo);
	Mat generateMarker(GrainVO vo);
	Mat segmentGrain(GrainVO vo, TempMarkerVO shadowVO);
	GrainVO doReSegmentGrainProcessing(GrainVO vo);
	GrainVO doFitEllipse(GrainVO vo);
	void findGrainContours(GrainVO vo);
	void fitEllipse(GrainVO vo);
	GrainVO enhanceToShow(GrainVO vo);
	/**
	 * Called by doReSegmentGrainProcessing
	 * @param vo
	 * @param splitVO
	 * @return 
	 */
	Mat generateSplitMarker(GrainVO vo, TempMarkerVO splitVO);
	/**
	 * Called by doReSegmentGrainProcessing
	 * @param vo
	 * @param mergeVO
	 * @return
	 */
	Mat generateMergeMarker(GrainVO vo, TempMarkerVO mergeVO);
}