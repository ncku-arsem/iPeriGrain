package edu.ncku.grainsizing.export;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GrainExportTests {
	public static final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
	private String target = "D:\\02_Workspace\\grainWorkspace1\\test.shp";
	@Autowired
	private GrainExport grinExport;
	
	@Test
	public void testExportGrain() throws Exception {
		List<GrainShape> ls = new ArrayList<>();
		ls.add(genGrainShape());
		grinExport.doExportGrain(ls, target);
	}
	public static GrainShape genGrainShape() {
		GrainShape shape = new GrainShape() {
			@Override
			public Polygon getPolygon() {
				List<Coordinate> points = new LinkedList<Coordinate>();
				points.add(new Coordinate(0, 0));
				points.add(new Coordinate(1, 1));
				points.add(new Coordinate(1, 0));
				points.add(new Coordinate(0, 0));
				LinearRing shell = geometryFactory.createLinearRing(points.toArray(new Coordinate[points.size()]));
				Polygon polygon = geometryFactory.createPolygon(shell, null);
				return polygon;
			}
			
			@Override
			public double getMinorAxis() {
				return 0;
			}
			
			@Override
			public double getMajorAxis() {
				return 0;
			}
			
			@Override
			public double getCenterY() {
				return 0;
			}
			
			@Override
			public double getCenterX() {
				return 0;
			}
			
			@Override
			public double getAngle() {
				return 0;
			}
		};
		
		return shape;
	}
}
