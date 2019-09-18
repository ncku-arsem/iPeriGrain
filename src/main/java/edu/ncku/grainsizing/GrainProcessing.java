package edu.ncku.grainsizing;

import org.opencv.core.Mat;

import edu.ncku.model.grainimage.GrainVO;
import edu.ncku.model.tempmarker.TempMarkerVO;

public interface GrainProcessing {
	
	public GrainVO doGrainProcessing(String workspace);
	public Mat smoothGrain(GrainVO vo);
	public Mat identifyNonGrain(GrainVO vo);
	public Mat generateDistanceMap(GrainVO vo);
	public Mat generateMarker(GrainVO vo);
	public Mat segmentGrain(GrainVO vo, TempMarkerVO shadowVO);
	public GrainVO doReSegmentGrainProcessing(GrainVO vo);
	public GrainVO doFitEllipse(GrainVO vo);
	public void findGrainContours(GrainVO vo);
	public void fitEllipse(GrainVO vo);
	public GrainVO enhaceToShow(GrainVO vo);
	/**
	 * Called by doReSegmentGrainProcessing
	 * @param vo
	 * @param splitVO
	 * @return 
	 */
	public Mat generateSplitMarker(GrainVO vo, TempMarkerVO splitVO);
	/**
	 * Called by doReSegmentGrainProcessing
	 * @param vo
	 * @param mergeVO
	 * @return
	 */
	public Mat generateMergeMarker(GrainVO vo, TempMarkerVO mergeVO);
}