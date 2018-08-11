package de.uks.dss.gui.controller;

import static de.uks.dss.model.PredefinedDocumentDataConstants.ACTIVE;
import static de.uks.dss.model.PredefinedDocumentDataConstants.STORY_CONTAINER;

import java.util.HashMap;
import java.util.Map.Entry;

import de.uks.dss.app.DocumentDataCheckManApp;
import de.uks.dss.generate.TaskFlowCloningMachine;
import de.uks.dss.gui.autocompletion.AutoCompletionComboBoxTask;
import de.uks.dss.model.Person;
import de.uks.dss.model.Task;
import de.uks.dss.model.util.TaskSet;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.StringList;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class OverviewController
{
	private static final String ESCAPE_CHAR = "";
	private static final String ENTER_CHAR = "\r";

	//app
	DocumentDataCheckManApp app;
	
	//model
	private Task taskflowContainer;
	
	private Person movedClient;
	private Task movedClientTask;

	//view
	private HBox toolbar;
	private Pane counterTop;
	private Label lastTaskflowItem;
	
	//controller
	private HashMap<Task, NodeController> nodeControllers;
	
	public OverviewController start()
	{
		nodeControllers = new HashMap<Task, NodeController>();
		
		//view
		Button backButton = new Button("<<");
		toolbar.getChildren().addAll(backButton);
		backButton.setOnAction(e->switchToMenuScene(e));
		
		drawGraphs();
		
		return this;
	}

	private void drawGraphs() {
		SimpleSet<Task> taskflows = taskflowContainer.getSubTasks().filter(value -> {
			if(value.getTaskData(STORY_CONTAINER) == null && value.getTaskData(ACTIVE)!=null && Boolean.valueOf(value.getTaskData(ACTIVE).getValue()))
			{
				return true;
			}
			else
			{
				return false;
			}
		});
		
		int xColumn = 50;
		int yRow = 50;
		
		// draw a graph for every taskflow
		for (Task taskFlow : taskflows)
		{
			yRow = 50;
			
			Label titleLabel = new Label(taskFlow.getName());
			titleLabel.setLayoutX(xColumn);
			titleLabel.setLayoutY(10);
			counterTop.getChildren().add(titleLabel);
			
			// insert a circle for every task
			for(Task task : taskFlow.getSubTasksTransitive().minus(taskFlow))
			{
				NodeController nodeController = new NodeController()
						.withTask(task)
						.withCounterTop(counterTop)
						.withXColumn(xColumn)
						.withYRow(yRow);
				nodeController.start();
				nodeControllers.put(task, nodeController);
				
				yRow +=100;
			}
			xColumn += 150;
		}
		
		updateConnections();
		
		createAndAddLastTaskflowItem(xColumn, 10);
	}
	
	private void createAndAddLastTaskflowItem(double x, double y)
	{
		lastTaskflowItem = new Label("new taskflow?");
		lastTaskflowItem.setLayoutX(x);
		lastTaskflowItem.setLayoutY(y);
		lastTaskflowItem.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		lastTaskflowItem.setOnMouseClicked(e -> offerTaskflowSelection());
		counterTop.getChildren().add(lastTaskflowItem);
	}

	private void offerTaskflowSelection() {
		TaskSet taskflows = new TaskSet();
		taskflows.addAll(taskflowContainer.getSubTasks().filter(value -> {
			//just taskflows && which are templates
			if(value.getTaskData(STORY_CONTAINER) == null && value.getTaskData(ACTIVE) == null)
			{
				return true;
			}
			else
			{
				return false;
			}
		}));
		StringList taskflowNames = taskflows.getName();

		double xPos = lastTaskflowItem.getLayoutX();
		double yPos = lastTaskflowItem.getLayoutY();
		AutoCompletionComboBoxTask taskflowComboBox = new AutoCompletionComboBoxTask(FXCollections.observableArrayList(taskflows));
		taskflowComboBox.setMaxWidth(140);
		taskflowComboBox.setLayoutX(xPos);
		taskflowComboBox.setLayoutY(yPos);
        taskflowComboBox.setPromptText("which taskflow?");
        taskflowComboBox.setOnValueSet(this::startNewTaskflow);
        
        double newXPos = xPos + 150;
        counterTop.getChildren().remove(lastTaskflowItem);
        counterTop.getChildren().add(taskflowComboBox);
        createAndAddLastTaskflowItem(newXPos, yPos);
	}
	
	private void autoComplete(ComboBox<String> taskflowComboBox, ObservableValue<? extends String> ov, String o, String n)
	{
		if (n.equals(taskflowComboBox.getSelectionModel().getSelectedItem()))
		{
			return;
		}

		final ObservableList<String> items = FXCollections.observableArrayList(taskflowComboBox.getItems());

		taskflowComboBox.hide();
		final FilteredList<String> filtered = items.filtered(s -> s.toLowerCase().contains(n.toLowerCase()));
		if (filtered.isEmpty()) {
			taskflowComboBox.getItems().setAll(items);
		} else {
			taskflowComboBox.getItems().setAll(filtered);
			taskflowComboBox.show();
		}
	}

	private void startNewTaskflow(Task newTask)
	{
//		if(e.getCharacter().equals(ESCAPE_CHAR))
//		{
//			taskflowComboBox.getEditor().setText("");
//			return;
//		}
		
		String selectedTaskflowName = newTask.getName();
//		if(! e.getCharacter().equals(ENTER_CHAR) || !taskflowComboBox.getItems().contains(selectedTaskflowName))
//		{
//			return;
//		}
		
		// just the taskflow && which is a template && has the right name
		Task taskflowToClone = taskflowContainer.getSubTasks().filter(value -> {
			if(value.getTaskData(STORY_CONTAINER) == null && value.getTaskData(ACTIVE) == null && value.getName().equals(selectedTaskflowName))
			{
				return true;
			}
			else
			{
				return false;
			}
		}).first();
		
		// clone taskflow
		TaskFlowCloningMachine.getInstance().cloneTaskflow(taskflowToClone, taskflowContainer);
		
		// refresh ui
		nodeControllers.clear();
		toolbar.getChildren().clear();
		counterTop.getChildren().clear();
		
		start();
	}

	public void updateConnections()
	{
		for (Entry<Task, NodeController> tmpEntry : nodeControllers.entrySet())
		{
			Task tmpTask = tmpEntry.getKey();
			NodeController tmpNodeController = tmpEntry.getValue();
			for (Task tmpSuccessor : tmpTask.getSubTasks()) {
				NodeController tmpSuccessorController = nodeControllers.get(tmpSuccessor);
				
				double startX = tmpNodeController.getXColumn()+15;
				double startY = tmpNodeController.getYRow()+8;
				double endX = startX; // always same column
				double endY = tmpSuccessorController.getYRow()+8;
				
				if(endY < startY || (endY - startY) > 105)
				{
					Line connection1 = new Line(startX, startY, endX+30, (endY - startY)/2+startY);
					connection1.setStroke(Color.BLACK);
					connection1.setStrokeWidth(2);
					counterTop.getChildren().add(connection1);
					
					startX = endX+30;
					startY = (endY - startY)/2+startY;
					
					Line connection2 = new Line(startX, startY, endX, endY);
					connection2.setStroke(Color.BLACK);
					connection2.setStrokeWidth(2);
					counterTop.getChildren().add(connection2);
				}
				else
				{	
					Line connection = new Line(startX, startY, endX, endY);
					connection.setStroke(Color.BLACK);
					connection.setStrokeWidth(2);
					counterTop.getChildren().add(connection);
				}
				
				Polygon arrow = new Polygon();
				arrow.setStroke(Color.BLACK);
				arrow.setFill(Color.WHITE);
                arrow.getPoints().addAll(new Double[]{
                            0.0, 5.0,
                            -5.0, -5.0,
                            5.0, -5.0});

                double angle = Math.atan2(endY - startY, endX - startX) * 180 / 3.14;

                arrow.setRotate((angle - 90));
                arrow.setTranslateX(startX);
                arrow.setTranslateY(startY);
                arrow.setTranslateX(endX);
                arrow.setTranslateY(endY);
                
                counterTop.getChildren().add(arrow);
			}
		}
	}

	private void switchToMenuScene(ActionEvent e)
	{
		app.switchToMenuScene();
	}
	
	public DocumentDataCheckManApp getApp()
	{
		return app;
	}

	public void setApp(DocumentDataCheckManApp app)
	{
		this.app = app;
	}
	
	public OverviewController withApp(DocumentDataCheckManApp app)
	{
		setApp(app);
		return this;
	}

	public Task getTaskflowContainer()
	{
		return taskflowContainer;
	}

	public void setTaskflowContainer(Task taskflowContainer)
	{
		this.taskflowContainer = taskflowContainer;
	}
	
	public OverviewController withTaskflowContainer(Task taskflowContainer)
	{
		setTaskflowContainer(taskflowContainer);
		return this;
	}

	public Person getMovedClient() {
		return movedClient;
	}

	public void setMovedClient(Person movedClient) {
		this.movedClient = movedClient;
	}
	
	public OverviewController withMovedClient(Person movedClient) {
		setMovedClient(movedClient);
		return this;
	}
	
	public Task getMovedClientTask()
	{
		return movedClientTask;
	}

	public void setMovedClientTask(Task movedClientTask)
	{
		this.movedClientTask = movedClientTask;
	}
	
	public OverviewController withMovedClientTask(Task movedClientTask)
	{
		setMovedClientTask(movedClientTask);
		return this;
	}

	public void setCounterTop(Pane counterTop)
	{
		this.counterTop = counterTop;
	}
	
	public OverviewController withCounterTop(Pane counterTop)
	{
		setCounterTop(counterTop);
		return this;
	}

	public void setToolbar(HBox toolbar)
	{
		this.toolbar = toolbar;
	}
	
	public OverviewController withToolbar(HBox toolbar)
	{
		setToolbar(toolbar);
		return this;
	}
}
