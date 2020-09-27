package edu.ncku.model.grain.vo;

public class GrainPointVO {
    private double x = 0.0;
    private double y = 0.0;
    private Double scale;

    public GrainPointVO(){

    }

    public GrainPointVO(double x, double y, Double scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Double getScale() {
        return scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }
}
