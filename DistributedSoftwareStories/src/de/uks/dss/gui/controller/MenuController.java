package de.uks.dss.gui.controller;

import java.beans.PropertyChangeEvent;

import de.uks.dss.app.DocumentDataCheckManApp;
import de.uks.dss.generate.TaskFlowGenerator;
import de.uks.dss.model.Task;
import de.uks.dss.model.util.TaskPO;
import de.uniks.networkparser.UpdateListener;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MenuController
{
	private static final int DEFAULT_WINDOW_WIDTH = 1024;
	private static final int DEFAULT_WINDOW_HEIGHT = 768;
	private static final int DEFAULT_TOOLBAR_WIDTH = 1000;

	//model
	private Task storyRoot;

	//view
	private HBox toolbar;
	private VBox menu;
	private Label lastMenuItem;

	public MenuController start()
	{
		Button overviewButton = new Button("Overview");
		overviewButton.setOnMouseClicked(e -> switchToOverview());
		
		Button checklistButton = new Button("Checklist");
		checklistButton.setOnMouseClicked(e -> switchToChecklist());
		
		Button generateButton = new Button("Generate");
		generateButton.setOnMouseClicked(e -> handleGenerate());
		
		toolbar.getChildren().addAll(overviewButton, checklistButton, generateButton);
		toolbar.setAlignment(Pos.TOP_LEFT);
		toolbar.setMinWidth(DEFAULT_TOOLBAR_WIDTH);

		menu.setAlignment(Pos.CENTER);

		for(Task tmpStory : storyRoot.getSubTasks())
		{			
			TextField menuItem = new TextField();
			menuItem.setText(tmpStory.getName());
			menuItem.setOnMouseClicked(e -> switchToStoryEditor(e, tmpStory));
			menuItem.setOnAction(e -> changeTitle(e, menuItem, tmpStory));
			menuItem.setOnKeyTyped(e -> deleteStory(e, tmpStory));

			menu.getChildren().add(menuItem);
			tmpStory.addPropertyChangeListener(Task.PROPERTY_NAME, e -> handleTitleChange(e.getNewValue(), menuItem));
		}

		createAndAddLastMenuItem();

		storyRoot.addPropertyChangeListener(Task.PROPERTY_SUBTASKS, e -> handleNewStory(e));
		
		return this;
	}

	private void switchToOverview()
	{
		//ui
		HBox toolBar = new HBox();
		Pane counterTop = new Pane();
		counterTop.setMinWidth(700);
		counterTop.setMinHeight(400);
		ScrollPane counterTopRoot = new ScrollPane(counterTop);
		
		new OverviewController()
			.withToolbar(toolBar)
			.withCounterTop(counterTop)
			.withTaskflowContainer(storyRoot.getParentTasks().first())
			.start();

		VBox root = new VBox(toolBar, counterTopRoot);
		Scene overviewScene = new Scene(root, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
		DocumentDataCheckManApp.switchScene(overviewScene);
	}

	private void switchToChecklist() {
		Scene checkListScene = DocumentDataCheckManApp.getChecklistScene();
		DocumentDataCheckManApp.switchScene(checkListScene);
	}

	private void handleGenerate()
	{
		Task root = storyRoot.getParentTasks().first();
		TaskFlowGenerator.getInstance().generateTaskFlows(storyRoot, root);
	}

	private void switchToStoryEditor(MouseEvent e, Task story)
	{
		//switch to story editor on double click
		if(e.getClickCount() == 2)
		{
			//ui
			HBox toolBar = new HBox();
			Pane counterTop = new Pane();
			counterTop.setMinWidth(700);
			counterTop.setMinHeight(400);
			
			new SoftwareStoryController()
				.withStoryRoot(storyRoot)
				.withToolbar(toolBar)
				.withCounterTop(counterTop)
				.withStory(story)
				.start();

			VBox root = new VBox(toolBar, counterTop);
			Scene editorScene = new Scene(root, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
			DocumentDataCheckManApp.switchScene(editorScene);
		}
	}

	private void createNewStory(MouseEvent e)
	{
		Task newStory = new Task();
		newStory.withName(lastMenuItem.getText());
		storyRoot.withSubTasks(newStory);
	}

	private void changeTitle(ActionEvent e, TextField menuItem, Task tmpStory)
	{
		tmpStory.withName(menuItem.getText());
	}
	
	private void deleteStory(KeyEvent e, Task tmpStory) {
		tmpStory.removeYou();
	}
	
	private void handleTitleChange(Object newValue, TextField menuItem)
	{
		menuItem.setText(newValue.toString());
	}

	private void handleNewStory(PropertyChangeEvent event)
	{
		Task newStory = (Task)event.getNewValue();
		TaskPO newStoryPO = new TaskPO(newStory);
		newStoryPO.startNAC().filterName("").endNAC();
		
		if(newStoryPO.getHasMatch())
		{
			showOnUIAndRegisterListeners(newStory);
		}
		else{
			//UpdateListener pcuListener = DocumentDataCheckManApp.getPropertyChangeUpdateListener();
			//pcuListener.registerPropertyChangeListener(newStoryPO, evt -> showOnUIAndRegisterListeners(newStory));
//			System.err.println("showOnUIAndRegisterListeners COULDN'T BE FIRED");
//			showOnUIAndRegisterListeners(newStory);
			//throw new RuntimeException("Removed PropertyChangeUpdateListener.. This is the Consequence!");
		}
		
		newStoryPO.getPattern().resetSearch();
		newStoryPO.getPattern().findMatch();
	}

	private void showOnUIAndRegisterListeners(Task newStory)
	{
		menu.getChildren().remove(lastMenuItem);

		TextField menuItem = new TextField();
		menuItem.setText(newStory.getName());
		menuItem.setOnMouseClicked(e -> switchToStoryEditor(e, newStory));
		menu.getChildren().add(menuItem);
		menuItem.setOnAction(e -> changeTitle(e, menuItem, newStory));
		newStory.addPropertyChangeListener(Task.PROPERTY_NAME, e -> handleTitleChange(e.getNewValue(), menuItem));
		
		createAndAddLastMenuItem();
	}

	private void createAndAddLastMenuItem()
	{
		lastMenuItem = new Label("new story?");
		lastMenuItem.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		lastMenuItem.setOnMouseClicked(e -> createNewStory(e));
		menu.getChildren().add(lastMenuItem);
	}

	public Task getStoryRoot()
	{
		return storyRoot;
	}

	public void setStoryRoot(Task storyRoot)
	{
		this.storyRoot = storyRoot;
	}

	public MenuController withStoryRoot(Task storyRoot)
	{
		setStoryRoot(storyRoot);
		return this;
	}

	public void setToolbar(HBox toolbar)
	{
		this.toolbar = toolbar;
	}
	
	public MenuController withToolbar(HBox toolbar)
	{
		setToolbar(toolbar);
		return this;
	}
	
	public void setMenu(VBox menu)
	{
		this.menu = menu;
	}
	
	public MenuController withMenu(VBox menu)
	{
		setMenu(menu);
		return this;
	}
}
