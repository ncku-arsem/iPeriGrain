package edu.ncku.grainsizing.export;

import com.vividsolutions.jts.geom.Polygon;

public interface GrainShape {
	public Polygon getPolygon();
	public double getCenterX();
	public double getCenterY();
	public double getMajorAxis();
	public double getMinorAxis();
	public double getAngle();
}
