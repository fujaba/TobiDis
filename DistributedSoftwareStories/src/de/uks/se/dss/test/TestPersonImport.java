package de.uks.se.dss.test;

import java.io.IOException;

import org.junit.Test;
import org.sdmlib.storyboards.Storyboard;

import de.uks.dss.importer.Importer;
import de.uks.dss.model.Task;

public class TestPersonImport {

	/**
	 * @see <a href='../../../../../../doc/SimpleTaskFlowTest.html'>SimpleTaskFlowTest.html</a>
	 * @see <a href='../../../../../../doc/ImportPersonsTest.html'>ImportPersonsTest.html</a>
 * @see <a href='../../../../../../doc/PersonImport.html'>PersonImport.html</a>
 */
	@Test
	public void testPersonImport(){
		Storyboard storyBoard = new Storyboard();
				//.withRootDir("src")
				//.withName("ImportPersonsTest");
		storyBoard.add("");

		Task root = new Task()
			.withName("run UniKasselTransfer");
		
		//import persons from Excel document
		//
		try
		{
			Importer.importPersons("./data/2015_07_28_Teilnehmerdaten_Ideenwettbewerb_2015.xlsx", root, "testUserName");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
//		storyBoard.addObjectDiagram(result);

		storyBoard.assertTrue("Keine 53 Teams!", root.getPersons().filterName("clients").getMembers().size() == 53);

		storyBoard.assertEquals("Keine 5 Member",5, root.getPersons().filterName("clients").first().getMembers()
				.filterName("betterspace")
				.first().getMembers().size());
		//storyBoard.dumpHTML();
	}
}
