package de.uks.se.checkman.app;

import de.uks.se.checkman.controller.CheckManController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ChecklistManagerApp extends Application {
	
	public static void main(String... args) {
		Application.launch();
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("ChecklistManager");
		
		CheckManController controller = new CheckManController();
		Scene mainScene = controller.initUI();
		
		stage.setScene(mainScene);
		stage.show();
	}
}