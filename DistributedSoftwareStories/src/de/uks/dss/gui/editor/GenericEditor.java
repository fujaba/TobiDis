package de.uks.dss.gui.editor;

import static de.uks.dss.model.PredefinedDocumentDataConstants.dateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Pattern;

import org.sdmlib.modelcouch.CouchDBAdapter;
import org.sdmlib.modelcouch.connection.ContentType;
import org.sdmlib.modelcouch.connection.ReturnObject;
import org.sdmlib.models.modelsets.SDMSet;

import de.uks.dss.app.DocumentDataCheckManApp;
import de.uks.dss.gui.editor.view.ViewEditor;
import de.uks.dss.model.DocumentData;
import de.uks.dss.model.DocumentDataType;
import de.uks.dss.model.Task;
import de.uks.dss.model.util.DocumentDataSet;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class GenericEditor {
    public static final String INDEX = "index";
    public static final String PROPERTY = "property";
    public static final String PROPERTY_NAME = "propertyName";
    public static final String REQUIRED = "required";
    public static final String TYPE = "type";
    public static final String FIELDS = "fields";
    public static final String VIEWS = "views";
    public static final String SEPERATOR = "||";
    private JsonObject viewLayout;
    private String key;

    public void editViews(Boolean graphical) {
	// ViewEditor viewEditor = new ViewEditor(getViewLayout().toString(3));
	ViewEditor viewEditor;
	if (key != null && viewLayout != null) {
	    viewEditor = new ViewEditor(viewLayout.toString(3), key, getViewLayout().toString(3), graphical);
	} else {
	    viewEditor = new ViewEditor(getViewLayout().toString(3));
	}
	Optional<String> result = viewEditor.showAndWait();
	result.filter(t -> t != null).ifPresent(t -> {
	    // at first get locale layout view
	    DocumentData viewLayoutLocale = getViewLayoutLocale();
	    if (viewLayoutLocale == null) {
		// create locale view data if not existing
		viewLayoutLocale = DocumentDataCheckManApp.getUser().createSubData().withTag(VIEWS)
			.withType(DocumentDataType.LAYOUTINFO.toString());
	    }
	    viewLayoutLocale.setValue(t);
	});
    }

    Callback<Boolean, Void> viewUpdate;

    public void setOnUpdate(Callback<Boolean, Void> callback) {
	viewUpdate = callback;
    }

    public void clear() {
	key = null;
	viewLayout = null;
	saveActions.clear();
    }

    LinkedList<Callback<Boolean, Void>> saveActions = new LinkedList<>();

    public void save(boolean b) {
	this.saveActions.forEach(t -> t.call(b));
	if (viewUpdate != null)
	    viewUpdate.call(b);
    }

    public void createView(Pane editPane, SendableEntity value, JsonObject viewLayout) {
	saveActions.clear();
	editPane.getChildren().clear();
	this.key = value.getClass().getSimpleName();
	JsonObject view = (JsonObject) viewLayout.get(key);
	this.viewLayout = view;
	if (view == null) {
	    // no view saved...
	    // take default view...
	    editPane.getChildren().clear();
	    return;
	}

	JsonArray fields = (JsonArray) view.get(FIELDS);

	fields.forEach(t -> {
	    Object res = value;
	    JsonObject field = (JsonObject) t;

	    String type = field.getString(TYPE, "String");
	    String propertyName = field.getString(PROPERTY_NAME, "undefined");
	    String property = field.getString(PROPERTY, "undefined");
	    boolean required = false;
	    if (field.containsKey(REQUIRED)){
		required = field.getBoolean(REQUIRED);
	    }
	    String[] split = property.split(Pattern.quote(SEPERATOR));

	    res = getValue(property, value, required);

	    if (res == null) {
		if (!required) {
		    return;
		}
		// if the field is required, we want to create the Form elements
		// anyway
	    }

	    // create fxComponent
	    LinkedList<Node> nodes = new LinkedList<>();

	    Object finalRes = res;
	    DocumentDataType docDataType = DocumentDataType.valueOf(type);
	    Node node = FXEditorUtil.docDataTypeToNode(docDataType);

	    final Callback<Boolean, Void> saveThisFieldCallback;
	    if (docDataType != DocumentDataType.ICON && docDataType != DocumentDataType.FORM) {
		saveThisFieldCallback = new Callback<Boolean, Void>() {
		    @Override
		    public Void call(Boolean param) {
			if (param) {
			    saveValue(property, value, FXEditorUtil.getValue(node).toString());
			} else {
			    FXEditorUtil.setValue(node, finalRes);
			}
			return null;
		    }
		};
		saveActions.add(saveThisFieldCallback);
	    } else {
		saveThisFieldCallback = null;
	    }
	    FXEditorUtil.setValue(node, res);
	    switch (docDataType) {
	    case DATE:
		nodes.add(node);
		((ComboBoxBase<LocalDate>) node).setOnAction(a -> {
		    if (saveThisFieldCallback != null) {
			saveThisFieldCallback.call(true);
		    }
		    // save(true)
		});
		break;
	    case BOOLEAN:
		nodes.add(node);
		((ButtonBase) node).setOnAction(new EventHandler<ActionEvent>() {

		    @Override
		    public void handle(ActionEvent event) {
			if (saveThisFieldCallback != null) {
			    saveThisFieldCallback.call(true);
			}
			// save(true);
		    }
		});
		break;
	    case FORM:
		FXEditorUtil.setValue(node, "Open Formular");

		// When you click, you want the Link to open the Formular
		((ButtonBase) node).setOnAction(x -> openLink((String) finalRes));

		FileChooser fileChooser = new FileChooser();
		Button formularButton = new Button("Change Formular");
		formularButton.setOnAction(f -> {
		    File file = fileChooser.showOpenDialog(DocumentDataCheckManApp.getChecklistScene().getWindow());
		    if (file != null && file.exists()) {
			// upload attachment and save Link in DocData
			saveValue(property, value,
				uploadAttachment(file.getAbsolutePath(), ContentType.APPLICATION_PDF));
			if (viewUpdate != null)
			    viewUpdate.call(true);
		    }
		});

		nodes.add(formularButton);
		nodes.add(node);
		break;
	    case ICON:
		// if (finalRes.toString() != null &&
		// !"value?".equals(finalRes.toString())) {
		// FXEditorUtil.setValue(node, new Image(finalRes.toString(),
		// 50, 50, true, true));
		// }

		FileChooser fileChooserIcon = new FileChooser();
		Button iconButton = new Button("Change Icon");
		iconButton.setOnAction(f -> {
		    File file = fileChooserIcon.showOpenDialog(DocumentDataCheckManApp.getChecklistScene().getWindow());
		    if (file != null && file.exists()) {
			// upload attachment and save Link in DocData
			saveValue(property, value,
				uploadAttachment(file.getAbsolutePath(), ContentType.APPLICATION_PDF));
			if (viewUpdate != null)
			    viewUpdate.call(true);
		    }
		});

		nodes.add(iconButton);
		nodes.add(node);
		break;
	    case DOCUMENT_DATA_TYPE:
		// here we show a choice box with all the DocDataTypes to
		// choose..
		// FXEditorUtil.setValue(node,
		// DocumentDataType.valueOf(res.toString()));
		nodes.add(node);

		((ChoiceBox<DocumentDataType>) node).setOnAction(f -> {
		    saveValue(property, value, FXEditorUtil.getValue(node).toString());
		});

		break;
	    case STRING:
	    default:
		// String text = res.toString();
		// FXEditorUtil.setValue(node, text);
		nodes.add(node);
		node.setOnKeyTyped(new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent event) {
			if (KeyCode.ENTER.equals(event.getCode()) || "\r".equals(event.getCharacter())) {
			    // save
			    if (saveThisFieldCallback != null) {
				saveThisFieldCallback.call(true);
			    }
			    // save(true);
			} else if (KeyCode.ESCAPE.equals(event.getCode())) {
			    // cancel
			    if (saveThisFieldCallback != null) {
				saveThisFieldCallback.call(false);
			    }
			    // save(false);
			}
		    }
		});
		break;
	    }

	    if (!nodes.isEmpty()) {
		editPane.getChildren().add(new Label(propertyName + ":"));
		ObservableList<Node> children = editPane.getChildren();
		nodes.forEach(n -> children.add(n));
	    }
	});
    }

    /**
     * Because of the restrictions to the couch, the user has to be logged in
     * before opening a form in the browser...
     * 
     * This is why we save the Attachment in the tmp directory, and open the
     * local file..
     * 
     * TODO search for a better place for this..
     */
    protected void openLink(String finalRes) {
	// first remove "http"
	CouchDBAdapter couchAdapter = DocumentDataCheckManApp.getCouchAdapter();

	byte[] attachment = couchAdapter.getAttachment((String) finalRes);

	FileOutputStream fos;
	String location = (String) finalRes;
	String[] tmpSplit = location.split("/");
	String filename = tmpSplit[tmpSplit.length - 1];

	try {
	    Path path = Paths.get("./tmp");
	    if (Files.notExists(path)) {
		Files.createDirectories(path);
	    }
	    Path file = Paths.get("./tmp/" + filename);
	    if (Files.notExists(file)) {
		Files.createFile(file);
	    }
	    Path write = Files.write(file, attachment);

	    DocumentDataCheckManApp.getDocumentDataCheckManApp().getHostServices()
		    .showDocument(write.toAbsolutePath().toString());
	} catch (Exception e) {
	    // e.printStackTrace();
	}
    }

    /**
     * Uploads the file at the destination <b>link<b> and returns the link to
     * the Attachment in the CouchDB.
     * 
     * TODO search for a better place for this..
     * 
     * @param link
     * @return Link to the Attachment in the Couch
     */
    public String uploadAttachment(String link, ContentType contentType) {
	// upload attachment
	CouchDBAdapter couchAdapter = DocumentDataCheckManApp.getCouchAdapter();

	ReturnObject send = couchAdapter.createEmptyDocument(DocumentDataCheckManApp.attachmentDatabaseName);

	ReturnObject addAttachment = couchAdapter.addAttachment(send, Paths.get(link), contentType);

	return addAttachment.getHeaderFields().get("Location").get(0);
    }

    private Object getValue(String property, SendableEntity entity) {
	return getValue(property, entity, false);
    }

    private Object getValue(String property, SendableEntity entity, boolean required) {
	return getValue(property, entity, new LinkedList<>(), required);
    }

    private Object getValue(String property, SendableEntity entity, LinkedList<Object> entityPath, boolean required) {
	String[] split = property.split(Pattern.quote(SEPERATOR));
	Object res = entity;
	int partNum = -1;
	for (String part : split) {
	    partNum++;
	    Object oldRes = res;
	    if (res == null) {
		if (!required) {
		}
		return null;
		// if required, wa want to create the Object

	    }

	    if (res instanceof DocumentDataSet) {
		// the part is the tag of the DocumentData we need...
		res = ((DocumentDataSet) res).filterTag(part).first();
		if (res == null && required) {
		    // theoretically we must go one level up and the call a
		    // add{PropertyName} on the element because of the property
		    // changes...
		    int size = entityPath.size();
		    if (size == 0 || partNum == 0) {
			// we don't have a entity where we can call the add
			// method on...
			return null;
		    }
		    Object parentEntity = entityPath.get(size - 1);
		    // get IdMap
		    SendableEntityCreator creatorClass = DocumentDataCheckManApp.getIdMap()
			    .getCreatorClass(parentEntity);
		    if (creatorClass == null)
			return null;
		    //// get value of property with name=part
		    // res = creatorClass.getValue(res, part);
		    try {
			Object instance = createInstance(parentEntity, split[partNum - 1]);
			res = instance;

			// now that we have the instance, we must set the tag of
			// the documentData...
			SendableEntityCreator subEntityCreatorClass = DocumentDataCheckManApp.getIdMap()
				.getCreatorClass(res);
			if (subEntityCreatorClass == null) {
			    return null;
			}

			subEntityCreatorClass.setValue(res, DocumentData.PROPERTY_TAG, part, SendableEntityCreator.UPDATE);
			subEntityCreatorClass.setValue(res, DocumentData.PROPERTY_VALUE, "", SendableEntityCreator.UPDATE);

			// add instance to parentEntity
			creatorClass.setValue(parentEntity, split[partNum - 1], instance, SendableEntityCreator.UPDATE);
		    } catch (NoSuchFieldException | SecurityException | InstantiationException
			    | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		    }
		}
	    } else {
		entityPath.add(res);

		// get IdMap
		SendableEntityCreator creatorClass = DocumentDataCheckManApp.getIdMap().getCreatorClass(res);
		if (creatorClass == null)
		    return null;
		// get value of property with name=part
		res = creatorClass.getValue(res, part);
		if (res == null && required) {
		    try {
			Object instance = createInstance(oldRes, part);
			res = instance;
		    } catch (NoSuchFieldException | SecurityException | InstantiationException
			    | IllegalAccessException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.err.println(e.getMessage());
			return null;
		    }
		}
	    }
	}
	return res;
    }

    /**
     * Creates an instance of the property
     * 
     * @param oldRes
     * @param part
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @return A instance of the property
     */
    private Object createInstance(Object entity, String property)
	    throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException {
	// get IdMap
	SendableEntityCreator entityCreator = DocumentDataCheckManApp.getIdMap().getCreatorClass(entity);
	Object subEntity = entityCreator.getValue(entity, property);
	if (subEntity == null) {
	    // the property was wrong, so there is no value...
	    throw new NoSuchFieldException(
		    "Couldn't get value, maybe wrong property? (Property was \"" + property + "\")");
	}
	Class<? extends Object> subEntityClass = subEntity.getClass();

	if (SDMSet.class.isAssignableFrom(subEntityClass)) {
	    // Field is a To Many Relation..
	    Object setInstance = subEntityClass.newInstance();
	    try {
		// try to get the EntryType
		Method declaredMethod = subEntityClass.getDeclaredMethod("getEntryType");
		String entryType = (String) declaredMethod.invoke(setInstance);

		// now that we have the full class name, we can create a new
		// instance of the class
		Class<?> entryClass = this.getClass().getClassLoader().loadClass(entryType);

		// and we can create an instance of it
		Object newInstance = entryClass.newInstance();
		return newInstance;
	    } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException
		    | ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	// Field is To One Relation

	Object newInstance = subEntityClass.newInstance();
	return newInstance;

    }

    private boolean saveValue(String property, SendableEntity entity, Object value) {
	String[] split = property.split(Pattern.quote(SEPERATOR));
	Object res = entity;
	int size = split.length;
	for (String part : split) {
	    if (res == null) {
		return false;
	    }
	    if (--size <= 0) {
		// we don't want to walk the last property, but instead save the
		// value to the Object
		SendableEntityCreator creatorClass = DocumentDataCheckManApp.getIdMap().getCreatorClass(res);
		if (creatorClass == null)
		    return false;
		if (res instanceof DocumentData) {
		    // in case it's DocumentData, we want to update the last
		    // editor and the time of the last edit
		    ((DocumentData) res).withLastEditor(DocumentDataCheckManApp.getUsername())
			    .withLastModified(dateFormat.format(new Date()));
		}
		boolean setValue = creatorClass.setValue(res, part, value, SendableEntityCreator.UPDATE);
		return setValue;
	    }

	    if (res instanceof DocumentDataSet) {
		// the part is the tag of the DocumentData we need...
		res = ((DocumentDataSet) res).filterTag(part).first();
	    } else {
		// get IdMap
		SendableEntityCreator creatorClass = DocumentDataCheckManApp.getIdMap().getCreatorClass(res);
		// get value of property with name=part
		res = creatorClass.getValue(res, part);
	    }
	}
	return false;
    }

    public DocumentData getViewData() {
	// at first, look, if there is a locale layout
	DocumentData res = getViewLayoutLocale();
	if (res != null) {
	    return res;
	}

	Task root = DocumentDataCheckManApp.getRoot();
	DocumentData taskData = root.getTaskData(VIEWS);
	DocumentDataSet filterTag = root.getTaskData().filterTag(VIEWS)
		.filterType(DocumentDataType.LAYOUTINFO.toString());
	if (filterTag.size() == 1) {
	    res = filterTag.first();
	    return res;
	} else {
	    if (filterTag.size() == 0) {
		System.err.println("No View Data. Creating Default..");
		res = root.createTaskData().withTag(VIEWS).withType(DocumentDataType.LAYOUTINFO.toString())
			.withValue(createDefaultViews().toString());
		return res;
	    } else {
		if (filterTag.size() > 1) {
		    // there are more than one GenericLayoutViews..
		    res = filterTag.first();
		    return res;
		}
	    }
	}
	return null;
    }

    /**
     * Here we are looking if the view layout is at the LayoutDB. If this is the
     * Case, the user has overwritten the standard Layout. And in this Case, we
     * want to return the locale layout view
     * 
     * @return the layout view from the user (JsonObject)
     */
    private DocumentData getViewLayoutLocale() {
	// at first get the layout root
	DocumentData user = DocumentDataCheckManApp.getUser();
	// now check if there is a Layout View
	DocumentData viewData = user.getSubData(VIEWS);
	return viewData;
    }

    public JsonObject getViewLayout() {
	// At first, get the Layoutinfo from the Root Task
	Task root = DocumentDataCheckManApp.getRoot();
	DocumentData taskData = root.getTaskData(VIEWS);
	// TODO: REMOVE!! ONLY FOR DEBUGGING!!!
	// if (taskData != null) {
	// taskData.removeYou();
	// }
	DocumentData viewData = getViewData();
	// if there is no taskData with tag=views and type=layoutinfo
	if (viewData != null) {
	    // just create a new DocData..
	    JsonObject jsonObject = createDefaultViews();

	    taskData = getViewData();
	}

	String viewString = EntityUtil.unQuote(taskData.getValue());

	JsonObject viewLayout = new JsonObject().withValue(EntityUtil.unQuote(viewString));
	return viewLayout;
    }

    private JsonObject createDefaultViews() {
	// Here is an example, how a viewInfo can look like
	JsonObject jsonObject = new JsonObject();

	/**
	 * Person
	 */
	JsonObject person = new JsonObject();
	JsonArray personFields = new JsonArray();

	JsonObject name = new JsonObject();
	name.add(TYPE, DocumentDataType.STRING);
	name.add(PROPERTY_NAME, "Name");
	// "personData||name||value"
	name.add(PROPERTY, "personData" + SEPERATOR + "name" + SEPERATOR + "value");
	personFields.add(name);

	JsonObject mail = new JsonObject();
	mail.add(TYPE, DocumentDataType.STRING);
	mail.add(PROPERTY_NAME, "E-Mail");
	// "personData||name||value"
	mail.add(PROPERTY, "personData" + SEPERATOR + "mail" + SEPERATOR + "value");
	personFields.add(mail);

	JsonObject date = new JsonObject();
	date.add(TYPE, DocumentDataType.DATE);
	date.add(PROPERTY_NAME, "date");
	date.add(PROPERTY, "personData" + SEPERATOR + "date" + SEPERATOR + "value");
	personFields.add(date);

	JsonObject checkBox = new JsonObject();
	checkBox.add(TYPE, DocumentDataType.BOOLEAN);
	checkBox.add(PROPERTY_NAME, "Check");
	checkBox.add(PROPERTY, "personData" + SEPERATOR + "check" + SEPERATOR + "value");
	personFields.add(checkBox);

	person.add(FIELDS, personFields);
	jsonObject.add("Person", person);

	/**
	 * Task
	 */
	JsonObject task = new JsonObject();
	JsonArray taskFields = new JsonArray();

	JsonObject taskName = new JsonObject();
	taskName.add(TYPE, DocumentDataType.STRING);
	taskName.add(PROPERTY_NAME, "Name");
	taskName.add(PROPERTY, "taskData" + SEPERATOR + "name" + SEPERATOR + "value");
	taskFields.add(taskName);

	JsonObject form = new JsonObject();
	form.add(TYPE, DocumentDataType.FORM);
	form.add(PROPERTY_NAME, "Formular");
	form.add(PROPERTY, "taskData" + SEPERATOR + "Formular" + SEPERATOR + "value");
	taskFields.add(form);

	task.add(FIELDS, taskFields);
	jsonObject.add("Task", task);

	/**
	 * DocData
	 */
	JsonObject docData = new JsonObject();
	JsonArray docDataFields = new JsonArray();

	JsonObject type = new JsonObject();
	type.add(TYPE, DocumentDataType.DOCUMENT_DATA_TYPE);
	type.add(PROPERTY_NAME, "Type");
	type.add(PROPERTY, TYPE);
	docDataFields.add(type);

	JsonObject tag = new JsonObject();
	tag.add(TYPE, DocumentDataType.STRING);
	tag.add(PROPERTY_NAME, "Tag");
	tag.add(PROPERTY, "tag");
	docDataFields.add(tag);

	JsonObject value = new JsonObject();
	value.add(TYPE, DocumentDataType.STRING);
	value.add(PROPERTY_NAME, "Value");
	value.add(PROPERTY, "value");
	docDataFields.add(value);

	docData.add(FIELDS, docDataFields);
	jsonObject.add("DocumentData", docData);

	return jsonObject;
    }
}
