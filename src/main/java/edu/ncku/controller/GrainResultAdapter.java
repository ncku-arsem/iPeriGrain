package edu.ncku.controller;

import java.util.LinkedList;
import java.util.List;

import edu.ncku.model.grain.vo.GrainPointVO;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import edu.ncku.grainsizing.export.GrainShape;
import edu.ncku.model.grain.vo.GrainResultVO;

public class GrainResultAdapter implements GrainShape{
	private static double SCALE = 1.0/100.0;
	public static void setScale(double mPerPixel){
		SCALE = mPerPixel;
	}
	private static final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
	private Polygon polygon;
	private RotatedRect ellipse;
	private double oriY;

	public GrainResultAdapter(GrainResultVO vo, GrainPointVO oriPoint) {
		if (oriPoint == null)
			oriPoint = new GrainPointVO(0.0, 0.0);
		MatOfPoint points = vo.getContour();
		Point[] pointArray = points.toArray();
		
		List<Coordinate> list = new LinkedList<Coordinate>();
		for(Point point:pointArray) 
			list.add(new Coordinate(oriPoint.getX()+(point.x + 0.5)*SCALE, oriPoint.getY()+(- 0.5 - point.y)*SCALE));
		Point firstOne = pointArray[0];
		list.add(new Coordinate(oriPoint.getX()+(firstOne.x + 0.5)*SCALE, oriPoint.getY()+(- 0.5 - firstOne.y)*SCALE));

		LinearRing shell = geometryFactory.createLinearRing(list.toArray(new Coordinate[list.size()]));
		polygon = geometryFactory.createPolygon(shell, null);
		ellipse = vo.getEllipse();
		this.oriY = oriPoint.getY();
	}
	
	@Override
	public Polygon getPolygon() {
		return polygon;
	}

	@Override
	public double getCenterX() {
		return ellipse.center.x*SCALE;
	}

	@Override
	public double getCenterY() {
		return oriY - (ellipse.center.y)*SCALE;
	}

	@Override
	public double getMajorAxis() {
		return ellipse.size.height*SCALE;
	}

	@Override
	public double getMinorAxis() {
		return ellipse.size.width*SCALE;
	}

	@Override
	public double getAngle() {
		if(ellipse.size.width < ellipse.size.height)
	        return 90.0-ellipse.angle;
	    return -ellipse.angle;
	}

}
