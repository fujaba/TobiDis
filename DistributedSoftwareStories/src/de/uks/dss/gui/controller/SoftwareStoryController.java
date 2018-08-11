package de.uks.dss.gui.controller;

import de.uks.dss.model.DocumentDataType;
import de.uks.dss.model.PredefinedDocumentDataConstants;

import java.beans.PropertyChangeEvent;
import java.util.Date;

import de.uks.dss.app.DocumentDataCheckManApp;
import de.uks.dss.model.Task;
import de.uks.dss.model.PredefinedDocumentDataConstants;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

public class SoftwareStoryController
{
	//model
	private Task storyRoot;
	private Task story;
	private Task newestAct;

	//view
	private HBox toolbar;
	private TextField titleText;
	private Pane counterTop;
	private Polyline polyLine;
	private Circle start;
	
	public SoftwareStoryController start()
	{
		story.addPropertyChangeListener(Task.PROPERTY_NAME, e -> handleTitleChange(e));
		story.addPropertyChangeListener(Task.PROPERTY_SUBTASKS, e -> handleNewAct(e));
		
		//view
		Button backButton = new Button("<<");
		titleText = new TextField(story.getName());
		toolbar.getChildren().addAll(backButton, titleText);
		
		backButton.setOnAction(e->switchToMenuScene(e));
		titleText.setOnAction(e->changeTitle(e));
		counterTop.setOnMouseClicked(e->createNewAct(e));
		
		start = new Circle(10, 10, 5);
		start.setStroke(Color.BLACK);
		counterTop.getChildren().add(start);
		
		polyLine = new Polyline();
		polyLine.setStrokeWidth(2);
		counterTop.getChildren().add(polyLine);
		
		//draw connections
		updateConnections();
		
		//load acts
		for(Task tmpAct : story.getSubTasks())
		{
			createAndInitNewActController(tmpAct);
		}
		
		return this;
	}
	
	private void handleTitleChange(PropertyChangeEvent evt)
	{
		if(evt.getNewValue() instanceof String)
		{	
			titleText.setText(evt.getNewValue().toString());
			titleText.positionCaret(titleText.getLength());
		}
	}
	
	private void handleNewAct(PropertyChangeEvent e)
	{
		if(e.getNewValue() instanceof Task)
		{
			createAndInitNewActController((Task) e.getNewValue());
		}
	}
	
	private void createNewAct(Event event)
	{
		MouseEvent mEvent = (MouseEvent) event; 

		if(mEvent.getPickResult().getIntersectedNode() != counterTop)
		{
			return;
		}

		Task oldLastAct = newestAct;

		newestAct = new Task()
				.withName("title?");
		newestAct.createTaskData()
			.withTag(DocumentDataType.LAYOUTINFO.toString() + ".xPos")
			.withValue(String.valueOf(mEvent.getX()));
		newestAct.createTaskData()
			.withTag(DocumentDataType.LAYOUTINFO.toString() + ".yPos")
			.withValue(String.valueOf(mEvent.getY()));
		newestAct.createTaskData()
			.withTag(DocumentDataType.DATE.toString())
			.withValue(PredefinedDocumentDataConstants.dateFormat.format(new Date(System.currentTimeMillis())));
		newestAct.createTaskData()
			.withTag(PredefinedDocumentDataConstants.LOCATION)
			.withValue("location?");
		newestAct.createPersons().withName(PredefinedDocumentDataConstants.PARTICIPANTS);
		newestAct.createPersons().withName(PredefinedDocumentDataConstants.RESPONSIBLES);

		if(oldLastAct != null)
		{
			oldLastAct.withSubTasks(newestAct);
		}
		
		story.withSubTasks(newestAct);
	}

	private void createAndInitNewActController(Task tmpAct)
	{
		ActController controller = new ActController()
			.withStoryController(this)
			.withCounterTop(counterTop)
			.withAct(tmpAct);
		controller.start();
		newestAct = tmpAct;
		
		updateConnections();
	}
	
	private void changeTitle(Event event)
	{
		story.withName(titleText.getText());
	}
	
	public void updateConnections()
	{
		polyLine.getPoints().clear();
		
		polyLine.getPoints().add(start.getCenterX());
		polyLine.getPoints().add(start.getCenterY());
		
		Task tmpAct = story.getSubTasks().first();
		while(tmpAct != null && !tmpAct.getTaskData().isEmpty())
		{
			Double xPos = Double.valueOf(tmpAct.getTaskData(DocumentDataType.LAYOUTINFO.toString() + ".xPos").getValue());
			Double yPos = Double.valueOf(tmpAct.getTaskData(DocumentDataType.LAYOUTINFO.toString() + ".yPos").getValue());
			
			ObservableList<Double> points = polyLine.getPoints();
			points.add(xPos);
			points.add(yPos);
			
			tmpAct = tmpAct.getSubTasks().filter(value -> {
				if (value.getParentTasks().getTaskData().filterTag(PredefinedDocumentDataConstants.STORY_CONTAINER).size() == 0)
					return true;
				else
					return false;
			}).first();
		}
	}

	private void switchToMenuScene(ActionEvent e)
	{
		DocumentDataCheckManApp.switchToMenuScene();
	}

	public Task getStory()
	{
		return story;
	}

	public void setStory(Task story)
	{
		this.story = story;
	}
	
	public SoftwareStoryController withStory(Task story)
	{
		setStory(story);
		return this;
	}
	

	public void setCounterTop(Pane counterTop)
	{
		this.counterTop = counterTop;
	}
	
	public SoftwareStoryController withCounterTop(Pane counterTop)
	{
		setCounterTop(counterTop);
		return this;
	}

	public void setToolbar(HBox toolbar)
	{
		this.toolbar = toolbar;
	}
	
	public SoftwareStoryController withToolbar(HBox toolbar)
	{
		setToolbar(toolbar);
		return this;
	}
	
	public Task getStoryRoot()
	{
		return storyRoot;
	}

	public void setStoryRoot(Task storyRoot)
	{
		this.storyRoot = storyRoot;
	}

	public SoftwareStoryController withStoryRoot(Task storyRoot) {
		setStoryRoot(storyRoot);
		return this;
	}
}
