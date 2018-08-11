package de.uks.se.dss.test;

import org.junit.Test;
import org.sdmlib.storyboards.Storyboard;

import de.uks.dss.engine.TaskFlowEngineHandler;
import de.uks.dss.engine.TaskFlowEngineHandler.ApplicationType;
import de.uks.dss.model.DocumentData;
import de.uks.dss.model.DocumentDataType;
import de.uks.dss.model.Person;
import de.uks.dss.model.PredefinedDocumentDataConstants;
import de.uks.dss.model.Task;

public class TestTaskFlowEngine {

	/**
	 * @see <a href='../../../../../../doc/SimpleTaskFlowTest.html'>SimpleTaskFlowTest.html</a>
	 */
	@Test
	public void testSimpleTaskFlow(){
		Storyboard storyBoard = new Storyboard();
				//.withRootDir("src")
				//.withName("SimpleTaskFlowTest");
		storyBoard.add("");

		Task root = new Task()
				.withName("UniKS verwalten");
		
		// register taskflow engine handler
		TaskFlowEngineHandler tfEngine = new TaskFlowEngineHandler()
			.withApplicationType(ApplicationType.StandAlone)
			.withUsername("tg")
			.withTaskflowContainer(root)
			.start();
		
		// create groups and persons
		Person seGroup = root.createPersons()
				.withName("SE Group");
		Person tg = seGroup.createMembers()
				.withName("tg");
		Person rb = seGroup.createMembers()
				.withName("rb");

		// create taskflow
		Task tf = root.createSubTasks()
				.withName("Tobias meldet an");
		tf.createTaskData()
			.withTag(PredefinedDocumentDataConstants.ACTIVE)
			.withType(DocumentDataType.BOOLEAN.toString())
			.withValue(PredefinedDocumentDataConstants.TRUE);

		Task t1 = tf.createSubTasks()
				.withPersons(tg);
		DocumentData anmelden = t1.createTaskData()
				.withTag(PredefinedDocumentDataConstants.NAME)
				.withValue("Anmelden");
		DocumentData done =  t1.createTaskData()
			.withTag(PredefinedDocumentDataConstants.DONE)
			.withType(DocumentDataType.BOOLEAN.toString())
			.withValue(PredefinedDocumentDataConstants.FALSE);

		Task t2 = tf.createSubTasks()
				.withName("Bestätigung bekommen")
				.withParentTasks(t1)
				.withPersons(seGroup);
		t2.createTaskData()
			.withTag(PredefinedDocumentDataConstants.DONE)
			.withType(DocumentDataType.BOOLEAN.toString())
			.withValue(PredefinedDocumentDataConstants.FALSE);

		//storyBoard.addObjectDiagram(story1.getSubTasks());

		// step: complete Anmelden
		done.withValue(PredefinedDocumentDataConstants.TRUE);
		
		//storyBoard.addObjectDiagram(result);

		storyBoard.assertTrue("Doesn't work!", t2.getPersons().contains(tg));
		storyBoard.assertTrue("Doesn't work!", t2.getPersons().contains(rb));

		//storyBoard.dumpHTML();
	}
}
