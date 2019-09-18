package edu.ncku.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.ncku.store.MarkerFile;
import edu.ncku.store.MarkerFileStore;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import com.gluonhq.charm.glisten.control.ToggleButtonGroup;

import edu.ncku.Utils;
import edu.ncku.canvas.DrawingAction;
import edu.ncku.canvas.PannableCanvas;
import edu.ncku.canvas.SceneGestures;
import edu.ncku.grainsizing.GrainProcessing;
import edu.ncku.grainsizing.export.GrainExport;
import edu.ncku.grainsizing.export.GrainShape;
import edu.ncku.model.grainimage.GrainConfig;
import edu.ncku.model.grainimage.GrainResultVO;
import edu.ncku.model.grainimage.GrainService;
import edu.ncku.model.grainimage.GrainVO;
import edu.ncku.model.workspace.WorkspaceService;
import edu.ncku.store.MarkerFileQueue;
import edu.ncku.service.ColorMap;
import edu.ncku.service.ColorMapper;
import edu.ncku.service.MakerRemover;
import edu.ncku.service.impl.MarkerRemoverImpl;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


@Controller
public class WorkdspaceController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final static String MARKER_SEED = "seed";
	private final static String MARKER_SHADOW = "shadow";
	private final static String MARKER_CLEAR = "clear";
	
	@FXML
	private BorderPane mainPane;
	@FXML 
	private MenuBar menuBar;
	@FXML
	private MenuItem workspaceMenuItem;
	@FXML
	private MenuItem improtMenuItem;
	@FXML 
	private BorderPane imgHolder;
	@FXML
	private ListView<GrainConfig> listView;
	@FXML
	private ToggleButton seedButton;
	@FXML
	private ToggleButton clearSeedButton;
	@FXML
	private ToggleButton trashSeedButton;
	@FXML
	private ToggleButton shadowButton;
	@FXML
	private ToggleButton clearShadowButton;
	@FXML
	private ToggleButton trashShadowButton;
	@FXML
	private ToggleButtonGroup toggleButtonGroup;
	@FXML
	private Button exportButton;
	@FXML
	private Button exportTextButton;
	@FXML
	private Button exportEllipseButton;
	@FXML
	private ChoiceBox<Integer> cacheChoiceBox;
	@FXML
	private Button restoreButton;
	@FXML
	private Button saveButton;
	@FXML
	private Label markerIndexLabel;
	@FXML
	private ChoiceBox<ColorMap> colorChoiceBox;
	@FXML
	private Button applyColorButton;
	@FXML
	private CheckBox markerCheckBox;
	@FXML
	private CheckBox segmentCheckBox;
	@FXML
	private CheckBox ellipseCheckBox;
	@FXML
	private Slider alphaSlider;
	@FXML
	private Slider betaSlider;

	@Autowired
	private WorkspaceService workspaceService;
	
	@Autowired
	private GrainService grainService;
	
	@Autowired
	private GrainProcessing grainProcessing;
	
	@Autowired
	private MakerRemover makerRemover;
	
	@Autowired
	private ColorMapper colorMapper;
	
	@Autowired
	@Qualifier("shpExport")
	private GrainExport grainExport;
	
	@Autowired
	@Qualifier("textExport")
	private GrainExport grainTextExport;
	
	@Autowired
	@Qualifier("ellipseExport")
	private GrainExport grainEllipseExport;
	
	@Autowired
	private MarkerFileQueue markerFileQueue;

	@Autowired
    private MarkerFileStore markerFileStore;
	
	private PannableCanvas canvas;
	private File workspaceFolder;
	private GrainVO grainVO;
	
	public void initialize() {
		logger.info("initialize");
		MarkerRemoverImpl markerRemover = new MarkerRemoverImpl(); 
		setToggleButton();
		canvas = new PannableCanvas(markerRemover);
		canvas.setFocusTraversable(true);
		SceneGestures sceneGestures = new SceneGestures(canvas);
		canvas.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
		canvas.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
		canvas.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
		mainPane.addEventFilter( KeyEvent.KEY_PRESSED, e->{
			if(KeyCode.F==e.getCode())
				segmentCheckBox.setSelected(!segmentCheckBox.isSelected());
		});
		markerCheckBox.selectedProperty().addListener((ov,old_val, new_val)->{
			canvas.setBothMarkerVisibleAndEnable(new_val);
		});
		segmentCheckBox.selectedProperty().addListener((ov,old_val, new_val)-> {
			canvas.setOverlayShow(new_val);
		});
		mainPane.addEventFilter( KeyEvent.KEY_PRESSED, e->{
			if(KeyCode.D==e.getCode())
				ellipseCheckBox.setSelected(!ellipseCheckBox.isSelected());
		});
		ellipseCheckBox.selectedProperty().addListener((ov,old_val, new_val)-> {
			canvas.setEllipseShow(new_val);
		});
		imgHolder.setCenter(canvas);
		exportButton.setOnAction(e->{
			FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SHP files (*.shp)", "*.shp"));
            Stage stage = (Stage) exportButton.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            if(file!=null)
            	doExport(file, grainExport);
		});
		exportTextButton.setOnAction(e->{
			FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
            Stage stage = (Stage) exportTextButton.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            if(file!=null)
            	doExport(file, grainTextExport);
		});
		exportEllipseButton.setOnAction(e->{
			FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SHP files (*.shp)", "*.shp"));
            Stage stage = (Stage) exportEllipseButton.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            if(file!=null)
            	doExport(file, grainEllipseExport);
		});
		List<Integer> cacheIndex = IntStream.range(1, 11).boxed().collect(Collectors.toList());
		cacheChoiceBox.setItems(FXCollections.observableArrayList(cacheIndex));
		cacheChoiceBox.getSelectionModel().selectFirst();
		saveButton.setOnAction(e->{
			Integer saveIndex = cacheChoiceBox.getValue();
            markerFileStore.copyDefaultToSpecific(workspaceFolder, saveIndex);
		});
		restoreButton.setOnAction(e->{
			Integer restoreIndex = cacheChoiceBox.getValue();
			if(restoreIndex==null || restoreIndex<1) {
				markerIndexLabel.setText("Result Index:");
				return;
			}
			if(!markerFileStore.copIndexToDefault(workspaceFolder, restoreIndex))
			    return;
			markerIndexLabel.setText("Result Index:"+restoreIndex);
			setMarkerImage();
		});
		alphaSlider.valueProperty().addListener((ob, oldVal, newVal)->{
			if(grainVO==null || grainVO.getConfig()==null)
				return;
			grainVO.getConfig().setAlpha(newVal.doubleValue());
			enhanceBasicImage();
		});
		betaSlider.valueProperty().addListener((ob, oldVal, newVal)->{
			if(grainVO==null || grainVO.getConfig()==null)
				return;
			grainVO.getConfig().setBeta(newVal.intValue());
			enhanceBasicImage();
		});
		
		colorChoiceBox.setItems(FXCollections.observableArrayList(ColorMap.values()));
		colorChoiceBox.getSelectionModel().selectFirst();
		applyColorButton.setOnAction(e->{
			if(grainVO==null || grainVO.getOriginalImg()==null)
				return;
			ColorMap colorMap = colorChoiceBox.getValue();
			Image img = colorMapper.convertColor(colorMap, grainVO.getOriginalImg());
			if(img!=null)
				canvas.setBasicImage(img);
		});
	}
	
	public void doReSegment() {
		doSaveCanvas();
		grainProcessing.doReSegmentGrainProcessing(grainVO);
		grainService.saveImage(grainVO);
		segmentCheckBox.setSelected(setOverlay(grainVO));
		ellipseCheckBox.setSelected(ellipseCheckBox.isSelected() && setEllipse(grainVO));
		setMarkerImage();
	}
	
	public void doSegment() {
		doSaveCanvas();
		grainVO = grainProcessing.doGrainProcessing(workspaceFolder.getAbsolutePath());
		grainService.saveImage(grainVO);
		segmentCheckBox.setSelected(setOverlay(grainVO));
		ellipseCheckBox.setSelected(setEllipse(grainVO));
		setMarkerImage();
	}

    /**it will save seed/shadow marker and add markers into queue*/
	private void doSaveCanvas() {
		File seedFile = new File(workspaceFolder, MarkerFile.SEED_FILE_NAME);
		File shadowFile = new File(workspaceFolder, MarkerFile.SHADOW_FILE_NAME);
		canvas.doSaveCanvas(seedFile, shadowFile);
		markerFileQueue.add(workspaceFolder);
	}
	
	public void importGrainImage(ActionEvent ae) {
		if(workspaceFolder==null || !workspaceFolder.isDirectory())
			return;
		workspaceMenuItem.setDisable(true);
		selectImageToImoprt();
		grainVO = grainService.getGrainVO(workspaceFolder.getAbsolutePath());
		if(grainVO==null) 
			return;
		improtMenuItem.setDisable(true);
		Image image = Utils.mat2Image(grainVO.getOriginalImg());
		canvas.initCanvas(image);
		segmentCheckBox.setSelected(setOverlay(grainVO));
		setMarkerImage();
	}
	
	public void openWorkspace(ActionEvent ae) {
		String workspace = selectWorkspace();
		if(!workspaceService.openWorkspace(workspace))
			return;
		workspaceMenuItem.setDisable(true);
		workspaceFolder = new File(workspace);
		grainVO = grainService.getGrainVO(workspace);
		if(grainVO==null) {
			improtMenuItem.setDisable(false);
			return;
		}
		improtMenuItem.setDisable(true);
		Image image = Utils.mat2Image(grainVO.getOriginalImg());
		canvas.initCanvas(image);
		segmentCheckBox.setSelected(setOverlay(grainVO));
		ellipseCheckBox.setSelected(setEllipse(grainVO));
		setMarkerImage();
	}
	
	private void setMarkerImage() {
		boolean hasSeed = setSeedCanvas();
		boolean hasShadow = setShadowCanvas();
		canvas.setBothMarkerVisibleAndEnable(hasSeed || hasShadow); 
		markerCheckBox.setSelected(hasSeed || hasShadow);
	}
	
	private void enhanceBasicImage() {
		grainVO = grainProcessing.enhaceToShow(grainVO);
		Image image = Utils.mat2Image(grainVO.getEnhanceImg());
		canvas.setBasicImage(image);
	}
	
	private void setToggleButton() {
		seedButton.setGraphic(FontIcon.of(FontAwesome.PENCIL, 20));
		seedButton.setUserData(MARKER_SEED);
		seedButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e->setDrawingAction(DrawingAction.SEED));
		clearSeedButton.setGraphic(FontIcon.of(FontAwesome.ERASER, 20));
		clearSeedButton.setUserData(MARKER_CLEAR);
		clearSeedButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e->canvas.setSeedClearDrawing());
		trashSeedButton.setGraphic(FontIcon.of(FontAwesome.TRASH, 20));
		trashSeedButton.setUserData(MARKER_SEED);
		trashSeedButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e->canvas.setSeedTrashDrawing());
		
		shadowButton.setGraphic(FontIcon.of(FontAwesome.REMOVE, 20));
		shadowButton.setUserData(MARKER_SHADOW);
		shadowButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e->setDrawingAction(DrawingAction.SHADOW));
		clearShadowButton.setGraphic(FontIcon.of(FontAwesome.ERASER, 20));
		clearShadowButton.setUserData(MARKER_CLEAR);
		clearShadowButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e->canvas.setShadowClearDrawing());
		trashShadowButton.setGraphic(FontIcon.of(FontAwesome.TRASH, 20));
		trashShadowButton.setUserData(MARKER_SEED);
		trashShadowButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e->canvas.setShadowTrashDrawing());
	}
	
	private void setDrawingAction(DrawingAction drawingAction) {
		canvas.triggerDrawingAction(drawingAction);
	}

	private void doExport(File exportFile, GrainExport grainExport) {
		List<GrainResultVO> list = grainVO.getResults();
		List<GrainShape> grainShapes = new LinkedList<>();
		for(GrainResultVO vo:list) {
			grainShapes.add(new GrainResultAdapter(vo, grainVO.getConfig().getHeight()));
		}
		grainExport.doExportGrain(grainShapes, exportFile.getAbsolutePath());
	}
	
	private boolean setOverlay(GrainVO vo) {
		if(vo.getOverlayImg()==null) 
			return false;
		Image overlay = null;
		try {
			overlay = Utils.mat2Image(vo.getOverlayImg());
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return canvas.setOverlay(overlay);
	}
	
	private boolean setEllipse(GrainVO vo) {
		if(vo.getEllpiseImg()==null) 
			return false;
		Image ellipse = null;
		try {
			ellipse = Utils.mat2Image(vo.getEllpiseImg());
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return canvas.setEllipse(ellipse);
	}
	
	private boolean setSeedCanvas() {
		File seedFile = new File(workspaceFolder, "_seed.png");
		if(seedFile.exists()) {
			try {
				canvas.setSeedImage(new Image(new FileInputStream(seedFile)));
				return true;
			} catch (FileNotFoundException e) {e.printStackTrace();}
		}else {
			canvas.clearSeedCanvas();
		}
		return false;
	}
	
	private boolean setShadowCanvas() {
		File shadowFile = new File(workspaceFolder, "_shadow.png");
		if(shadowFile.exists()) {
			try {
				canvas.setShadowImage(new Image(new FileInputStream(shadowFile)));
				return true;
			} catch (FileNotFoundException e) {e.printStackTrace();}
		}else {
			canvas.clearShadowImage();
		}
		return false;
	}

	private String selectWorkspace() {
		Stage stage = (Stage) menuBar.getScene().getWindow();
		DirectoryChooser chooser = new DirectoryChooser();
		File defaultDirectory = new File("/");
		chooser.setInitialDirectory(defaultDirectory);
		File selectedDirectory = chooser.showDialog(stage);
		return selectedDirectory==null ? "":selectedDirectory.getAbsolutePath();
	}
	
	private boolean selectImageToImoprt() {
		Stage stage = (Stage) menuBar.getScene().getWindow();
		FileChooser chooser = new FileChooser();
		File defaultDirectory = new File("/");
		chooser.setInitialDirectory(defaultDirectory);
		File imageFile = chooser.showOpenDialog(stage);
		return workspaceService.importImageToWorkspace(workspaceFolder.getAbsolutePath(), imageFile.getAbsolutePath());
	}
}