package edu.ncku.grainsizing.export.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ncku.grainsizing.export.GrainExport;
import edu.ncku.grainsizing.export.GrainShape;
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

import com.vividsolutions.jts.geom.Polygon;

@Component("shpExport")
public class GrainExportImpl implements GrainExport {
	
    private static SimpleFeatureType createFeatureType() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Grains");
        //builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system
        builder.add("shape", Polygon.class);
        builder.add("centerX", String.class);
        builder.add("centerY", String.class);
        builder.add("major axis", Double.class);
        builder.add("minor axis", Double.class);
        builder.add("angle", Double.class);
        // build the type
        final SimpleFeatureType GRAINS = builder.buildFeatureType();
        return GRAINS;
    }

	@Override
	public void doExportGrain(List<GrainShape> grainsShapes, String target) {
		SimpleFeatureCollection collection = DefaultFeatureCollections.newCollection();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(createFeatureType());
		for(GrainShape shape:grainsShapes) {
			featureBuilder.add(shape.getPolygon());
			featureBuilder.add(shape.getCenterX());
			featureBuilder.add(shape.getCenterY());
			featureBuilder.add(shape.getMajorAxis());
			featureBuilder.add(shape.getMinorAxis());
			featureBuilder.add(shape.getAngle());
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
			try { transaction.rollback(); } catch (IOException ex) { }
			throw new RuntimeException("transaction problem:"+e.getMessage());
		}finally {
			try {transaction.close();} catch (IOException e) {}
		}
	}

}
