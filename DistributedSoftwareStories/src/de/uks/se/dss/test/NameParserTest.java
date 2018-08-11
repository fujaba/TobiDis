package de.uks.se.dss.test;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uks.dss.model.DocumentData;
import de.uks.dss.model.DocumentDataType;
import de.uks.dss.model.Person;
import de.uks.dss.model.PredefinedDocumentDataConstants;
import de.uks.dss.model.Task;
import de.uks.dss.parser.nameLayout.NameParser;

public class NameParserTest {

    private static Task root;
    private static Person person;

    @Test
    public void testName() {
	Person person = new Person();
	person.createPersonData().withTag(PredefinedDocumentDataConstants.NAME).withValue("Susi");

	Task root = new Task();
	root.createTaskData().withTag(PredefinedDocumentDataConstants.NAME).withValue("TaskName");
	root.withPersons(person);

	NameParser.setRoot(root);

	String taskName = NameParser.getName(root, "<name>");

	assertEquals("TaskName", taskName);

	String personName = NameParser.getName(person);

	assertEquals("Susi", personName);
    }

    @BeforeClass
    public static void init() {
	person = new Person();
	person.createPersonData().withTag(PredefinedDocumentDataConstants.NAME).withValue("Sorglos");
	person.createPersonData().withTag("surname").withValue("Susi");
	person.createPersonData().withTag(DocumentDataType.EMAIL.toString()).withValue("susisorglos@mail.com");

	root = new Task();
	root.createTaskData().withTag(PredefinedDocumentDataConstants.NAME).withValue("TaskName");
	root.withPersons(person);
	root.createTaskData().withTag("description").withValue("a Task");
	root.createTaskData().withTag(Task.class.toGenericString()).withValue("<name> <description>")
		.withType(DocumentDataType.LAYOUTINFO.toString());
	root.createTaskData().withTag(Person.class.toGenericString()).withValue("<name>, <surname>")
		.withType(DocumentDataType.LAYOUTINFO.toString());

	NameParser.setRoot(root);
    }

    @Test
    public void testLocaleOverGlobalName() {
	DocumentData rootUser = new DocumentData().withType("user").withTag("user").withValue("userName");
	rootUser.createSubData().withTag(Task.class.toGenericString()).withValue("<name> <description>")
		.withType(DocumentDataType.LAYOUTINFO.toString());
	rootUser.createSubData().withTag(Person.class.toGenericString()).withValue("<name>, <surname>")
		.withType(DocumentDataType.LAYOUTINFO.toString());

	NameParser.setRootUser(rootUser);

	String taskName = NameParser.getName(root);

	assertEquals("TaskName a Task", taskName);

	String personName = NameParser.getName(person);

	assertEquals("Sorglos, Susi", personName);

	NameParser.setRootUser(null);

    }

    @Test
    public void testGrammar() {
	String parsePerson = NameParser.getName(person, "<name>");

	assertEquals("Sorglos", parsePerson);
    }

    @Test
    public void testGrammarOptionalMatching() {
	String parsePerson = NameParser.getName(person, "<name>(, <surname>)");

	assertEquals("Sorglos, Susi", parsePerson);
    }

    @Test
    public void testGrammarOptionalMatchingCharBeforeTag() {
	String parsePerson = NameParser.getName(person, ":<name>");

	assertEquals(":Sorglos", parsePerson);
    }

    @Test
    public void testGrammarOptionalMatchingCharAfterTag() {
	String parsePerson = NameParser.getName(person, "<surname>,");

	assertEquals("Susi,", parsePerson);
    }

    @Test
    public void testGrammarOptionalNotMatching() {
	String parsePerson = NameParser.getName(person, "<name>(, <EMail>)");

	assertEquals("Sorglos", parsePerson);
    }

    @Test
    public void testGrammarOptionalInsideOptionalNotMatching() {
	// matching: name, E-Mail
	// not matching: Vorname
	String parsePerson = NameParser.getName(person, "<name>(, <Vorname>( - <E-Mail>))");

	assertEquals("Sorglos", parsePerson);
    }

    @Test
    public void testGrammarOptionalInsideOptionalMatching() {
	// matching: name, E-Mail, surname
	String parsePerson = NameParser.getName(person, "<name>(, <surname>( - <E-Mail>))");

	assertEquals("Sorglos, Susi - susisorglos@mail.com", parsePerson);
    }
}
