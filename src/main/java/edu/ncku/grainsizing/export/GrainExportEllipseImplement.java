package edu.ncku.grainsizing.export;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.util.GeometricShapeFactory;

@Component("ellipseExport")
public class GrainExportEllipseImplement implements GrainExport{
	
	private static final double PI = 3.14159265359;
	
    private static SimpleFeatureType createFeatureType() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Grains");
        builder.add("shape", Polygon.class);
        final SimpleFeatureType GRAINS = builder.buildFeatureType();
        return GRAINS;
    }

	@Override
	public void doExportGrain(List<GrainShape> grainsShapes,String target) {
		SimpleFeatureCollection collection = DefaultFeatureCollections.newCollection();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(createFeatureType());
		for(GrainShape shape:grainsShapes) {
			GeometricShapeFactory gsf = new GeometricShapeFactory();
		    gsf.setCentre(new Coordinate(shape.getCenterX(), shape.getCenterY()));
		    gsf.setWidth(shape.getMajorAxis());
		    gsf.setHeight(shape.getMinorAxis());
		    gsf.setNumPoints(30);
		    Polygon ellipse = gsf.createCircle();
		    AffineTransformation trans = AffineTransformation.rotationInstance(shape.getAngle()*PI/180.0, shape.getCenterX(), shape.getCenterY());
		    ellipse.apply(trans);
			featureBuilder.add(ellipse);
            collection.add(featureBuilder.buildFeature(null));
		}
		File newFile = new File(target);

    	Map<String, Serializable> params = new HashMap<String, Serializable>();
		try {
			params.put("url", newFile.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new RuntimeException("newFile problem:"+e.getMessage());
		}
		params.put("create spatial index", Boolean.TRUE);

		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
    	ShapefileDataStore newDataStore = null;
		try {
			newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
			newDataStore.createSchema(createFeatureType());
		} catch (IOException e) {
			throw new RuntimeException("createNewDataStore problem:"+e.getMessage());
		}
		
        Transaction transaction = new DefaultTransaction("create");
		try {
			String typeName = newDataStore.getTypeNames()[0];
			SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
			featureStore.setTransaction(transaction);
			featureStore.addFeatures(collection);
			transaction.commit();
		} catch (IOException e) {
			throw new RuntimeException("transaction problem:"+e.getMessage());
		}finally {
			try {transaction.rollback();} catch (IOException e) {}
		}
	}

}
