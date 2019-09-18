package edu.ncku.model.grainimage;

public class GrainConfig {
	private String workspace;
	private GrainStatus status = GrainStatus.UNSEGMENTED;
	private int maxIndex;
	private double width;
	private double height;
	private double alpha = 1.0;
	private int beta = 0;
	public String getWorkspace() {
		return workspace;
	}
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}
	public GrainStatus getStatus() {
		return status;
	}
	public void setStatus(GrainStatus status) {
		this.status = status;
	}
	public int getMaxIndex() {
		return maxIndex;
	}
	public void setMaxIndex(int maxIndex) {
		this.maxIndex = maxIndex;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public double getAlpha() {
		return alpha;
	}
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	public int getBeta() {
		return beta;
	}
	public void setBeta(int beta) {
		this.beta = beta;
	}
	@Override
	public String toString() {
		return "GrainConfig [workspace=" + workspace + ", status=" + status + ", maxIndex=" + maxIndex + "]";
	}
}
