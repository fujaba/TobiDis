package de.uks.dss.engine;

import java.beans.PropertyChangeEvent;

import de.uks.dss.model.DocumentData;
import de.uks.dss.model.Person;
import de.uks.dss.model.PredefinedDocumentDataConstants;
import de.uks.dss.model.Task;
import de.uks.dss.model.util.PersonSet;
import de.uks.dss.model.util.TaskSet;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;
import javafx.application.Platform;

/**
 * 
 * @see <a href='../../../../../src/de/uks/se/dss/test/TestTaskFlowEngine.java'>TestTaskFlowEngine.java</a>
 */
public class TaskFlowEngineHandler
{
	public enum ApplicationType {StandAlone, JavaFX};
	private  ApplicationType applicationType;

	private Task taskflowContainer;
	private String username;

	//register at taskflowcontainer to react on groups to handle
	public TaskFlowEngineHandler start()
	{	
		getTaskflowContainer().addPropertyChangeListener(Task.PROPERTY_PERSONS, (e)->scanForMembers(e));
		return this;
	}
	
	
	private void scanForMembers(PropertyChangeEvent e)
	{
		if(e.getNewValue() != null)
		{
			((Person) e.getNewValue()).addPropertyChangeListener(Person.PROPERTY_MEMBERS, (evt)->scanNameForUsername(evt));
		}
	}
	
	private void scanNameForUsername(PropertyChangeEvent e)
	{		
		if(e.getNewValue() != null)
		{
			((Person) e.getNewValue()).addPropertyChangeListener(Person.PROPERTY_NAME, (evt)->registerAtPersonToHandle(evt));
		}
	}
	
	//register at that persons tasks to react on completed tasks
	private void registerAtPersonToHandle(PropertyChangeEvent e)
	{
		if(e.getNewValue() != null && e.getNewValue().equals(username))
		{			
			Person personToHandle = (Person)e.getSource();
			personToHandle.addPropertyChangeListener(Person.PROPERTY_TASKS, (evt)->handleNewTask(evt));
		}
	}

	private void handleNewTask(PropertyChangeEvent e)
	{
		if(e.getNewValue() != null)
		{
			((Task) e.getNewValue()).addPropertyChangeListener(Task.PROPERTY_TASKDATA, (evt)->handleNewTaskData(evt));
		}
	}

	private void handleNewTaskData(PropertyChangeEvent e)
	{
		if(e.getNewValue() != null)
		{
			((DocumentData) e.getNewValue()).addPropertyChangeListener(DocumentData.PROPERTY_VALUE, (evt)->checkIfTaskIsCompleted((evt)));
		}
	}

	// check if there is a completed task & hand out etc.
	private void checkIfTaskIsCompleted(PropertyChangeEvent e)
	{
		if(e.getSource() != null && ((DocumentData)e.getSource()).getTasks() != null
				&& ((DocumentData)e.getSource()).getTasks().size() == 1)
		{
			Task candidate = ((DocumentData)e.getSource()).getTasks().first();

			//is there task data where tag is "done" and value is "true"
			SimpleSet<DocumentData> taskDataDoneIsTrue = candidate.getTaskData().filter(taskData ->
			{
				if(PredefinedDocumentDataConstants.DONE.equals(taskData.getTag()) && PredefinedDocumentDataConstants.TRUE.equals(taskData.getValue()))
				{
					return true;
				}
				else
				{
					return false;							
				}
			});
			boolean taskIsDone = taskDataDoneIsTrue.size() == 1;

			//hand out successors to responsibles,
			//move client(s) to last finished task
			//TODO remove unfinished predecessors from responsibles
			if(taskIsDone)
			{	
				//taskflow has taskdata: tag= active, value= true
				Task taskflow = candidate.getParentTasksTransitive().filter((task ->
				{
					DocumentData taskDataActive = task.getTaskData().filterTag(PredefinedDocumentDataConstants.ACTIVE).first();
					if(taskDataActive != null && PredefinedDocumentDataConstants.TRUE.equals(taskDataActive.getValue()))
					{
						return true;
					}
					else
					{
						return false;							
					}
				})).first();
				
				//hand out successors
				TaskSet successors = candidate.getSubTasks();
				for (Task tmpSuccessor : successors)
				{	
					PersonSet responsibles = tmpSuccessor.getPersons().getMembers();

					if(applicationType == ApplicationType.JavaFX)
					{
						Platform.runLater(()->
						{
							tmpSuccessor.withPersons(responsibles.toArray(new Person[responsibles.size()]));
							//TODO remove listener??
						});
					}
					else
					{
						tmpSuccessor.withPersons(responsibles.toArray(new Person[responsibles.size()]));
					}
				}
			}
		}		
	}
	
	private void handOutAndMoveClientsAndRemoveUnfinished()
	{
		
	}

	public Task getTaskflowContainer() {
		return taskflowContainer;
	}

	public void setTaskflowContainer(Task taskflowContainer) {
		this.taskflowContainer = taskflowContainer;
	}

	public TaskFlowEngineHandler withTaskflowContainer(Task taskflowContainer) {
		setTaskflowContainer(taskflowContainer);
		return this;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public TaskFlowEngineHandler withUsername(String username) {
		setUsername(username);
		return this;
	}
	
	public ApplicationType getApplicationType()
	{
		return applicationType;
	}

	public void setApplicationType(ApplicationType applicationType)
	{
		this.applicationType = applicationType;
	}
	
	public TaskFlowEngineHandler withApplicationType(ApplicationType applicationType)
	{
		setApplicationType(applicationType);
		return this;
	}
}