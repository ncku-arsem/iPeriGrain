package edu.ncku.canvas;

import edu.ncku.service.MarkerRemover;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class PannableCanvas extends Pane{
	private final Logger logger = LogManager.getLogger(PannableCanvas.class.getClass());

	private DoubleProperty currentScale = new SimpleDoubleProperty(1.0);
	private Canvas basicCanvas;
	private Canvas overlayCanvas;
	private Canvas ellipseCanvas;

	private Canvas seedCanvas;
	private Canvas shadowCanvas;
	private Canvas confirmedCanvas;

	private DrawingAction drawingAction;
	private ClearTrashAction clearTrashAction;
	private MarkerRemover markerRemover;
	
	public PannableCanvas(MarkerRemover markerRemover) {
		super();
		this.markerRemover = markerRemover;
		setStyle("-fx-background-color: lightgrey;");
		resize(600, 600);
		setPrefSize(600, 600);
		scaleXProperty().bind(currentScale);
		scaleYProperty().bind(currentScale);
		addEventFilter(MouseEvent.MOUSE_PRESSED, getOnMousePressedEventHandler());
		addEventFilter(MouseEvent.MOUSE_DRAGGED, getOnMouseDraggedEventHandler());
	}

	public double getScale() {
		return currentScale.get();
	}

	public void setScale(double scale) {
		currentScale.set(scale);
	}

	public void setPivot(double x, double y) {
		setTranslateX(getTranslateX() - x);
		setTranslateY(getTranslateY() - y);
	}

	@Override
	public boolean isResizable() {
		return false;
	}
	
	public void initCanvas(Image img) {
		clearCanvas();
		double w = img.getWidth();
		double h = img.getHeight();
		resize(w, h);
		basicCanvas = new Canvas(w, h);
		seedCanvas = new Canvas(w, h);
		shadowCanvas = new Canvas(w, h);
		confirmedCanvas = new Canvas(w, h);

		basicCanvas.setMouseTransparent(true);
		seedCanvas.setMouseTransparent(true);
		shadowCanvas.setMouseTransparent(true);
		confirmedCanvas.setMouseTransparent(true);

		GraphicsContext gc = basicCanvas.getGraphicsContext2D();
		gc.setImageSmoothing(false);
		gc.drawImage(img, 0, 0, w, h);
		seedCanvas.getGraphicsContext2D().setStroke(Color.RED);
		seedCanvas.getGraphicsContext2D().setImageSmoothing(false);
		shadowCanvas.getGraphicsContext2D().setStroke(Color.BLUE);
		shadowCanvas.getGraphicsContext2D().setImageSmoothing(false);
		confirmedCanvas.getGraphicsContext2D().setStroke(Color.GREEN);
		confirmedCanvas.getGraphicsContext2D().setImageSmoothing(false);
		getChildren().add(basicCanvas);
		getChildren().add(seedCanvas);
		getChildren().add(shadowCanvas);
		getChildren().add(confirmedCanvas);
		basicCanvas.toBack();
		setNotClearing();
		triggerDrawingAction(DrawingAction.SEED);
	}

	public void setBasicImage(Image img) {
		GraphicsContext gc = basicCanvas.getGraphicsContext2D();
		gc.drawImage(img, 0, 0, basicCanvas.getWidth(), basicCanvas.getHeight());
		basicCanvas.toBack();
	}

	public boolean setOverlay(Image overlay) {
		try {
			double w = overlay.getWidth();
			double h = overlay.getHeight();
			if(overlayCanvas==null) 
				overlayCanvas = new Canvas(basicCanvas.getWidth(), basicCanvas.getHeight());
			overlayCanvas.setMouseTransparent(true);
			GraphicsContext gc = overlayCanvas.getGraphicsContext2D();
			gc.clearRect(0, 0, w, h);
			gc.drawImage(overlay, 0, 0, w, h);
			if(!getChildren().contains(overlayCanvas))
				getChildren().add(overlayCanvas);
			overlayCanvas.toFront();
			seedCanvas.toFront();
			shadowCanvas.toFront();
			confirmedCanvas.toFront();
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean setEllipse(Image ellipse) {
		logger.info("setEllipse");
		try {
			double w = ellipse.getWidth();
			double h = ellipse.getHeight();
			if(ellipseCanvas==null) 
				ellipseCanvas = new Canvas(basicCanvas.getWidth(), basicCanvas.getHeight());
			ellipseCanvas.setMouseTransparent(true);
			GraphicsContext gc = ellipseCanvas.getGraphicsContext2D();
			gc.clearRect(0, 0, w, h);
			gc.drawImage(ellipse, 0, 0, w, h);
			if(!getChildren().contains(ellipseCanvas))
				getChildren().add(ellipseCanvas);
			ellipseCanvas.toFront();
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void setSeedImage(Image seedImg) {
		GraphicsContext gc = seedCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, seedCanvas.getWidth(), seedCanvas.getHeight());
		gc.drawImage(seedImg, 0, 0, seedCanvas.getWidth(), seedCanvas.getHeight());
		gc.setStroke(Color.RED);
		seedCanvas.setVisible(true);
	}
	
	public void setShadowImage(Image shadowImg) {
		GraphicsContext gc = shadowCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, shadowImg.getWidth(), shadowImg.getHeight());
		gc.drawImage(shadowImg, 0, 0, shadowCanvas.getWidth(), shadowCanvas.getHeight());
		gc.setStroke(Color.BLUE);
		shadowCanvas.setVisible(true);
	}

	public void setConfirmedImage(Image confirmedImg) {
		GraphicsContext gc = confirmedCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, confirmedImg.getWidth(), confirmedImg.getHeight());
		gc.drawImage(confirmedImg, 0, 0, confirmedCanvas.getWidth(), confirmedCanvas.getHeight());
		gc.setStroke(Color.GREEN);
		confirmedCanvas.setVisible(true);
	}
	
	public void clearSeedCanvas() {
		if(seedCanvas==null) return;
		GraphicsContext gc = seedCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, seedCanvas.getWidth(), seedCanvas.getHeight());
	}
	
	public void clearShadowImage() {
		if(shadowCanvas==null) return;
		GraphicsContext gc = shadowCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, shadowCanvas.getWidth(), shadowCanvas.getHeight());
	}

	public void setBasicShow(boolean showBasic) {
		if (basicCanvas == null)
			return;
		basicCanvas.setVisible(showBasic);
	}
	
	public void setOverlayShow(boolean showOverlay) {
		if (overlayCanvas == null)
			return;
		overlayCanvas.setVisible(showOverlay);
	}
	
	public void setEllipseShow(boolean showOverlay) {
		if (ellipseCanvas == null)
			return;
		ellipseCanvas.setVisible(showOverlay);
	}
	
	public void setBothMarkerVisibleAndEnable(boolean enable) {
		if(seedCanvas != null) {
			seedCanvas.setVisible(enable);
			seedCanvas.setDisable(!enable);
		}
		if(shadowCanvas != null) {
			shadowCanvas.setVisible(enable);
			shadowCanvas.setDisable(!enable);
		}
	}
	
	public void triggerDrawingAction(DrawingAction drawingAction) {
		setNotClearing();
		setBothMarkerVisibleAndEnable(true);
		this.drawingAction = drawingAction;
		seedCanvas.setDisable(drawingAction.equals(DrawingAction.SEED));
		shadowCanvas.setDisable(drawingAction.equals(DrawingAction.SHADOW));
	}
	
	public void setSeedClearDrawing() {
		clearTrashAction = ClearTrashAction.SEED_CLEAR;
		setBothMarkerVisibleAndEnable(true);
	}
	
	public void setShadowClearDrawing() {
		clearTrashAction = ClearTrashAction.SHADOW_CLEAR;
		setBothMarkerVisibleAndEnable(true);
	}
	
	public void setSeedTrashDrawing() {
		clearTrashAction = ClearTrashAction.SEED_TRASH;
		setBothMarkerVisibleAndEnable(true);
	}
	
	public void setShadowTrashDrawing() {
		clearTrashAction = ClearTrashAction.SHADOW_TRASH;
		setBothMarkerVisibleAndEnable(true);
	}
	
	public void doSaveCanvas(File seedFile, File shadowFile) {
		if(seedCanvas!=null) {
			seedCanvas.setDisable(false);
			seedCanvas.setVisible(saveCanvas(seedCanvas, seedFile));
		}
		if(shadowCanvas!=null) {
			shadowCanvas.setDisable(false);
			shadowCanvas.setVisible(saveCanvas(shadowCanvas, shadowFile));
		}
		setBothMarkerVisibleAndEnable(true);
		seedCanvas.setDisable(drawingAction.equals(DrawingAction.SEED));
		shadowCanvas.setDisable(drawingAction.equals(DrawingAction.SHADOW));
	}
	
	private boolean saveCanvas(Canvas canvas, File file) {
		if(canvas==null) return false;
		WritableImage writableImage = getWritableImage(canvas);
		RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
        try {
			ImageIO.write(renderedImage, "png", file);
		} catch (IOException e) {
			return false;
		}
        return true;
	}
	
	private WritableImage getWritableImage(Canvas canvas) {
		canvas.setVisible(true);
		WritableImage writableImage = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
		SnapshotParameters sp = new SnapshotParameters();
	    sp.setFill(Color.TRANSPARENT);
		canvas.snapshot(sp, writableImage);
		return writableImage;
	}

	private void clearCanvas() {
		getChildren().clear();
		setScale(1);
		setTranslateX(0);
		setTranslateY(0);
	}

	public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}

	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
			if (!event.isPrimaryButtonDown())
				return;
			GraphicsContext gc;
			if(clearTrashAction == ClearTrashAction.SEED_CLEAR || clearTrashAction == ClearTrashAction.SHADOW_CLEAR) 
				return;
			if(clearTrashAction == ClearTrashAction.SEED_TRASH || clearTrashAction == ClearTrashAction.SHADOW_TRASH) {
				Image image = getWritableImage(clearTrashAction==ClearTrashAction.SEED_TRASH ? seedCanvas:shadowCanvas);
				image = markerRemover.removeMaker(event.getX(), event.getY(), image);
				if(clearTrashAction.equals(ClearTrashAction.SEED_TRASH)) {
					setSeedImage(image);
				}else {
					setShadowImage(image);
				}
				return;
			}
			gc = drawingAction.equals(DrawingAction.SEED) ? seedCanvas.getGraphicsContext2D():shadowCanvas.getGraphicsContext2D();
			gc.beginPath();
			gc.moveTo(event.getX(), event.getY());
			gc.stroke();
		}
	};

	public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent e) {
			if (!e.isPrimaryButtonDown())
				return;
			GraphicsContext gc;
			if(clearTrashAction == ClearTrashAction.SEED_TRASH || clearTrashAction == ClearTrashAction.SHADOW_TRASH)
				return;
			if(clearTrashAction == ClearTrashAction.SEED_CLEAR || clearTrashAction == ClearTrashAction.SHADOW_CLEAR) {
				gc = clearTrashAction == ClearTrashAction.SEED_CLEAR ? seedCanvas.getGraphicsContext2D():shadowCanvas.getGraphicsContext2D();
				gc.clearRect(e.getX() - 2, e.getY() - 2, 5, 5);
			}else {	
				gc = drawingAction.equals(DrawingAction.SEED) ? seedCanvas.getGraphicsContext2D():shadowCanvas.getGraphicsContext2D();
				gc.lineTo(e.getX(), e.getY());
				gc.stroke();
			}
			e.consume();
		}
	};
	
	private void setNotClearing() {
		clearTrashAction = ClearTrashAction.NONE;
	}

	@SuppressWarnings("unused")
	private void doLogging() {
		addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			System.out.println("canvas event: "
					+ (((event.getSceneX() - getBoundsInParent().getMinX()) / getScale()) + ", scale: " + getScale()));
			System.out.println("canvas bounds: " + getBoundsInParent());
		});
	}
}
