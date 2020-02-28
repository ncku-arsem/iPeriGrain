package edu.ncku.model.grain;

import edu.ncku.service.grain.GrainService;
import edu.ncku.model.grain.vo.GrainVO;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GrainServiceTests {
	
	@Autowired
	private GrainService grainService;
	
	private String workspace = "D:\\02_Workspace\\grainWorkspace\\";
	
	@BeforeClass
	public static void loadOpenCV() {
		nu.pattern.OpenCV.loadShared();
	}
	
	@Test
	public void testGetImage() throws Exception {
		GrainVO vo = grainService.getGrainVO(workspace);
		Assert.assertNotNull(vo);
		Assert.assertNotNull(vo.getOriginalImg());
	}

	@Test
	public void testSaveImage() throws Exception {
		GrainVO vo = grainService.getGrainVO(workspace);
		grainService.saveImage(vo);
	}
}
