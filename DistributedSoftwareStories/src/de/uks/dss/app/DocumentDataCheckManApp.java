package de.uks.dss.app;

import static de.uks.dss.model.PredefinedDocumentDataConstants.NAME;
import static de.uks.dss.model.PredefinedDocumentDataConstants.STORY_CONTAINER;
import static de.uks.dss.model.PredefinedDocumentDataConstants.dateFormat;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.sdmlib.modelcouch.CouchDBAdapter;
import org.sdmlib.modelcouch.ModelCouch;
import org.sdmlib.modelcouch.ModelCouch.ApplicationType;
import org.sdmlib.modelcouch.connection.authentication.CookieAuthenticator;

import de.uks.dss.engine.TaskFlowEngineHandler;
import de.uks.dss.gui.OwnTreeCell;
import de.uks.dss.gui.OwnTreeItem;
import de.uks.dss.gui.controller.MenuController;
import de.uks.dss.gui.controller.TodoViewController;
import de.uks.dss.importer.Importer;
import de.uks.dss.model.DocumentData;
import de.uks.dss.model.DocumentDataType;
import de.uks.dss.model.Person;
import de.uks.dss.model.PredefinedDocumentDataConstants;
import de.uks.dss.model.Task;
import de.uks.dss.model.util.DocumentDataCreator;
import de.uks.dss.model.util.TaskCreator;
import de.uks.dss.parser.nameLayout.NameParser;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.UpdateListener;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.json.JsonTokener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DocumentDataCheckManApp extends Application {
    public static final String databaseName = "ddcm";
    public static final String attachmentDatabaseName = "ddcm_attachments";
    private static final String DB_PASSWORD = "dbPassword";
    private static final String DB_USER = "dbUsername";
    private static final String DB_HOST = "dbHost";
    private static final int DEFAULT_WINDOW_WIDTH = 1024;
    private static final int DEFAULT_WINDOW_HEIGHT = 768;

    public static void main(String[] args) {
	launch(args);
    }

    private static Stage stage;
    private String userName;
    private String sessionId;
    private static IdMap idMap;
    private static IdMap idMapLayout;
    private static ModelCouch modelCouch;
    private static TreeView treeView;
    private static ModelCouch modelCouchLayout;
    private static Scene checklistScene;
    private HBox toolbar;
    private Pane countertop;
    private static Task root;
    private static DocumentData user;
    private static CouchDBAdapter couchAdapter;
    private static DocumentDataCheckManApp documentDataCheckManApp;
    private static Scene todoScene;
    private static boolean debugmode = false;
    private static UpdateListener propertyChangeUpdateListener;
    private static UpdateListener propertyChangeUpdateListenerLayout;

    public static DocumentDataCheckManApp getDocumentDataCheckManApp() {
	return documentDataCheckManApp;
    }

    @Override
    public void start(Stage stage) throws Exception {
	documentDataCheckManApp = this;
	this.stage = stage;

	// init model couch
	Map<String, String> parameters = this.getParameters().getNamed();
	debugmode = Boolean.parseBoolean(parameters.get("debugmode"));
	userName = parameters.get("username");
	String dbHost = parameters.get(DB_HOST);
	if (dbHost == null || "".equals(dbHost)) {
	    dbHost = "localhost";
	}

	sessionId = userName + System.currentTimeMillis();
	idMap = TaskCreator.createIdMap(sessionId);

	idMapLayout = DocumentDataCreator.createIdMap(sessionId);

	DocumentData userRoot = new DocumentData();
	idMapLayout.put("user", userRoot);
	couchAdapter = new CouchDBAdapter().withHostName(dbHost).withPort(5984).withUserName(userName);
	modelCouchLayout = new ModelCouch(couchAdapter)
		// .withPassword(password)
		.withIdMap(idMapLayout).withApplicationType(ApplicationType.JavaFX).withContinuous(false);

	if (!login(couchAdapter, parameters)) {
	    System.err.println("couldn't login...");
	    Platform.exit();
	    return;
	}

	propertyChangeUpdateListenerLayout = new UpdateListener(idMapLayout, new JsonTokener());
	idMapLayout.withListener(modelCouchLayout);
	idMapLayout.withModelExecutor(propertyChangeUpdateListenerLayout);
	String dbLayoutName = databaseName + "_layout_" + toDBName(userName);
	if (!couchAdapter.testConnection(dbLayoutName)) {
	    if (couchAdapter.createDB(dbLayoutName).getResponseCode() >= 400) {
		System.err.println("Couldn't create LayoutDB!");
		Platform.exit();
		return;
	    }
	    couchAdapter.setUserPrivileges(dbLayoutName, null, Arrays.asList("admin"), Arrays.asList(userName), null);
	}

	if (!couchAdapter.testConnection(attachmentDatabaseName)) {
	    if (couchAdapter.createDB(attachmentDatabaseName).getResponseCode() >= 400) {
		System.err.println("Couldn't create AttachmentDB!");
		Platform.exit();
		return;
	    }
	    couchAdapter.setUserPrivileges(dbLayoutName, null, Arrays.asList("admin"), Arrays.asList(userName), null);
	}

	modelCouchLayout.open(dbLayoutName);

	root = new Task();

	// register taskflow engine handler on root
	new TaskFlowEngineHandler().withApplicationType(TaskFlowEngineHandler.ApplicationType.JavaFX)
		.withUsername(userName).withTaskflowContainer(root).start();

	idMap.put("root", root);
	modelCouch = new ModelCouch(couchAdapter).withIdMap(idMap).withApplicationType(ApplicationType.JavaFX)
		.withContinuous(true);

	// if (!login(modelCouch, parameters)) {
	// System.err.println("couldn't login...");
	// Platform.exit();
	// return;
	// }

	// Here you can register PatternObjects in order to Fire a
	// PropertyChange if the target matches the PatternObject
	propertyChangeUpdateListener = new UpdateListener(idMap, new JsonTokener());
	idMap.withListener(modelCouch);
	idMap.withModelExecutor(propertyChangeUpdateListener);
	if (!couchAdapter.testConnection(databaseName)) {
	    if (couchAdapter.createDB(databaseName).getResponseCode() >= 400) {
		System.err.println("Couldn't create LayoutDB!");
		Platform.exit();
		return;
	    }
	    couchAdapter.setUserPrivileges(databaseName, null, Arrays.asList("admin"), null, Arrays.asList("user"));
	}

	modelCouch.open(databaseName);

	// old changes loaded, fetch the current data model
	DocumentData nameData = root.getTaskData(NAME);

	user = userRoot.getSubData("user");
	if (user == null) {
	    user = userRoot.createSubData().withType("user").withTag("user").withValue(userName)
		    .withLastEditor(userName).withLastModified(dateFormat.format(new Date(System.currentTimeMillis())));
	}

	if (nameData == null) {
	    // init data model
	    if (!initDataModel(nameData)) {
		Platform.exit();
		return;
	    }
	    modelCouch.close();
	    modelCouch.open(databaseName);
	}

	if (root.getPersons().filterName("Members")
		.filter(p -> (p.getPersonData(PredefinedDocumentDataConstants.NAME) != null)
			? userName.equalsIgnoreCase(p.getPersonData(PredefinedDocumentDataConstants.NAME).getValue())
			: null)
		.size() == 0
		&& root.getPersons().getMembersTransitive()
			.filter(p -> userName.equalsIgnoreCase(NameParser.getName(p))).size() == 0) {
	    createUser(root, userName);
	}

	// view
	switchToChecklistScene();

	stage.setTitle(userName + " @ DocumentDataCheckManApp");

	stage.setOnCloseRequest(e -> System.exit(0));

	stage.show();
    }

    private boolean initDataModel(DocumentData nameData) throws IOException {
	Dialog<Integer> dialog = new Dialog<>();
	Button button = new Button("import from local");
	VBox.setVgrow(button, Priority.ALWAYS);
	// button.setPrefWidth(Control.USE_COMPUTED_SIZE);
	button.setMaxWidth(Double.MAX_VALUE);
	button.setOnAction(t -> dialog.setResult(1));
	Button button2 = new Button("replicate from Server");
	// button2.setPrefWidth(Control.USE_COMPUTED_SIZE);
	button2.setMaxWidth(Double.MAX_VALUE);
	VBox.setVgrow(button2, Priority.ALWAYS);
	button2.setOnAction(t -> dialog.setResult(2));
	Button button3 = new Button("create empty");
	// button3.setPrefWidth(Control.USE_COMPUTED_SIZE);
	button3.setMaxWidth(Double.MAX_VALUE);
	VBox.setVgrow(button3, Priority.ALWAYS);
	button3.setOnAction(t -> dialog.setResult(3));
	Button button4 = new Button("Abort");
	// button4.setPrefWidth(Control.USE_COMPUTED_SIZE);
	button4.setMaxWidth(Double.MAX_VALUE);
	VBox.setVgrow(button4, Priority.ALWAYS);
	button4.setOnAction(t -> dialog.setResult(4));
	VBox vBox = new VBox();
	vBox.setMaxWidth(Double.MAX_VALUE);
	vBox.getChildren().addAll(button, button2, button3, button4);
	// TODO - change to dialogPane
	dialog.setGraphic(vBox);
	dialog.showAndWait();
	Integer result = dialog.getResult();
	switch (result) {
	case 1:
	    createModel(nameData);
	    break;
	case 2:
	    importModel(nameData);
	    break;
	case 3:
	    createEmptyModel(nameData, "");
	    break;
	default:
	    return false;
	}
	return true;
    }

    private void importModel(DocumentData nameData) {
	Dialog<String> dialog = new TextInputDialog("http://localhost:5984");
	dialog.setContentText("Please enter the address of the remote couch:");
	dialog.showAndWait();

	modelCouch.getCouchDBAdapter().replicate(modelCouch, dialog.getResult() + "/" + databaseName, "ddcm");
    }

    private void createModel(DocumentData nameData) throws IOException {
	String rootName = "Uni verwalten";
	createEmptyModel(nameData, rootName);

	/*
	 * Importer.importPersons(
	 * "./data/2015_07_28_Teilnehmerdaten_Ideenwettbewerb_2015.xlsx", root,
	 * userName); Importer.importPersons("./data/Beispiel SCM.xlsx", root,
	 * userName);
	 */
	Importer.importPersons("./data/UniKassel.xlsx", root, userName);
    }

    private void createEmptyModel(DocumentData nameData, String rootName) throws IOException {
	root.setName(rootName);
	nameData = root.createTaskData().withTag(NAME).withValue(rootName).withLastEditor(userName)
		.withLastModified(dateFormat.format(new Date(System.currentTimeMillis())))
		.withType(DocumentDataType.STRING.toString());

	root.createTaskData().withTag(Task.class.toGenericString()).withValue("<name>( <description>)")
		.withType(DocumentDataType.LAYOUTINFO.toString());
	root.createTaskData().withTag(Person.class.toGenericString()).withValue("<name>(, <Vorname>( - <Mail-Adresse>))")
		.withType(DocumentDataType.LAYOUTINFO.toString());
    }

    /**
     * Creates the user with the userName as a person inside the root task..
     * TODO - Find better solution.. ;)
     * 
     * @param root
     * @param userName
     */
    private void createUser(Task root, String userName) {
	Person memberGroup = root.getPersons().filterName("Members").first();
	if (memberGroup == null) {
	    memberGroup = root.createPersons().withName("Members").withPersonData(
		    new DocumentData().withTag(PredefinedDocumentDataConstants.NAME).withValue("Members"));
	}
	memberGroup.createMembers().withName(userName).createPersonData().withValue(userName)
		.withTag(PredefinedDocumentDataConstants.NAME).withType(DocumentDataType.STRING.toString());
    }

    private boolean login(CouchDBAdapter couchDBAdapter, Map<String, String> parameters) {
	if (parameters.get(DB_USER) != null) {
	    String user = parameters.get(DB_USER);
	    String password = parameters.get(DB_PASSWORD);
	    if (password == null) {
		password = "";
	    }
	    try {
		couchDBAdapter.withAuthenticator(new CookieAuthenticator()).withUserName(user).login(password);
	    } catch (Exception e) {
		// couldn't log in..
		System.err.println("Couldn't log in into DB");
		e.printStackTrace();
		return false;
	    }
	}
	return true;
    }

    public static void switchScene(Scene newScene) {
	Platform.runLater(() -> {
	    stage.setScene(newScene);
	});
    }

    public static void switchToMenuScene() {
	Task storyRoot = root.getSubTasks().filter(new Condition<Task>() {
	    @Override
	    public boolean update(Task value) {
		if (value.getTaskData().getTag().contains(STORY_CONTAINER)) {
		    return true;
		} else {
		    return false;
		}
	    }
	}).first();

	if (storyRoot == null) {
	    // not yet initialized
	    storyRoot = root.createSubTasks();
	    storyRoot.createTaskData().withTag(STORY_CONTAINER);
	}

	HBox toolbar = new HBox();
	VBox menu = new VBox();
	new MenuController().withToolbar(toolbar).withMenu(menu).withStoryRoot(storyRoot).start();
	VBox menuBox = new VBox(toolbar, menu);
	ScrollPane uiRoot = new ScrollPane(menuBox);
	Scene menuScene = new Scene(uiRoot, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
	stage.setScene(menuScene);
    }

    public static void switchToTodoScene() {

	Scene scene = null;
	if (todoScene == null) {
	    // get the Person, that corresponds to the User logged in
	    Person personRoot = root.getPersons().filterName("Members").first().getMembers().filterName(user.getValue())
		    .first();

	    if (personRoot == null) {
		personRoot = root.getPersons().filterName("Members").first().createMembers().withPersonData(user);
	    }

	    FXMLLoader loader = new FXMLLoader(DocumentDataCheckManApp.class.getResource("TodoView.fxml"));
	    try {
		ScrollPane scrollPane = new ScrollPane(loader.load());
		scrollPane.setMaxHeight(Double.MAX_VALUE);
		scrollPane.setMaxWidth(Double.MAX_VALUE);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);

		scene = new Scene(scrollPane);
		todoScene = scene;
		TodoViewController controller = loader.getController();

		controller.init(personRoot);
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} else {
	    scene = todoScene;
	}
	stage.setScene(scene);
    }

    public static void switchToChecklistScene() {
	if (checklistScene == null) {
	    Button backButton = new Button("<<");
	    backButton.setOnAction(e -> switchToMenuScene());

	    // Button for todo-view
	    Button todoButton = new Button("TODO");
	    todoButton.setOnAction(e -> switchToTodoScene());

	    HBox toolBar = new HBox(backButton, todoButton);

	    if (debugmode == true) {
		Button debugButton = new Button("Open Debug Menu");
		debugButton.setOnAction(e -> {
		    FXMLLoader fxmlLoader = new FXMLLoader(DocumentDataCheckManApp.class.getResource("DebugView.fxml"));
		    try {
			Parent load = fxmlLoader.load();
			DebugController debugController = fxmlLoader.getController();
			Stage debugStage = new Stage();
			debugStage.setScene(new Scene(load));
			debugController.initialize(debugButton, debugStage);
			debugStage.show();
		    } catch (Exception e1) {
			e1.printStackTrace();
		    }
		});
		toolBar.getChildren().add(debugButton);
	    }

	    // EntryPane rootVBox = new EntryPane<>();
	    treeView = new TreeView<>();
	    treeView.setCellFactory(c -> new OwnTreeCell<>());
	    treeView.setRoot(new OwnTreeItem("Checklist", root, null));

	    treeView.setEditable(true);

	    // rootVBox.setMaxWidth(Double.MAX_VALUE);
	    // treeView.setMaxWidth(Double.MAX_VALUE);
	    // treeView.setPrefWidth(Double.MAX_VALUE);

	    ScrollPane rootScrollPane = new ScrollPane(treeView);
	    rootScrollPane.setMaxWidth(Double.MAX_VALUE);
	    rootScrollPane.setMaxHeight(Double.MAX_VALUE);

	    rootScrollPane.setFitToWidth(true);
	    rootScrollPane.setFitToHeight(true);

	    // rootController = new TaskController(idMap, rootVBox, root, user,
	    // userName)
	    // .start();

	    VBox rootPane = new VBox(toolBar, rootScrollPane);
	    rootPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	    rootPane.setFillWidth(true);
	    VBox.setVgrow(rootScrollPane, Priority.ALWAYS);

	    // show root editor
	    checklistScene = new Scene(rootPane, 600, 700);

	    stage.setScene(checklistScene);
	}
	DocumentDataCheckManApp.switchScene(checklistScene);
    }

    /**
     * Remove not allowed chars for db-name (There could be duplicates in db
     * names...)
     * 
     * @param s
     * @return
     */
    public static String toDBName(String s) {
	s = s.toLowerCase();
	s = s.replaceAll("(?!([a-z]|[0-9]|\\+|\\-|\\$|'_'|'('|')'|'\\/')*)", "");
	return s;
    }

    public static IdMap getIdMap() {
	return idMap;
    }

    public static IdMap getIdMapLayout() {
	return idMapLayout;
    }

    public static Scene getChecklistScene() {
	return checklistScene;
    }

    public static ModelCouch getModelCouch() {
	return modelCouch;
    }

    public static ModelCouch getModelCouchLayout() {
	return modelCouchLayout;
    }

    public static TreeView getTreeView() {
	return treeView;
    }

    /**
     * Returns the root object for the layout
     * 
     * @return
     */
    public static DocumentData getUser() {
	return user;
    }

    public static String getUsername() {
	return user.getValue();
    }

    /**
     * Returns the root Task for the layout
     * 
     * @return
     */
    public static Task getRoot() {
	return root;
    }

    public static CouchDBAdapter getCouchAdapter() {
	return couchAdapter;
    }

    public static UpdateListener getPropertyChangeUpdateListener() {
		return propertyChangeUpdateListener;
	}

	public static void close() {
	getModelCouch().close();
	getModelCouchLayout().close();
	Platform.exit();
    }
}
