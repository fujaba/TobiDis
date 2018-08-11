package de.uks.se.dss.test;

import org.junit.Test;
import org.sdmlib.storyboards.Storyboard;

import de.uks.dss.generate.TaskFlowCloningMachine;
import de.uks.dss.model.Person;
import de.uks.dss.model.PredefinedDocumentDataConstants;
import de.uks.dss.model.Task;

public class TestCloneTaskFlow {

	@Test
	public void testSimpleTaskFlow(){
		Storyboard storyBoard = new Storyboard();
				//.withName("GenerateTaskFlowFromTwoStoriesWithSameCollectionTitle");
		storyBoard.add("From the following two stories one taskflow should be generated:");

		Task root = new Task()
				.withName("run SE Group");

		Person seGroup = root.createPersons()
				.withName("SE Group");
		Person tg = seGroup.createMembers()
				.withName("TG");

		Person students = root.createPersons()
				.withName("students");
		Person sc = seGroup.createMembers()
				.withName("SC");

		//first story
		Task taskflow = root.createSubTasks()
				.withName("supervise bachelor thesis")
				.withPersons(seGroup);

		Task firstTask = taskflow.createSubTasks()
				.withName("gather contact data")
				.withPersons(seGroup);
		firstTask.createTaskData()
		.withLastEditor("TG")
		.withLastModified(String.valueOf(System.currentTimeMillis()));

		Task secondTask = taskflow.createSubTasks()
				.withName("discuss topic")
				.withParentTasks(firstTask)
				.withPersons(seGroup);
		secondTask.createTaskData()
		.withLastEditor("TG")
		.withLastModified(String.valueOf(System.currentTimeMillis()));

		//storyBoard.addObjectDiagram(taskflow);

		TaskFlowCloningMachine.getInstance().cloneTaskflow(taskflow, root);

		//storyBoard.addObjectDiagram(root.getSubTasks());

		storyBoard.assertTrue("No active taskflow!", root.getSubTasks().filterName("supervise bachelor thesis").getTaskData()
				.filterTag(PredefinedDocumentDataConstants.ACTIVE).size() == 1);

		//storyBoard.dumpHTML();
	}

   /**
    * 
    * @see <a href='../../../../../../doc/GenerateTaskFlowFromStoryWithTwoSubstories.html'>GenerateTaskFlowFromStoryWithTwoSubstories.html</a>
 */
   @Test
	public void testStoryWithTwoSubstories(){
		Storyboard storyBoard = new Storyboard(); //.withRootDir("src").withName("GenerateTaskFlowFromStoryWithTwoSubstories");

		Task root = new Task()
				.withName("Uni verwalten");

		Person seGroup = root.createPersons()
				.withName("SE Group");
		Person ng = seGroup.createMembers()
				.withName("NG");

		//main story
		Task taskflow = root.createSubTasks()
				.withName("NG promoviert")
				.withPersons(seGroup);

		Task firstTask = taskflow.createSubTasks()
				.withName("Anmelden")
				.withPersons(ng);

		Task secondTask = taskflow.createSubTasks()
				.withParentTasks(firstTask)
				.withName("Prüfungskommission bilden")
				.withPersons(ng);

		//sub task 1
		Task firstSubTask = firstTask.createSubTasks()
				.withName("NG meldet an")
				.withName("Antrag einreichen")
				.withPersons(ng);

		//sub task 2
		Task secondSubTask = secondTask.createSubTasks()
				.withName("Kommission bilden")
				.withPersons(ng);

		// storyBoard.addObjectDiagram(root);
		
		TaskFlowCloningMachine.getInstance().cloneTaskflow(taskflow, root);

		storyBoard.assertTrue("No active taskflow!", root.getSubTasks().filterName("NG promoviert").getTaskData()
				.filterTag(PredefinedDocumentDataConstants.ACTIVE).size() == 1);

		//		storyBoard.dumpHTML();
	}
}
