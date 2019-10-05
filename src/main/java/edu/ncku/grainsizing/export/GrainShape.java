package edu.ncku.grainsizing.export;

import com.vividsolutions.jts.geom.Polygon;

public interface GrainShape {
	Polygon getPolygon();
	double getCenterX();
	double getCenterY();
	double getMajorAxis();
	double getMinorAxis();
	double getAngle();
}
