package de.uks.se.checkman.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CheckManController {

	private static final int DEFAULT_WINDOW_WIDTH = 800;
	private static final int DEFAULT_WINDOW_HEIGHT = 600;
	
	private Scene scene;
	
	public Scene initUI(){
		VBox rootPane = new VBox(12);
		rootPane.setPadding(new Insets(24));
		
		try {
			String content = new String(Files.readAllBytes(Paths.get("data/persist.cmlog")));
			JsonObject rootObject = new JsonObject().withValue(content);
			
			JsonArray thesisArray = rootObject.getJsonArray("thesis");
			
			//contact data
			JsonObject contactData = thesisArray.getJSONObject(0);
			
			VBox contactElements = new VBox(); 
			rootPane.getChildren().add(contactElements);
			
			for(String key : contactData){
				String value = (String) contactData.getValue(key);
				
				if(key.endsWith("_text")){
					TextField textField = new TextField(value);
					contactElements.getChildren().add(textField);
				}
				else if(key.endsWith("_combo")){
					ComboBox<String> combo = new ComboBox<String>();
					combo.getItems().add(value);
					combo.setValue(value);
					contactElements.getChildren().add(combo);
				}
			}
			
			//open
			VBox openTasksBox = new VBox();
			rootPane.getChildren().add(openTasksBox);
         
			HBox openTitleRow = new HBox(new Label("Open Tasks:"));
			openTasksBox.getChildren().add(openTitleRow);
			
			JsonArray openTasks = thesisArray.getJSONObject(1).getJsonArray("open");
			for (int i = 0; i < openTasks.size(); i++) {
				JsonObject task = (JsonObject) openTasks.get(i);
				
				TextField taskTitle = new TextField((String) task.get("title"));
				taskTitle.setPrefWidth(400);
				TextField taskAssigned = new TextField((String) task.get("assigned"));
				taskAssigned.setPrefWidth(200-24);
				
				HBox taskRow = new HBox(taskTitle,taskAssigned);
				openTasksBox.getChildren().add(taskRow);
			}
			
			//done
			VBox doneTasksBox = new VBox();
         rootPane.getChildren().add(doneTasksBox);
         
			HBox doneTitleRow = new HBox(new Label("Done Tasks:"));
			doneTasksBox.getChildren().add(doneTitleRow);
			
			JsonArray doneTasks = thesisArray.getJSONObject(2).getJsonArray("done");
			for (int i = 0; i < doneTasks.size(); i++) {
				JsonObject task = (JsonObject) doneTasks.get(i);
				
				TextField taskTitle = new TextField((String) task.get("title"));
				taskTitle.setPrefWidth(400);
				TextField taskAssigned = new TextField((String) task.get("assigned"));
				taskAssigned.setPrefWidth(200-24);
				TextField taskTimestamp = new TextField((String) task.get("timestamp"));
				taskTimestamp.setPrefWidth(200-24);
				HBox taskRow = new HBox(taskTitle,taskAssigned,taskTimestamp);
				doneTasksBox.getChildren().add(taskRow);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Scene mainScene = new Scene(rootPane, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
		return mainScene;
	}

	public CheckManController withScene(Scene scene) {
		this.scene = scene;
		return this;
	}
}