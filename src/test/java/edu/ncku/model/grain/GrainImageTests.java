package edu.ncku.model.grain;

import edu.ncku.model.grain.dao.GrainConfigDAO;
import edu.ncku.model.grain.vo.GrainConfig;
import edu.ncku.model.grain.vo.GrainStatus;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GrainImageTests {
	
	@Autowired
	private GrainConfigDAO configDAO;
	
	private String workspace = "D:\\02_Workspace\\grainWorkspace\\";
	
	@BeforeClass
	public static void loadOpenCV() {
		nu.pattern.OpenCV.loadShared();
	}
	
	@Test
	public void testGetConfig() throws Exception {
		GrainConfig config = configDAO.getGrainConfig(workspace);
		Assert.assertNotNull(config);
		Assert.assertTrue(Files.isSameFile(Paths.get(workspace), Paths.get(config.getWorkspace())));
		Assert.assertEquals(GrainStatus.UNSEGMENTED, config.getStatus());
	}
}
