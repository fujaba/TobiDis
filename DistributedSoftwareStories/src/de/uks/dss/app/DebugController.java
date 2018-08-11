package de.uks.dss.app;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.sdmlib.modelcouch.CouchDBAdapter;
import org.sdmlib.modelcouch.ModelCouch;
import org.sdmlib.modelcouch.connection.ReturnObject;

import de.uks.dss.gui.autocompletion.AutoCompletionDialogPerson;
import de.uks.dss.gui.autocompletion.AutoCompletionDialogTask;
import de.uks.dss.importer.Importer;
import de.uks.dss.model.Person;
import de.uks.dss.model.Task;
import de.uks.dss.model.util.PersonSet;
import de.uks.dss.model.util.TaskSet;
import de.uks.dss.parser.nameLayout.NameParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DebugController {

    @FXML
    Button replicateFromBtn;
    @FXML
    Button replicateToBtn;
    @FXML
    Button importPersonsBtn;
    private ModelCouch modelCouch;
    private ModelCouch modelCouchLayout;
    private Task root;
    private String userName;
    private Button showDebugBtn;

    public void initialize(Button showDebugBtn, Stage stage) {
	this.showDebugBtn = showDebugBtn;
	this.modelCouch = DocumentDataCheckManApp.getModelCouch();
	this.modelCouchLayout = DocumentDataCheckManApp.getModelCouchLayout();
	root = DocumentDataCheckManApp.getRoot();
	userName = DocumentDataCheckManApp.getUsername();

	showDebugBtn.disableProperty().bind(stage.showingProperty());
    }

    @FXML
    public void replicateToDocker(ActionEvent event) {
	ReturnObject replicate = modelCouch.getCouchDBAdapter().replicate(modelCouch, modelCouch.getDatabaseName(),
		"http://docker.cs.uni-kassel.de:5984/ddcm");
	if (replicate.getResponseCode() >= 400) {
	    System.err.println(replicate.getError());
	}
    }

    @FXML
    public void replicateFromDocker(ActionEvent event) {
	ReturnObject replicate = modelCouch.getCouchDBAdapter().replicate(modelCouch,
		"http://docker.cs.uni-kassel.de:5984/ddcm", modelCouch.getDatabaseName());
	if (replicate.getResponseCode() >= 400) {
	    System.err.println(replicate.getError());
	}
    }

    @FXML
    public void importPersons() {
	try {
	    /*
	     * Importer.importPersons(
	     * "./data/2015_07_28_Teilnehmerdaten_Ideenwettbewerb_2015.xlsx",
	     * root, userName);
	     * Importer.importPersons("./data/Beispiel SCM.xlsx", root,
	     * userName);
	     */
	    Importer.importPersons("./data/UniKassel.xlsx", root, userName);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @FXML
    public void searchForPerson() {
	PersonSet existingPersons = DocumentDataCheckManApp.getRoot().getPersons().getMembersTransitive();
	ObservableList<Person> existingPersonList = FXCollections.observableArrayList(existingPersons);

	AutoCompletionDialogPerson dialog = new AutoCompletionDialogPerson(existingPersonList);

	Optional<Person> result = dialog.showAndWait();
	result.ifPresent(t -> {
	    if (t != null) {
		System.out.println("Person: " + NameParser.getName(t));
	    }
	});
    }

    @FXML
    public void searchForTask() {
	TaskSet existingPersons = DocumentDataCheckManApp.getRoot().getSubTasksTransitive();
	ObservableList<Task> existingPersonList = FXCollections.observableArrayList(existingPersons);

	AutoCompletionDialogTask dialog = new AutoCompletionDialogTask(existingPersonList);

	Optional<Task> result = dialog.showAndWait();
	result.ifPresent(t -> {
	    if (t != null) {
		System.out.println("Task: " + NameParser.getName(t));
	    }
	});
    }

    @FXML
    public void clearDB() {
	CouchDBAdapter couchAdapter = DocumentDataCheckManApp.getCouchAdapter();

	/*
	 * First remove the attachment and the ddcm DB
	 */
	couchAdapter.deleteDatabase(DocumentDataCheckManApp.databaseName);
	couchAdapter.deleteDatabase(DocumentDataCheckManApp.attachmentDatabaseName);
	/*
	 * Remove the LayoutInfoDB
	 */
	couchAdapter.deleteDatabase(
		DocumentDataCheckManApp.databaseName + "_layout_" + DocumentDataCheckManApp.toDBName(userName));

	/*
	 * Now Re-Create DB's
	 */
	couchAdapter.createDB(DocumentDataCheckManApp.databaseName);
	couchAdapter.setUserPrivileges(DocumentDataCheckManApp.databaseName, null, Arrays.asList("admin"),
		Arrays.asList(userName), null);
	couchAdapter.createDB(DocumentDataCheckManApp.attachmentDatabaseName);
	couchAdapter.setUserPrivileges(DocumentDataCheckManApp.attachmentDatabaseName, null, Arrays.asList("admin"),
		Arrays.asList(userName), null);
	couchAdapter.createDB(
		DocumentDataCheckManApp.databaseName + "_layout_" + DocumentDataCheckManApp.toDBName(userName));
	couchAdapter.setUserPrivileges(
		DocumentDataCheckManApp.databaseName + "_layout_" + DocumentDataCheckManApp.toDBName(userName), null,
		Arrays.asList("admin"), null, Arrays.asList("user"));

	/*
	 * Now restart...
	 */
	System.err.println("Restart required...");
	DocumentDataCheckManApp.close();
    }
}
