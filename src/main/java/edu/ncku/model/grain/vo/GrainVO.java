package edu.ncku.model.grain.vo;

import java.util.List;

import org.opencv.core.Mat;

public class GrainVO {
	private GrainConfig config = new GrainConfig();
	private Mat originalImg;
	private Mat displayImg;
	private Mat enhanceImg;
	private Mat smoothImg;
	private Mat nonGrainImg;
	private Mat disMapImg;
	private Mat markImg;
	private Mat oriMarkImg;
	private Mat segmentedImg;
	private Mat indexImg;
	private Mat overlayImg;
	private Mat ellipseImg;
	
	private List<GrainResultVO> results;
	public Mat getOverlayImg() {
		return overlayImg;
	}
	public void setOverlayImg(Mat overlayImg) {
		this.overlayImg = overlayImg;
	}
	public GrainConfig getConfig() {
		return config;
	}
	public void setConfig(GrainConfig config) {
		this.config = config;
	}
	public Mat getOriginalImg() {
		return originalImg;
	}
	public void setOriginalImg(Mat originalImg) {
		this.disMapImg = originalImg;
		this.originalImg = originalImg;
	}
	public void setDisplayImg(Mat displayImg){
		this.displayImg = displayImg;
	}
	public Mat getDisplayImg(){
		return displayImg;
	}
	public Mat getSmoothImg() {
		return smoothImg;
	}
	public void setSmoothImg(Mat smoothImg) {
		this.smoothImg = smoothImg;
	}
	public Mat getNonGrainImg() {
		return nonGrainImg;
	}
	public void setNonGrainImg(Mat nonGrainImg) {
		this.nonGrainImg = nonGrainImg;
	}
	public Mat getDisMapImg() {
		return disMapImg;
	}
	public void setDisMapImg(Mat disMapImg) {
		this.disMapImg = disMapImg;
	}
	public Mat getMarkImg() {
		return markImg;
	}
	public void setMarkImg(Mat markImg) {
		this.markImg = markImg;
	}
	public Mat getSegmentedImg() {
		return segmentedImg;
	}
	public void setSegmentedImg(Mat segmentedImg) {
		this.segmentedImg = segmentedImg;
	}
	public List<GrainResultVO> getResults() {
		return results;
	}
	public void setResults(List<GrainResultVO> results) {
		this.results = results;
	}
	public Mat getIndexImg() {
		return indexImg;
	}
	public void setIndexImg(Mat indexImg) {
		this.indexImg = indexImg;
	}
	public Mat getEllipseImg() {
		return ellipseImg;
	}
	public void setEllipseImg(Mat ellipseImg) {
		this.ellipseImg = ellipseImg;
	}
	public Mat getEnhanceImg() {
		return enhanceImg;
	}
	public void setEnhanceImg(Mat enhanceImg) {
		this.enhanceImg = enhanceImg;
	}

	public Mat getOriMarkImg() {
		return oriMarkImg;
	}

	public void setOriMarkImg(Mat oriMarkImg) {
		this.oriMarkImg = oriMarkImg;
	}
}
