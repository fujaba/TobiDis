package de.uks.dss.gui.controller;

import java.beans.PropertyChangeEvent;

import de.uks.dss.app.DocumentDataCheckManApp;
import de.uks.dss.model.Person;
import de.uks.dss.model.PredefinedDocumentDataConstants;
import de.uks.dss.model.Task;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.Condition;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class NodeController{

	private static final int TOOLTIP_Y_OFFSET = 5;
	private static final int TOOLTIP_X_OFFSET = 15;
	
	//model
	private Task task;
	private Task nextWorkflowDummy;

	//view
	private Pane counterTop;
	private StackPane nodePane;
	private Label nodeLabel;
	private Circle node;
	private Circle currentTask;
	private Circle nextWorkflowTask;
	private Tooltip tooltip;

	private double xColumn;
	private double yRow;

	public NodeController start()
	{
		nextWorkflowDummy = new Task().withName(PredefinedDocumentDataConstants.NEXTWORKFLOWTASK);
		
		currentTask = new Circle(0,0,10);
		currentTask.setStroke(Color.GREEN);
		currentTask.setFill(Color.GREEN);
		currentTask.setOpacity(0.5);
		
		nextWorkflowTask = new Circle(0,0,10);
		nextWorkflowTask.setStroke(Color.YELLOW);
		nextWorkflowTask.setFill(Color.YELLOW);
		nextWorkflowTask.setOpacity(0.5);
		
		showOnUIAndRegisterListeners();
		
		return this;
	}

	private void showOnUIAndRegisterListeners()
	{
		node = new Circle(0, 0, 5);
		node.setStroke(Color.BLACK);
		nodeLabel = new Label();
		nodeLabel.setMinWidth(3);
		nodeLabel.setMinHeight(3);
		tooltip = new Tooltip(task.getName());
		nodePane = new StackPane();
		nodePane.setLayoutX(getXColumn());
		nodePane.setLayoutY(getYRow());
		nodePane.getChildren().addAll(node, nodeLabel);
		counterTop.getChildren().add(nodePane);

		//showTooltipInstantly
		nodePane.setOnMouseEntered(event -> setOnMouseEntered(event));
		nodePane.setOnMouseExited(event -> setOnMouseExited());

		//draw transparent green node for current task
		task.addPropertyChangeListener(Task.PROPERTY_PERSONS, e->handleCurrentTask(e));
		task.addPropertyChangeListener(Task.PROPERTY_PARENTTASKS, e->handleNextWorkflowTask(e));

		//drag and drop
		nodePane.setOnDragDetected(e -> onDragDetected(e));
		nodePane.setOnDragOver(e -> onDragOver(e));
		nodePane.setOnDragDone(e -> onDragDone(e));
		nodePane.setOnDragDropped(e -> onDragDropped(e));

		if(task.getPersons().getGroupsTransitive().getName().contains(PredefinedDocumentDataConstants.RESPONSIBLES))
		{
			nodePane.getChildren().add(currentTask);
			task.getSubTasks().forEach(subTask -> subTask.withParentTasks(nextWorkflowDummy));
		}
	}

	private void setOnMouseEntered(MouseEvent event)
	{
		tooltip.show(nodePane.getScene().getWindow(), event.getScreenX()+TOOLTIP_X_OFFSET, event.getScreenY()+TOOLTIP_Y_OFFSET);
	}

	private void setOnMouseExited()
	{
		tooltip.hide();
	}

	private void handleCurrentTask(PropertyChangeEvent e)
	{
		if(task.getPersons().getGroupsTransitive().getName().contains(PredefinedDocumentDataConstants.RESPONSIBLES) && !nodePane.getChildren().contains(currentTask))
		{
			nodePane.getChildren().add(currentTask);
		}
		else if (!task.getPersons().getGroupsTransitive().getName().contains(PredefinedDocumentDataConstants.RESPONSIBLES))
		{
			nodePane.getChildren().remove(currentTask);
		}
	}
	
	private void handleNextWorkflowTask(PropertyChangeEvent e)
	{
		if(!task.getParentTasks().filterName(PredefinedDocumentDataConstants.NEXTWORKFLOWTASK).isEmpty() && !nodePane.getChildren().contains(nextWorkflowTask))
		{
			nodePane.getChildren().add(nextWorkflowTask);
		}
		else if (task.getParentTasks().filterName(PredefinedDocumentDataConstants.NEXTWORKFLOWTASK).isEmpty())
		{
			nodePane.getChildren().remove(nextWorkflowTask);
		}
	}

	//drag detected
	private void onDragDetected(MouseEvent e)
	{
		Person client = task.getPersons().filter(new Condition<Person>() {
			@Override
			public boolean update(Person value) {
				if(value.getGroupsTransitive().getName().contains(PredefinedDocumentDataConstants.RESPONSIBLES))
				{
					return true;
				}
				return false;
			}
		}).first();

		if(client != null)
		{
			IdMap idMap = DocumentDataCheckManApp.getIdMap();
			String clientId = idMap.getId(client);

			Dragboard db = nodePane.startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();
			content.putString(clientId);
			db.setContent(content);
		}

		e.consume();
	}

	//target of finished drag gesture
	private void onDragOver(DragEvent e)
	{
		//show hint
		if(e.getGestureSource() != e.getGestureTarget())
		{
			e.acceptTransferModes(TransferMode.MOVE);
			e.consume();
		}
		else
		{
			e.acceptTransferModes(TransferMode.NONE);
		}
	}

	//source of finished drag gesture
	private void onDragDone(DragEvent e)
	{
		TransferMode transferMode = e.getTransferMode();
		if(transferMode != null && transferMode == TransferMode.MOVE)
		{
			String clientId = e.getDragboard().getString();
			IdMap idMap = DocumentDataCheckManApp.getIdMap();
			Person movedClient = (Person) idMap.getObject(clientId);

			if(movedClient != null)
			{			
				task.withoutPersons(movedClient);
				task.getSubTasks().forEach(subTask -> subTask.withoutParentTasks(nextWorkflowDummy));
			}
			e.consume();
		}
	}

	//target of finished drag gesture
	private void onDragDropped(DragEvent e)
	{
		String clientId = e.getDragboard().getString();
		IdMap idMap = DocumentDataCheckManApp.getIdMap();
		Person movedClient = (Person) idMap.getObject(clientId);

		if(movedClient != null)
		{			
			task.withPersons(movedClient);
			task.getSubTasks().forEach(subTask -> subTask.withParentTasks(nextWorkflowDummy));
			e.setDropCompleted(true);
			e.consume();
		}
	}

	public Task getTask()
	{
		return this.task;
	}

	public void setTask(Task task)
	{
		this.task = task;
	}

	public NodeController withTask(Task task)
	{
		setTask(task);
		return this;
	}

	public void setCounterTop(Pane counterTop)
	{
		this.counterTop = counterTop;
	}

	public NodeController withCounterTop(Pane counterTop)
	{
		setCounterTop(counterTop);
		return this;
	}

	public double getXColumn()
	{
		return xColumn;
	}

	public void setXColumn(double xColumn)
	{
		this.xColumn = xColumn;
	}

	public NodeController withXColumn(double xColumn)
	{
		setXColumn(xColumn);
		return this;
	}

	public double getYRow() 
	{
		return yRow;
	}

	public void setYRow(double yRow)
	{
		this.yRow = yRow;
	}

	public NodeController withYRow(double yRow)
	{
		setYRow(yRow);
		return this;
	}
}
