package edu.ncku.model.grainimage;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GrainImageTests {
	
	@Autowired
	private GrainDAO grainDAO;
	
	private String workspace = "D:\\02_Workspace\\grainWorkspace\\";
	
	@BeforeClass
	public static void loadOpenCV() {
		nu.pattern.OpenCV.loadShared();
	}
	
	@Test
	public void testGetConfig() throws Exception {
		GrainConfig config = grainDAO.getGrainConfig(workspace);
		Assert.assertNotNull(config);
		Assert.assertTrue(Files.isSameFile(Paths.get(workspace), Paths.get(config.getWorkspace())));
		Assert.assertEquals(GrainStatus.UNSEGMENTED, config.getStatus());
	}
	
	@Test
	public void testGetImage() throws Exception {
		GrainVO vo = grainDAO.getGrainVO(grainDAO.getInitGrainConfig(workspace));
		Assert.assertNotNull(vo);
		Assert.assertNotNull(vo.getOriginalImg());
	}
}
