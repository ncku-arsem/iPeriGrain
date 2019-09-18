package edu.ncku.controller;

import java.util.LinkedList;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import edu.ncku.grainsizing.export.GrainShape;
import edu.ncku.model.grainimage.GrainResultVO;

public class GrainResultAdapter implements GrainShape{
	private static final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
	private Polygon polygon;
	private RotatedRect ellipse;
	private double height;
	public GrainResultAdapter(GrainResultVO vo, double height) {
		MatOfPoint points = vo.getContour();
		Point[] pointArray = points.toArray();
		
		List<Coordinate> list = new LinkedList<Coordinate>();
		for(Point point:pointArray) 
			list.add(new Coordinate(point.x, height - point.y));
		Point firstOne = pointArray[0];
		list.add(new Coordinate(firstOne.x, height - firstOne.y));
		LinearRing shell = geometryFactory.createLinearRing(list.toArray(new Coordinate[list.size()]));
		polygon = geometryFactory.createPolygon(shell, null);
		ellipse = vo.getEllipse();
		this.height = height;
	}
	
	@Override
	public Polygon getPolygon() {
		return polygon;
	}

	@Override
	public double getCenterX() {
		return ellipse.center.x;
	}

	@Override
	public double getCenterY() {
		return height - ellipse.center.y;
	}

	@Override
	public double getMajorAxis() {
		return ellipse.size.height;
	}

	@Override
	public double getMinorAxis() {
		return ellipse.size.width;
	}

	@Override
	public double getAngle() {
		if(ellipse.size.width < ellipse.size.height)
	        return 90.0-ellipse.angle;
	    return -ellipse.angle;
	}

}
