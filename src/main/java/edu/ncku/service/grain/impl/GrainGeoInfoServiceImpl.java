package edu.ncku.service.grain.impl;

import com.google.gson.Gson;
import edu.ncku.model.grain.vo.GrainPointVO;
import edu.ncku.service.grain.GrainGeoInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;

@Service
public class GrainGeoInfoServiceImpl implements GrainGeoInfoService {
    private static final String COMMAND_PATTERN = "java -jar %s %s %s";
    private Gson gson = new Gson();
    private String jarFolder;
    private String jarName;

    private final Logger logger = LogManager.getLogger(GrainGeoInfoServiceImpl.class);

    @Autowired
    public GrainGeoInfoServiceImpl(@Value("${geoinfo.tiff.jar}") String jarName, @Value("${geoinfo.tiff.folder}") String folder){
        this.jarFolder = folder;
        this.jarName = jarName;
    }

    public GrainPointVO getOriPoint(File imgPath) {
        if (!imgPath.canRead() || imgPath.isDirectory())
            return new GrainPointVO(0.0, 0.0, 0.01);
        if (!StringUtils.startsWith(FileUtils.getFileExtension(imgPath), "tif")) {
            return new GrainPointVO(0.0, 0.0, 0.01);
        }
        File resultFile = new File("result.json");

        if (resultFile.exists() && !resultFile.delete())
            return new GrainPointVO(0.0, 0.0, 0.01);
        try {
            String jarPath = System.getProperty("user.dir")+File.separator+jarFolder+File.separator+jarName;
            String command = String.format(COMMAND_PATTERN, jarPath, imgPath.getAbsolutePath(), resultFile.getAbsolutePath());
            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
            InputStream err = proc.getErrorStream();
            if (err!=null && err.available() > 0) {
                byte b[] = new byte[err.available()];
                err.read(b, 0, b.length);
                logger.error(new String(b));
            }else {
                try (FileReader fileReader = new FileReader(resultFile)) {
                    return gson.fromJson(fileReader, GrainPointVO.class);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return new GrainPointVO(0.0, 0.0, 0.01);
    }
}
