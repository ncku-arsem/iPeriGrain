package edu.ncku.grainsizing;

public class GrainParam {
    private int cannyMinThreshold;
    private int cannyMaxThreshold;

    public void setCannyMaxThreshold(int cannyMaxThreshold) {
        this.cannyMaxThreshold = cannyMaxThreshold;
    }

    public void setCannyMinThreshold(int cannyMinThreshold) {
        this.cannyMinThreshold = cannyMinThreshold;
    }

    public int getCannyMaxThreshold() {
        return cannyMaxThreshold;
    }

    public int getCannyMinThreshold() {
        return cannyMinThreshold;
    }
}
