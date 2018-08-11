package de.uks.dss.generate;

import de.uks.dss.model.Task;
import de.uks.dss.model.util.PersonSet;
import de.uks.dss.model.util.TaskSet;
import de.uks.dss.model.DocumentData;
import de.uks.dss.model.DocumentDataType;
import de.uks.dss.model.Person;
import de.uks.dss.model.PredefinedDocumentDataConstants;

public class TaskFlowCloningMachine {

	private static TaskFlowCloningMachine instance;

	private TaskFlowCloningMachine(){}

	public static TaskFlowCloningMachine getInstance()
	{
		if (instance == null)
		{
			instance = new TaskFlowCloningMachine();
		}
		return instance;
	}

	/**
	 * clones a taskflow and adds the clone to the taskflow container
	 * @param taskflowToClone
	 * @param taskflowContainer
	 */
	public void cloneTaskflow(Task taskflowToClone, Task taskflowContainer) {
		String selectedTaskflowName = taskflowToClone.getName();
		Task newTaskFlow = new Task().withName(selectedTaskflowName);
		cloneTaskflow(newTaskFlow, taskflowToClone, selectedTaskflowName);
		
		newTaskFlow.createTaskData()
			.withTag(PredefinedDocumentDataConstants.ACTIVE)
			.withValue(PredefinedDocumentDataConstants.TRUE)
			.withType(DocumentDataType.BOOLEAN.toString());
		taskflowContainer.withSubTasks(newTaskFlow);
	}
	
	private Task cloneTaskflow(Task clone, Task taskToClone, String taskflowName) {
		//responsible groups/persons
		PersonSet persons = taskToClone.getPersons();
		clone.withPersons(persons.toArray(new Person[persons.size()]));
		
		//doc data including sub data
		for (DocumentData tmpData : taskToClone.getTaskData())
		{
			DocumentData clonedData = cloneTaskData(tmpData);
			clone.withTaskData(clonedData);
		}
		
		//subtasks
		for (Task tmpSubTask : taskToClone.getSubTasks())
		{
			TaskSet subtasksWithSameName = clone.getSubTasks().filterName(tmpSubTask.getName());
			Task tmpSubTaskClone = subtasksWithSameName.first();
			if(tmpSubTaskClone == null)
			{
				tmpSubTaskClone = new Task().withName(tmpSubTask.getName());
				clone.withSubTasks(tmpSubTaskClone);
				
				//always:
				tmpSubTaskClone = cloneTaskflow(tmpSubTaskClone, tmpSubTask, taskflowName);
			}
			
			if(!tmpSubTask.getParentTasks().filterName(taskflowName).isEmpty())
			{
				Task taskflow = clone.getParentTasksTransitive().filterName(taskflowName).first();
				tmpSubTaskClone.withParentTasks(taskflow);
			}
		}
		
		return clone;
	}

	private DocumentData cloneTaskData(DocumentData tmpData) {
		DocumentData clonedData = new DocumentData()
			.withTag(tmpData.getTag())
			.withValue(tmpData.getValue())
			.withType(tmpData.getType());
		
		for (DocumentData tmpSubData : tmpData.getSubData())
		{
			DocumentData clonedSubData = cloneTaskData(tmpSubData);
			clonedData.withSubData(clonedSubData);
		}
		
		return clonedData;
	}
}
