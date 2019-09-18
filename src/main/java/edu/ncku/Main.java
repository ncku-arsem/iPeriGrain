package edu.ncku;
	
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import edu.ncku.store.MarkerFileQueue;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
public class Main extends Application {
	
	private ConfigurableApplicationContext springContext;
	private Parent rootNode;

	public static void main(String[] args) {
		nu.pattern.OpenCV.loadShared();
		Application.launch(args);
	}

	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(Main.class);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resource/fxml/WorkeSpace.fxml"));
		fxmlLoader.setControllerFactory(springContext::getBean);
		rootNode = fxmlLoader.load();
	}

	@Override
	public void start(Stage primaryStage) {
		Scene scene = new Scene(rootNode, 1200, 800);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void stop() {
		springContext.close();
	}
}
