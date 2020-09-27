package edu.ncku.controller;

import edu.ncku.Utils;
import edu.ncku.canvas.DrawingAction;
import edu.ncku.canvas.PannableCanvas;
import edu.ncku.canvas.SceneGestures;
import edu.ncku.grainsizing.GrainParam;
import edu.ncku.grainsizing.GrainProcessing;
import edu.ncku.grainsizing.export.GrainExport;
import edu.ncku.grainsizing.export.GrainShape;
import edu.ncku.model.grain.vo.GrainResultVO;
import edu.ncku.model.grain.vo.GrainVO;
import edu.ncku.model.program.vo.ProgramConfigVO;
import edu.ncku.model.workspace.WorkspaceService;
import edu.ncku.service.ColorMap;
import edu.ncku.service.ColorMapper;
import edu.ncku.service.MarkerRemover;
import edu.ncku.service.ProgramConfigService;
import edu.ncku.service.grain.GrainService;
import edu.ncku.store.MarkerFile;
import edu.ncku.store.MarkerFileQueue;
import edu.ncku.store.MarkerFileStore;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class WorkSpaceController {
	private final Logger logger = LogManager.getLogger(WorkSpaceController.class);
	@FXML
	private BorderPane mainPane;
	@FXML 
	private MenuBar menuBar;
	@FXML
    private Menu recentMenu;
	@FXML
	private MenuItem workspaceMenuItem;
	@FXML
	private MenuItem improtMenuItem;
	@FXML 
	private BorderPane imgHolder;
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
	private Button exportButton;
	@FXML
	private Button fitEllipseButton;
	@FXML
	private Button closeEllipseButton;
	@FXML
	private ChoiceBox<Integer> cacheChoiceBox;
	@FXML
	private Button restoreButton;
	@FXML
	private Button saveButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
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
	private CheckBox basicCheckBox;
	@FXML
	private Slider alphaSlider;
	@FXML
	private Slider betaSlider;
	@FXML
	private TextField minThreshold;
	@FXML
	private TextField maxThreshold;
	@FXML
	private TextField scaleText;

	@Autowired
	private WorkspaceService workspaceService;
	@Autowired
    private ProgramConfigService programConfigService;
	@Autowired
	private GrainService grainService;
	@Autowired
	private GrainProcessing grainProcessing;
	@Autowired
	private MarkerRemover markerRemover;
	@Autowired
	private ColorMapper colorMapper;
	@Autowired
	private MarkerFileQueue markerFileQueue;
	@Autowired
	private MarkerFileStore markerFileStore;
	
	@Autowired
	@Qualifier("shpExport")
	private GrainExport grainExport;
	
	@Autowired
	@Qualifier("textExport")
	private GrainExport grainTextExport;
	
	@Autowired
	@Qualifier("ellipseExport")
	private GrainExport grainEllipseExport;
	
	private PannableCanvas canvas;
	private File workspaceFolder;
	private GrainVO grainVO;
	private ProgramConfigVO programConfigVO;

	public void initialize() {
		logger.info("initialize");
		initializeConfig();
		initializeRecentMenu();
        initializeItem();
		initializeToggleButton();
		initializeCanvas();
        initializeRestore();
		setNextPrevious();

		mainPane.addEventFilter( KeyEvent.KEY_PRESSED, e->{
			if(KeyCode.F==e.getCode()) {
				segmentCheckBox.setSelected(!segmentCheckBox.isSelected());
			}else if(KeyCode.R==e.getCode()){
				doReSegment();
			}
		});
		markerCheckBox.selectedProperty().addListener((ov,old_val, new_val)->
			canvas.setBothMarkerVisibleAndEnable(new_val)
		);
		segmentCheckBox.selectedProperty().addListener((ov,old_val, new_val)->
			canvas.setOverlayShow(new_val)
		);
		basicCheckBox.selectedProperty().addListener((ov,old_val, new_val)->
				canvas.setBasicShow(new_val)
		);

		exportButton.setOnAction(e->{
			if (Objects.isNull(workspaceFolder))
				return;
			if (StringUtils.isBlank(scaleText.getText())) {
				showInfoAlert("Please enter the scale");
				scaleText.requestFocus();
				return;
			}
			doExportResult("perimeter.shp", grainExport);
			doExportResult("par_ellipse.txt", grainTextExport);
			doExportResult("ellipse.shp", grainEllipseExport);
		});

		fitEllipseButton.setOnAction(e -> {
			grainProcessing.doFitEllipse(grainVO);
			setEllipse(grainVO);
		});
		closeEllipseButton.setOnAction(e-> canvas.setEllipseShow(false));

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

		applyColorButton.setOnAction(e->{
			if(grainVO==null || grainVO.getOriginalImg()==null)
				return;
			ColorMap colorMap = colorChoiceBox.getValue();
			Mat mat = colorMapper.convertColorMat(colorMap, grainVO.getOriginalImg());
			grainVO.setDisplayImg(mat);
			Image img = Utils.mat2Image(mat);
			if(img!=null)
				canvas.setBasicImage(img);
		});

		previousButton.setOnAction(e->{
		    if(grainVO==null || !markerFileQueue.restorePrevious(workspaceFolder)){
		        showInfoAlert("Can't load previous result.");
		        return;
            }
		    setMarkerImage();
            doReSegment(false);
        });

        nextButton.setOnAction(e->{
            if(grainVO==null || !markerFileQueue.restoreNext(workspaceFolder)){
                showInfoAlert("Can't load next result.");
                return;
            }
            setMarkerImage();
            doReSegment(false);
        });
		scaleText.setAlignment(Pos.CENTER);
	}

	private void doExportResult(String fileName, GrainExport grainExport) {
		File exportFolder = new File(workspaceFolder, "export");
		if (!exportFolder.isDirectory() && !exportFolder.mkdir())
			return;
		File exportFile = new File(exportFolder, fileName);
		if (exportFile.exists() && !overwriteFileConfirm(exportFile))
			return;
		doExport(exportFile, grainExport);
	}

	private boolean overwriteFileConfirm(File file){
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Overwrite Confirmation");
		alert.setHeaderText("It will overwrite existing files:" + file.getName());
		Optional<ButtonType> optional = alert.showAndWait();
		return ButtonType.OK.equals(optional.orElse(ButtonType.NO));
	}

	private void initializeConfig() {
		programConfigVO = programConfigService.getProgramConfig();
	}

	private void initializeRecentMenu() {
		List<MenuItem> items = recentMenu.getItems();
		recentMenu.getItems().remove(0, items.size());
		Set<String> set = programConfigVO.getResentFiles();
		for (String s:set) {
			MenuItem m = new MenuItem(s);
			m.setOnAction(e-> openWorkspace(m.getText()));
			recentMenu.getItems().add(0, m);
		}
	}

	private void initializeItem(){
        List<Integer> cacheIndex = IntStream.range(1, 11).boxed().collect(Collectors.toList());
        cacheChoiceBox.setItems(FXCollections.observableArrayList(cacheIndex));
        cacheChoiceBox.getSelectionModel().selectFirst();
        colorChoiceBox.setItems(FXCollections.observableArrayList(ColorMap.values()));
        colorChoiceBox.getSelectionModel().selectFirst();
        previousButton.setGraphic(FontIcon.of(FontAwesome.CHEVRON_CIRCLE_LEFT, 20));
        nextButton.setGraphic(FontIcon.of(FontAwesome.CHEVRON_CIRCLE_RIGHT, 20));
    }

	private void initializeToggleButton() {
	    Color grainToolColor = Color.rgb(209, 79, 8);
	    seedButton.setTooltip(new Tooltip("add grain marker"));
		seedButton.setGraphic(FontIcon.of(FontAwesome.PENCIL, 20, grainToolColor));
		seedButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e->setDrawingAction(DrawingAction.SEED));

		clearSeedButton.setTooltip(new Tooltip("erase grain marker - by pixel"));
		clearSeedButton.setGraphic(FontIcon.of(FontAwesome.ERASER, 20, grainToolColor));
		clearSeedButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e->canvas.setSeedClearDrawing());

		trashSeedButton.setTooltip(new Tooltip("delete grain marker - by connected pixels"));
		trashSeedButton.setGraphic(FontIcon.of(FontAwesome.TRASH, 20, grainToolColor));
		trashSeedButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e->canvas.setSeedTrashDrawing());

		Color shadowToolColor = Color.rgb(8, 35, 209);
		shadowButton.setTooltip(new Tooltip("add background marker"));
		shadowButton.setGraphic(FontIcon.of(FontAwesome.REMOVE, 20, shadowToolColor));
		shadowButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e->setDrawingAction(DrawingAction.SHADOW));

		clearShadowButton.setTooltip(new Tooltip("erase background marker - by pixel"));
		clearShadowButton.setGraphic(FontIcon.of(FontAwesome.ERASER, 20, shadowToolColor));
		clearShadowButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e->canvas.setShadowClearDrawing());

		trashShadowButton.setTooltip(new Tooltip("delete background marker - by connected pixels"));
		trashShadowButton.setGraphic(FontIcon.of(FontAwesome.TRASH, 20, shadowToolColor));
		trashShadowButton.addEventFilter(MouseEvent.MOUSE_PRESSED, e->canvas.setShadowTrashDrawing());
	}

	private void initializeCanvas(){
		canvas = new PannableCanvas(markerRemover);
		canvas.setFocusTraversable(true);
		SceneGestures sceneGestures = new SceneGestures(canvas);
		canvas.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
		canvas.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
		canvas.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
		imgHolder.setCenter(canvas);
	}

	private void initializeRestore(){
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
            try {
				markerFileStore.copyIndexToDefault(workspaceFolder, restoreIndex);
			}catch (Exception ex){
            	logger.error(ex.getMessage());
            	showInfoAlert("Restore Image failed");
            	return;
			}

            setMarkerImage();
            doReSegment();
            markerIndexLabel.setText("Result Index:"+restoreIndex);
        });
    }

    private void setNextPrevious(){
		nextButton.setDisable(!markerFileQueue.hasNext());
		previousButton.setDisable(!markerFileQueue.hasPrevious());
	}

    public void doReSegment(){
        doReSegment(true);
    }

    private void doReSegment(boolean saveCache) {
        doSaveCanvas(saveCache);
        grainProcessing.doReSegmentGrainProcessing(grainVO);
        grainService.saveImage(grainVO);
        segmentCheckBox.setSelected(setOverlay(grainVO));
		basicCheckBox.setSelected(true);
        setMarkerImage();
        markerIndexLabel.setText("Result Index:");
		setNextPrevious();
		setEllipse(grainVO);
    }
	
	public void doSegment() {
		doSaveCanvas(true);
		GrainParam param = new GrainParam();
		param.setCannyMaxThreshold(Integer.parseInt(minThreshold.getText()));
		param.setCannyMaxThreshold(Integer.parseInt(maxThreshold.getText()));
		grainProcessing.doGrainProcessing(grainVO, param);
		grainService.saveImage(grainVO);
		segmentCheckBox.setSelected(setOverlay(grainVO));
		basicCheckBox.setSelected(true);
		setMarkerImage();
		setNextPrevious();
		setEllipse(grainVO);
	}

	private void doSaveCanvas(boolean saveCache) {
		File seedFile = new File(workspaceFolder, MarkerFile.SEED_FILE_NAME);
		File shadowFile = new File(workspaceFolder, MarkerFile.SHADOW_FILE_NAME);
		canvas.doSaveCanvas(seedFile, shadowFile);
		if(saveCache)
		    markerFileQueue.add(workspaceFolder);
	}
	
	public void importGrainImage(ActionEvent ae) {
		if(workspaceFolder==null || !workspaceFolder.isDirectory())
			return;
		workspaceMenuItem.setDisable(true);
		Optional<File> oriImgOptional = selectImageToImport(workspaceFolder);
		if (!oriImgOptional.isPresent())
			return;
		grainVO = grainService.getGrainVO(workspaceFolder.getAbsolutePath(), oriImgOptional);
		if(grainVO == null)
			return;
		if (Objects.nonNull(grainVO.getConfig())
				&& Objects.nonNull(grainVO.getConfig())
				&& Objects.nonNull(grainVO.getConfig().getOriPoint())
				&& Objects.nonNull(grainVO.getConfig().getOriPoint().getScale())) {
			scaleText.setText(String.valueOf(grainVO.getConfig().getOriPoint().getScale() * 100));
		}

		improtMenuItem.setDisable(true);
		Image image = Utils.mat2Image(grainVO.getOriginalImg());
		canvas.initCanvas(image);
		segmentCheckBox.setSelected(setOverlay(grainVO));
		basicCheckBox.setSelected(true);
		setMarkerImage();
	}
	
	public void openWorkspace(ActionEvent ae) {
		openWorkspace(selectWorkspace());
	}

	private void openWorkspace(String workspace) {
		if(!workspaceService.openWorkspace(workspace))
			return;
		workspaceMenuItem.setDisable(true);
		recentMenu.setDisable(true);
		workspaceFolder = new File(workspace);
		markerFileQueue.clearTemp(workspaceFolder);
		programConfigService.addRecentWorkspace(programConfigVO, workspace);
		programConfigService.saveConfig(programConfigVO);
		grainVO = grainService.getGrainVO(workspace, Optional.empty());
		if(grainVO==null) {
			improtMenuItem.setDisable(false);
			return;
		}
		if (Objects.nonNull(grainVO.getConfig())
				&& Objects.nonNull(grainVO.getConfig())
				&& Objects.nonNull(grainVO.getConfig().getOriPoint())
				&& Objects.nonNull(grainVO.getConfig().getOriPoint().getScale())) {
			scaleText.setText(String.valueOf(grainVO.getConfig().getOriPoint().getScale() * 100));
		}
		improtMenuItem.setDisable(true);
		Image image = Utils.mat2Image(grainVO.getOriginalImg());
		canvas.initCanvas(image);
		segmentCheckBox.setSelected(setOverlay(grainVO));
		basicCheckBox.setSelected(true);
		setMarkerImage();
	}
	
	private void setMarkerImage() {
		boolean hasSeed = setSeedCanvas();
		boolean hasShadow = setShadowCanvas();
		canvas.setBothMarkerVisibleAndEnable(hasSeed || hasShadow); 
		markerCheckBox.setSelected(hasSeed || hasShadow);
	}
	
	private void enhanceBasicImage() {
		grainVO = grainProcessing.enhanceToShow(grainVO);
		Image image = Utils.mat2Image(grainVO.getEnhanceImg());
		canvas.setBasicImage(image);
	}

	private void setDrawingAction(DrawingAction drawingAction) {
		canvas.triggerDrawingAction(drawingAction);
	}

	private void doExport(File exportFile, GrainExport grainExport) {
		try {
			double mPerPixel = Double.parseDouble(scaleText.getText()) / 100.0;
			GrainResultAdapter.setScale(mPerPixel);
			grainVO.getConfig().getOriPoint().setScale(mPerPixel);
			grainService.saveConfig(grainVO);
		}catch (Exception e){
			logger.error("doExport:{}", ExceptionUtils.getStackTrace(e));
			showInfoAlert("Format pixel scale failed.");
			return;
		}
		try {
			grainProcessing.doFitEllipse(grainVO);
			List<GrainResultVO> list = grainVO.getResults();
			List<GrainShape> grainShapes = new LinkedList<>();
			for (GrainResultVO vo : list) {
				grainShapes.add(new GrainResultAdapter(vo, grainVO.getConfig().getOriPoint()));
			}
			grainExport.doExportGrain(grainShapes, exportFile.getAbsolutePath());
			setEllipse(grainVO);
			grainVO.setResults(null);
		}catch (Exception e){
			logger.error("doExport:{}", ExceptionUtils.getStackTrace(e));
			showInfoAlert("Export ellipse failed:"+e.getLocalizedMessage());
		}
	}
	
	private boolean setOverlay(GrainVO vo) {
		if(vo.getOverlayImg()==null) 
			return false;
		Image overlay;
		try {
			overlay = Utils.mat2Image(vo.getOverlayImg());
		}catch(Exception e) {
			logger.error("setOverlay failed:{}", e.getMessage());
			return false;
		}
		return canvas.setOverlay(overlay);
	}
	
	private void setEllipse(GrainVO vo) {
		if(vo.getEllipseImg()==null) {
			canvas.setEllipseShow(false);
			return;
		}
		try {
			canvas.setEllipse(Utils.mat2Image(vo.getEllipseImg()));
			canvas.setEllipseShow(true);
		}catch(Exception e) {
			logger.error("setEllipse failed:{}", e.getMessage());
			canvas.setEllipseShow(false);
		}
	}
	
	private boolean setSeedCanvas() {
		File seedFile = new File(workspaceFolder, "_seed.png");
		if(seedFile.exists()) {
			try(FileInputStream inputStream = new FileInputStream(seedFile)) {
				canvas.setSeedImage(new Image(inputStream));
				return true;
			} catch (IOException e) {
				logger.error("setSeedCanvas failed:{}", e.getMessage());
			}
		}else {
			canvas.clearSeedCanvas();
		}
		return false;
	}
	
	private boolean setShadowCanvas() {
		File shadowFile = new File(workspaceFolder, "_shadow.png");
		if(shadowFile.exists()) {
			try(FileInputStream inputStream = new FileInputStream(shadowFile)) {
				canvas.setShadowImage(new Image(inputStream));
				return true;
			} catch (IOException e) {
				logger.error("setShadowCanvas failed:{}", e.getMessage());
			}
		}else {
			canvas.clearShadowImage();
		}
		return false;
	}

	private String selectWorkspace() {
		Stage stage = (Stage) menuBar.getScene().getWindow();
		DirectoryChooser chooser = new DirectoryChooser();
		String folder = "/";
		if (!CollectionUtils.isEmpty(programConfigVO.getResentFiles())) {
			int count = programConfigVO.getResentFiles().size();
			Optional<String> opt = programConfigVO.getResentFiles().stream().skip(count - 1).findFirst();
			if (opt.isPresent() && (new File(opt.get())).isDirectory())
				folder = opt.get();
		}
		File defaultDirectory = new File(folder);
		chooser.setInitialDirectory(defaultDirectory);
		File selectedDirectory = chooser.showDialog(stage);
		return selectedDirectory==null ? "":selectedDirectory.getAbsolutePath();
	}
	
	private Optional<File> selectImageToImport(File workspaceFolder) {
		Stage stage = (Stage) menuBar.getScene().getWindow();
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(workspaceFolder);
		File imageFile = chooser.showOpenDialog(stage);
		return workspaceService.importImageToWorkspace(workspaceFolder.getAbsolutePath(), imageFile.getAbsolutePath());
	}

	private void showInfoAlert(String msg){
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK);
        alert.setTitle("INFO");
        alert.setHeaderText("");
        alert.showAndWait();
    }
}