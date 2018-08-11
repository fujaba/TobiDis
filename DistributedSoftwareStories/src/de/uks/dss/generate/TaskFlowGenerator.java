package de.uks.dss.generate;

import java.util.HashSet;
import java.util.Set;

import de.uks.dss.model.DocumentDataType;
import de.uks.dss.model.Person;
import de.uks.dss.model.PredefinedDocumentDataConstants;
import de.uks.dss.model.Task;
import de.uks.dss.model.util.DocumentDataSet;
import de.uks.dss.model.util.PersonSet;
import de.uks.dss.model.util.TaskSet;

public class TaskFlowGenerator
{

	private static TaskFlowGenerator instance;

	private TaskFlowGenerator(){}

	public static TaskFlowGenerator getInstance()
	{
		if (instance == null)
		{
			instance = new TaskFlowGenerator();
		}
		return instance;
	}

	/**
	 * @param
	 * storyRoot is the container for all software stories that will be combined to taskflows
	 * storyRoot contains tasks that represent single stories
	 * the stories contain tasks that represent acts
	 * acts may contain sub stories (which are contained in storyRoot too)
	 * 
	 * root is the container for all generated taskflows
	 */
	public void generateTaskFlows(Task storyRoot, Task root)
	{
		// put all names of all stories into a set to eliminate duplicates
		// skip sub stories - they get processed later
		Set<String> uniqueCollectionTitles = new HashSet<String>(((TaskSet)storyRoot.getSubTasks().filter(value -> {
			if(value.getParentTasks().size() > 1)
				return false;
			else
				return true;
		})).getName());

		for(String tmpCollectionTitle : uniqueCollectionTitles)
		{
			// corresponding taskflow already existing?
			// if not: create a taskflow for each unique collection title
			TaskSet candidates = root.getSubTasks().filterName(tmpCollectionTitle);
			Task newFlow = null;
			
			if(candidates.isEmpty())
			{				
				newFlow = root.createSubTasks();
				newFlow.withName(tmpCollectionTitle);
			}
			else
			{
				newFlow = candidates.first();
			}

			// combine all acts with the same title to one corresponding task in the taskflow
			TaskSet collection = storyRoot.getSubTasks().filterName(tmpCollectionTitle);
			TaskSet acts = collection.getSubTasksTransitive().minus(collection);
			
			for(Task tmpAct : acts)
			{	
				String tmpActTitle = tmpAct.getName();

				// corresponding task already existing?
				Task combinedTask = null;
				
				TaskSet allActNamesWithTmpActTitle = newFlow.getSubTasks().filterName(tmpActTitle);
				if(!allActNamesWithTmpActTitle.isEmpty())
				{
					combinedTask = allActNamesWithTmpActTitle.first();
				}
				else
				{	
					combinedTask = newFlow.createSubTasks();
					combinedTask.withName(tmpActTitle);
					//tasks are initially marked as not done
					combinedTask.createTaskData()
						.withTag(PredefinedDocumentDataConstants.DONE)
						.withType(DocumentDataType.BOOLEAN.toString())
						.withValue(PredefinedDocumentDataConstants.FALSE);
				}
				
				// successors
				for(Task tmpSuccessor : tmpAct.getSubTasks())
				{	
					String tmpSuccessorTitle = tmpSuccessor.getName();
					
					// corresponding task already existing?
					Task tmpSuccessorTask = null;
					TaskSet allSuccessorActsWithTmpSuccessorTitle = newFlow.getSubTasks().filterName(tmpSuccessorTitle);
					if(!allSuccessorActsWithTmpSuccessorTitle.isEmpty())
					{
						tmpSuccessorTask = allSuccessorActsWithTmpSuccessorTitle.first();
					}
					else
					{
						tmpSuccessorTask = newFlow.createSubTasks();
						tmpSuccessorTask.withName(tmpSuccessorTitle);
						
						// tasks are initially marked as not done
						tmpSuccessorTask.createTaskData()
							.withTag(PredefinedDocumentDataConstants.DONE)
							.withType(DocumentDataType.BOOLEAN.toString())
							.withValue(PredefinedDocumentDataConstants.FALSE);
						
						//mark sub stories
						if(tmpSuccessor.getParentTasks().contains(storyRoot))
						{
							tmpSuccessorTask.createTaskData()
							.withTag(PredefinedDocumentDataConstants.SUBSTORY)
							.withType(DocumentDataType.STRING.toString())
							.withValue(PredefinedDocumentDataConstants.TRUE);
						}
					}

					// add successor to subtasks
					combinedTask.withSubTasks(tmpSuccessorTask);
				}

				// responsibles and/or responsible groups:
				PersonSet tmpActPersons = tmpAct.getPersons().getMembers();/*.filter(p -> {
					if(p.getMembers().isEmpty())
					{
						return true;
					}
					else
						return false;
				});*/
				for(Person tmpPerson : tmpActPersons)
				{
					//students and/or clients can't be responsible
					if(tmpPerson.getGroupsTransitive().filterName(PredefinedDocumentDataConstants.STUDENTS).isEmpty() &&
						tmpPerson.getGroupsTransitive().filterName(PredefinedDocumentDataConstants.CLIENTS).isEmpty())
					{
						//participants only or person in responsibles and participants -> all member participant groups
						if(tmpAct.getPersons().filterName(PredefinedDocumentDataConstants.RESPONSIBLES).first().getMembers().isEmpty() ||
								(tmpAct.getPersons().filterName(PredefinedDocumentDataConstants.RESPONSIBLES).first().getMembers().contains(tmpPerson)
									&& tmpAct.getPersons().filterName(PredefinedDocumentDataConstants.PARTICIPANTS).first().getMembers().contains(tmpPerson)))
						{
							//each group (of responsible persons) with highest depth is responsible
							PersonSet groups = tmpPerson.getGroups().filter(g -> {
								if(g.getGroupsTransitive().getTasks().contains(root))
								{
									return true;
								}
								else
								{
									return false;
								}
							});
							Person responsibleGroup = null;
							int highestDepth = 0;
							
							for (Person group : groups) {						
								int depth = 1;
										
								for(; !group.getTasks().contains(root) /*&& !group.getGroups().isEmpty()*/; depth++)
								{
									group = group.getGroups().first();
								}
								
								if(depth > highestDepth)
								{
									highestDepth = depth;
									responsibleGroup = group;
								}
							}
							
							combinedTask.withPersons(responsibleGroup);
						}
						// person only in responsibles -> person directly
						else if(tmpAct.getPersons().filterName(PredefinedDocumentDataConstants.RESPONSIBLES).first().getMembers().contains(tmpPerson))
						{
							combinedTask.withPersons(tmpPerson);
						}
					}
				}
			}
			
			TaskSet tasksToRemove = pullUpSubStoryTasks(newFlow, newFlow);
			for(Task tmpTask : tasksToRemove)
			{
				tmpTask.removeYou();
			}
		}
	}

	// recursively pull up all sub story tasks
	private TaskSet pullUpSubStoryTasks(Task newFlow, Task parent) {
		TaskSet tasksToAdd = new TaskSet();
		TaskSet tasksToRemove = new TaskSet();
		
		for(Task tmpTask : parent.getSubTasks())
		{	
			DocumentDataSet substoryData = tmpTask.getTaskData().filterTag(PredefinedDocumentDataConstants.SUBSTORY);
			if( ! substoryData.isEmpty() && substoryData.getValue().first().equals(PredefinedDocumentDataConstants.TRUE))
			{
				/*Task firstSubStoryAct = tmpTask.getSubTasks().filter(value -> {
					if(value.getParentTasks().size() == 1)
						return true;
					else
						return false;
				}).first();
				
				//Task predecessor = parent.getParentTasks().without(newFlow).first();
				Task regularSuccessor = parent.getSubTasks().without(tmpTask).first();
				
				Task lastSubStoryAct = tmpTask.getSubTasks().filter(value -> {
					if(value.getSubTasks().isEmpty())
						return true;
					else
						return false;
				}).first();
				
				
				// connect
				// first act with parent predecessor 
				// last act with regular successor
				// to fill the gap
				firstSubStoryAct.withParentTasks(parent);
				lastSubStoryAct.withSubTasks(regularSuccessor);*/
				
				tasksToAdd = tmpTask.getSubTasks();
				tasksToRemove.add(tmpTask);
			}
			
			TaskSet subsubstoryTasks = pullUpSubStoryTasks(newFlow, tmpTask);
			tasksToRemove.with(subsubstoryTasks);
		}
		
		for(Task tmpTask : tasksToAdd)
		{
			parent.withSubTasks(tmpTask);
		}
		
		return tasksToRemove;
	}
}
