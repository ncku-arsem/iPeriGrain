package edu.ncku.grainsizing.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;


@Component("textExport")
public class GrainExportTextImplement implements GrainExport{
	
	private static final String PATTERN = "%s, %s, %s, %s, %s";

	@Override
	@SuppressWarnings("resource")
	public void doExportGrain(List<GrainShape> grainsShapes,String target) {
		List<String> exportList = new ArrayList<>(grainsShapes.size());
		for(GrainShape s:grainsShapes) {
			exportList.add(String.format(PATTERN, s.getMajorAxis(), s.getMinorAxis(), s.getCenterX(), s.getCenterY(), s.getAngle()));
		}
		FileOutputStream out = null;
    	try {
    		out = new FileOutputStream(new File(target));
    		BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(out));
    		for(String s:exportList) {
    			buff.write(s);
    			buff.newLine();
    		}
		} catch (IOException e) {
			throw new RuntimeException("export file failed:"+e.getMessage());
		}finally {
			if(out!=null)
				try {out.close();} catch (IOException e) {}
		}
	}

}
