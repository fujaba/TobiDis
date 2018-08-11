package de.uks.se.dss.test;

import org.junit.Test;
import org.sdmlib.storyboards.Storyboard;

import de.uks.dss.generate.TaskFlowGenerator;
import de.uks.dss.model.Person;
import de.uks.dss.model.PredefinedDocumentDataConstants;
import de.uks.dss.model.Task;

public class TestStoryToTaskFlow {

	/**
	 * 
	 * @see <a href='../../../../../../doc/GenerateTaskFlowFromTwoStoriesWithSameCollectionTitle.html'>GenerateTaskFlowFromTwoStoriesWithSameCollectionTitle.html</a>/n 
	 */
	@Test
	public void testTwoStoriesToOneTaskFlow(){
		Storyboard storyBoard = new Storyboard();
				//.withRootDir("src")
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
		Person cr = seGroup.createMembers()
				.withName("CR");

		Task storyRoot = root.createSubTasks();
		storyRoot.createTaskData().withTag(PredefinedDocumentDataConstants.STORY_CONTAINER);
		
		//first story
		Task story1 = storyRoot.createSubTasks()
				.withName("supervise bachelor thesis")
				.withPersons(tg,sc);
			
		Task firstActS1 = story1.createSubTasks()
				.withName("gather contact data")
				.withPersons(tg);
			firstActS1.createTaskData()
			.withLastEditor("TG")
			.withLastModified(String.valueOf(System.currentTimeMillis()));
		
		Task act2S1 = story1.createSubTasks()
				.withName("discuss topic")
				.withParentTasks(firstActS1)
				.withPersons(tg);
		act2S1.createTaskData()
			.withLastEditor("TG")
			.withLastModified(String.valueOf(System.currentTimeMillis()));
		storyBoard.addObjectDiagram(story1.getSubTasks());

		//second story
		Task story2 = storyRoot.createSubTasks()
				.withName("supervise bachelor thesis")
				.withPersons(tg,cr);
			
		Task firstActS2 = story2.createSubTasks()
				.withName("gather contact data")
				.withPersons(tg);
		firstActS2.createTaskData()
			.withLastEditor("TG")
			.withLastModified(String.valueOf(System.currentTimeMillis()));
		
		Task act2S2 = story2.createSubTasks()
				.withName("discuss topic")
				.withParentTasks(firstActS2)
				.withPersons(tg);
		act2S2.createTaskData()
			.withLastEditor("TG")
			.withLastModified(String.valueOf(System.currentTimeMillis()));
		storyBoard.addObjectDiagram(story2.getSubTasks());

		TaskFlowGenerator.getInstance().generateTaskFlows(storyRoot, root);
		
		storyBoard.addObjectDiagram(root.getSubTasks());

		storyBoard.assertTrue("Not 1 workflow!", root.getSubTasks().filterName("supervise bachelor thesis").size() == 1);
		storyBoard.assertTrue("Not 2 subtasks!", root.getSubTasks().filterName("supervise bachelor thesis").getSubTasks().size() == 2);
		storyBoard.assertTrue("No group responsible!", root.getSubTasks().filterName("supervise bachelor thesis").first().getSubTasks().filterName("gather contact data").getPersons().isEmpty());
	
		//storyBoard.dumpHTML();
	}

   /**
    * 
    * @see <a href='../../../../../../doc/GenerateTaskFlowFromStoryWithTwoSubstories.html'>GenerateTaskFlowFromStoryWithTwoSubstories.html</a>
 */
   @Test
	public void testStoryWithTwoSubstories(){
		Storyboard storyBoard = new Storyboard();//.withRootDir("src").withName("GenerateTaskFlowFromStoryWithTwoSubstories");

		Task root = new Task()
				.withName("Uni verwalten");
		
		Task storyRoot = root.createSubTasks();
		storyRoot.createTaskData().withTag(PredefinedDocumentDataConstants.STORY_CONTAINER);
		
		Person seGroup = root.createPersons()
				.withName("SE Group");
		Person ng = seGroup.createMembers()
				.withName("NG");

		//main story
		Task story1 = storyRoot.createSubTasks()
				.withName("NG promoviert")
				.withPersons(ng);
			
		Task firstActS1 = story1.createSubTasks()
				.withName("Anmelden")
				.withPersons(ng);
		
		Task secondActS1 = story1.createSubTasks()
				.withParentTasks(firstActS1)
				.withName("Prüfungskommission bilden")
				.withPersons(ng);

		//sub story 1
			Task subStory1 = storyRoot.createSubTasks()
					.withName("NG meldet an")
					.withPersons(ng);
				
			Task firstActSS1 = subStory1.createSubTasks()
					.withName("Antrag einreichen")
					.withPersons(ng);
		
		//sub story 2
				Task subStory2 = storyRoot.createSubTasks()
				.withName("NGs Promoverfahren wird eröffnet")
				.withPersons(ng);
			
		Task firstActSS2 = subStory2.createSubTasks()
				.withName("Kommission bilden")
				.withPersons(ng);

		// link substory1 to first act of main story and substory2 to second act
		firstActS1.withSubTasks(subStory1);
		secondActS1.withSubTasks(subStory2);
		
//		storyBoard.addObjectDiagram(root);
		
		TaskFlowGenerator.getInstance().generateTaskFlows(storyRoot, root);
		

		storyBoard.assertTrue("Doesn't work!", root.getSubTasks().filterName("NG promoviert").size() == 1);
		
		Task newFlow = root.getSubTasks().filterName("NG promoviert").first();
		storyBoard.assertTrue("Doesn't work!", newFlow.getSubTasks().size() == 4);
//		storyBoard.assertTrue("Doesn't work!", newFlow.getSubTasks().getSubTasks().filterName("Antrag einreichen").size() == 1);
//		storyBoard.assertTrue("Doesn't work!", newFlow.getSubTasks().getSubTasks().filterName("Kommission bilden").size() == 1);
	   
//		storyBoard.dumpHTML();
	}
}
