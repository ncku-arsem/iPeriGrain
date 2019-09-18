package edu.ncku.grainsizing;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.ncku.model.grainimage.GrainService;
import edu.ncku.model.grainimage.GrainVO;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GrainProcessingTests {
	
	@Autowired
	private GrainProcessing grainProcessing;
	
	@Autowired
	private GrainService grainService;
	
	private String workspace = "D:\\02_Workspace\\grainWorkspace1\\";
	
	@BeforeClass
	public static void loadOpenCV() {
		nu.pattern.OpenCV.loadShared();
	}
	
//	@Test
//	public void testGenerateMark() throws Exception {
//		GrainVO vo = grainProcessing.doGrainProcessing(workspace);
//		grainDAO.saveGrainVO(vo);
//	}
//	
//	@Test
//	public void testCombineMergeMat() throws Exception {
//		GrainVO vo = grainProcessing.doGrainProcessing(workspace);
//		TempMarkerVO mergeVO = tempMarkerService.getSeedMarker(workspace);
//		Mat m = grainProcessing.generateMergeMarker(vo, mergeVO);
//		File f = new File(workspace+File.separator+"_test_merge.png");
//		Imgcodecs.imwrite(f.getAbsolutePath(), m);
//	}
	
//	@Test
//	public void testCombineSplitMat() throws Exception {
//		GrainVO vo = grainProcessing.doGrainProcessing(workspace);
//		TempMarkerVO splitVO = tempMarkerService.getSplitMarker(workspace);
//		Mat m = grainProcessing.generateSplitMarker(vo, splitVO);
//		File f = new File(workspace+File.separator+"_test_split.png");
//		Imgcodecs.imwrite(f.getAbsolutePath(), m);
//	}
	
//	@Test
//	public void testReSegmentGrainProcessing() throws Exception {
//		GrainVO vo = grainProcessing.doGrainProcessing(workspace);
//		vo = grainProcessing.doReSegmentGrainProcessing(vo);
//		grainServcie.saveImage(vo);
//	}
	
	@Test
	public void testFindContours() throws Exception {
		GrainVO vo = grainProcessing.doGrainProcessing(workspace);
		grainProcessing.doFitEllipse(vo);
		Assert.assertNotNull(vo.getResults());
		Assert.assertNotEquals(0, vo.getResults().size());
		grainService.saveImage(vo);
	}
}
