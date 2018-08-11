package de.uks.dss.gui.editor.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import com.sun.glass.ui.Screen;

import de.uks.dss.gui.editor.FXEditorUtil;
import de.uks.dss.gui.editor.GenericEditor;
import de.uks.dss.model.DocumentDataType;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;

public class ViewEditor extends Dialog<String> {
    private TextArea textArea;

    public ViewEditor(String viewJson) {
	setTitle("Edit View");
	// TODO Explanation..
	setHeaderText("EXCPLANATION FOLLOWS...");

	// Set the button types.
	ButtonType loginButtonType = new ButtonType("Save", ButtonData.OK_DONE);
	getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

	nonGraphical(viewJson);

	setResultConverter(new Callback<ButtonType, String>() {
	    @Override
	    public String call(ButtonType buttonType) {
		if (ButtonBar.ButtonData.OK_DONE.equals(buttonType.getButtonData())) {
		    return textArea.getText().replaceAll("\\s", "");
		}
		return null;
	    }
	});
    }

    /**
     * For only showing the part, that should be edited
     * 
     * @param viewJson
     * @param completeViewJson
     */
    public ViewEditor(String viewJson, String key, String completeViewJson) {
	setTitle("Edit View");
	// TODO Explanation..
	setHeaderText("EXCPLANATION FOLLOWS...");

	// Set the button types.
	ButtonType loginButtonType = new ButtonType("Save", ButtonData.OK_DONE);
	getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

	nonGraphical(viewJson);

	setResultConverter(new Callback<ButtonType, String>() {
	    @Override
	    public String call(ButtonType buttonType) {
		if (ButtonBar.ButtonData.OK_DONE.equals(buttonType.getButtonData())) {
		    String jsonString = textArea.getText().replaceAll("\\s", "");
		    JsonObject jsonPart = new JsonObject();
		    jsonPart.withValue(jsonString);

		    JsonObject jsonObject = new JsonObject();
		    jsonObject.withValue(completeViewJson);
		    jsonObject.replace(key, jsonPart);

		    return jsonObject.toString();
		}
		return null;
	    }
	});
    }

    /**
     * For only showing the part, that should be edited
     * 
     * @param viewJson
     * @param completeViewJson
     */
    public ViewEditor(String viewJson, String key, String completeViewJson, Boolean graphical) {
	setTitle("Edit View");
	// TODO Explanation..
	setHeaderText("EXCPLANATION FOLLOWS...");

	// Set the button types.
	ButtonType loginButtonType = new ButtonType("Save", ButtonData.OK_DONE);
	getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

	if (!graphical) {
	    nonGraphical(viewJson);
	    setResultConverter(new Callback<ButtonType, String>() {
		@Override
		public String call(ButtonType buttonType) {
		    if (ButtonBar.ButtonData.OK_DONE.equals(buttonType.getButtonData())) {
			String jsonString = textArea.getText().replaceAll("\\s", "");
			JsonObject jsonPart = new JsonObject();
			jsonPart.withValue(jsonString);

			JsonObject jsonObject = new JsonObject();
			jsonObject.withValue(completeViewJson);
			jsonObject.replace(key, jsonPart);

			return jsonObject.toString();
		    }
		    return null;
		}
	    });
	} else {
	    Callback<Boolean, JsonArray> fields = graphical(viewJson);
	    setResultConverter(new Callback<ButtonType, String>() {
		@Override
		public String call(ButtonType buttonType) {
		    if (ButtonBar.ButtonData.OK_DONE.equals(buttonType.getButtonData())) {

			JsonObject jsonPart = new JsonObject();
			jsonPart.add(GenericEditor.FIELDS, fields.call(true));

			JsonObject jsonObject = new JsonObject();
			jsonObject.withValue(completeViewJson);
			jsonObject.replace(key, jsonPart);

			return jsonObject.toString();
		    }
		    return null;
		}
	    });
	}

    }

    private Callback<Boolean, JsonArray> graphical(String viewJson) {
	VBox rootVBox = new VBox(15);
	ScrollPane pane = new ScrollPane(rootVBox);
	pane.setFitToWidth(true);
	pane.setMinWidth(500);

	pane.setMaxSize(Screen.getMainScreen().getVisibleWidth() - 250,
		Screen.getMainScreen().getVisibleHeight() - 250);
	pane.setMaxHeight(Screen.getMainScreen().getVisibleHeight() - 250);
	pane.setPrefHeight(Screen.getMainScreen().getVisibleHeight() - 250);

	JsonObject jsonView = new JsonObject().withValue(viewJson);

	JsonArray fields = jsonView.getJsonArray(GenericEditor.FIELDS);

	LinkedList<Callback<Boolean, JsonObject>> saveActions = new LinkedList<>();

	VBox propertyVBox = new VBox(15);
	rootVBox.getChildren().add(propertyVBox);
	fields.forEach(f -> {
	    JsonObject fieldJson = new JsonObject().withValue(f.toString());

	    saveActions.add(addField(fieldJson.getString(GenericEditor.PROPERTY_NAME),
		    DocumentDataType.valueOf(fieldJson.getString(GenericEditor.TYPE)),
		    fieldJson.getString(GenericEditor.PROPERTY), propertyVBox, fieldJson));
	});

	Button newButton = new Button("Add new Field");
	newButton.setOnAction(t -> {
	    saveActions.add(addField(GenericEditor.PROPERTY_NAME, DocumentDataType.STRING, GenericEditor.PROPERTY,
		    propertyVBox, new JsonObject()));
	});
	rootVBox.getChildren().add(newButton);

	getDialogPane().setContent(pane);

	return new Callback<Boolean, JsonArray>() {
	    @Override
	    public JsonArray call(Boolean save) {
		if (save) {
		    JsonArray resultArray = new JsonArray();
		    saveActions.forEach(t -> {
			resultArray.add(t.call(true));
		    });

		    // now sort
		    resultArray.sort(new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
			    if (o1 instanceof JsonObject && o2 instanceof JsonObject) {
				Integer int1 = ((SimpleKeyValueList<String, Object>) o1).getInt(GenericEditor.INDEX);
				Integer int2 = ((SimpleKeyValueList<String, Object>) o2).getInt(GenericEditor.INDEX);
				return int1.compareTo(int2);
			    } else
				return 0;
			}
		    });

		    // after sort, we can delete the index key-value pairs
		    resultArray.forEach(t -> {
			if (t instanceof JsonObject) {
			    ((JsonObject) t).remove(GenericEditor.INDEX);
			}
		    });

		    return resultArray;
		}
		return null;
	    }
	};
    }

    protected Callback<Boolean, JsonObject> addField(String propertyName, DocumentDataType docDataType, String property,
	    Pane parentNode, JsonObject fieldJson) {
	ObservableList<Node> children = parentNode.getChildren();
	VBox vBox2 = new VBox(5);
	// vBox2.setPadding(new Insets(10));
	vBox2.setStyle("-fx-border-style: solid inside;" + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
		+ "-fx-border-radius: 0;" + "-fx-border-color: black;" + "-fx-padding: 10;");

	TextField propertyNameTextField = new TextField(propertyName);
	vBox2.getChildren().add(propertyNameTextField);

	// vBox2.getChildren().add(
	// FXEditorUtil.docDataTypeToNode(DocumentDataType.valueOf(fieldJson.getString(GenericEditor.TYPE))));
	ChoiceBox<DocumentDataType> docDataTypeToNode = (ChoiceBox<DocumentDataType>) FXEditorUtil
		.docDataTypeToNode(DocumentDataType.DOCUMENT_DATA_TYPE);
	docDataTypeToNode.setValue(docDataType);
	vBox2.getChildren().add(docDataTypeToNode);

	TextField propertyTextField = new TextField(property);
	vBox2.getChildren().add(propertyTextField);

	CheckBox requiredBox = new CheckBox("Required");
	if (fieldJson.containsKey(GenericEditor.REQUIRED)) {
	    requiredBox.setSelected(fieldJson.getBoolean(GenericEditor.REQUIRED));
	}
	vBox2.getChildren().add(requiredBox);

	// save action generates json from the values
	Callback<Boolean, JsonObject> callback = new Callback<Boolean, JsonObject>() {
	    public boolean deleted = false;

	    @Override
	    public JsonObject call(Boolean save) {
		if (save && !deleted) {
		    JsonObject resObject = new JsonObject();

		    // We add the index temporarily, so that we can sort the
		    // Json Objects inside the JsonArray
		    resObject.add(GenericEditor.INDEX, getIndex(parentNode, vBox2));
		    resObject.add(GenericEditor.TYPE, docDataTypeToNode.getValue());
		    resObject.add(GenericEditor.PROPERTY_NAME, propertyNameTextField.getText());
		    resObject.add(GenericEditor.PROPERTY, propertyTextField.getText());
		    resObject.add(GenericEditor.REQUIRED, requiredBox.isSelected());

		    return resObject;
		} else if (!save && !deleted) {
		    deleted = true;
		}
		return null;
	    }
	};

	// Add options like delete and order change
	HBox optionsBox = new HBox();
	Button deleteButton = new Button("Delete");
	deleteButton.setOnAction(t -> {
	    callback.call(false);
	    children.remove(vBox2);
	});
	optionsBox.getChildren().add(deleteButton);
	Button upButton = new Button("UP");
	upButton.setOnAction(t -> {
	    up(parentNode, vBox2);
	});
	optionsBox.getChildren().add(upButton);

	Button downButton = new Button("DOWN");
	downButton.setOnAction(t -> {
	    down(parentNode, vBox2);
	});
	optionsBox.getChildren().add(downButton);

	vBox2.getChildren().add(optionsBox);
	children.add(vBox2);

	return callback;
    }

    private void up(Pane parentNode, VBox objectToMove) {
	move(parentNode, objectToMove, 1);
    }

    private void down(Pane parentNode, VBox objectToMove) {
	move(parentNode, objectToMove, -1);
    }

    private int getIndex(Pane parentNode, Node node) {
	return parentNode.getChildrenUnmodifiable().indexOf(node);
    }

    private void move(Pane parentNode, VBox objectToMove, int i) {
	int index = getIndex(parentNode, objectToMove);
	ObservableList<Node> children = FXCollections.observableArrayList(parentNode.getChildren());
	if ((index - i) < 0 || (index - i) > children.size()) {
	    // element is already at one end of the list...
	    return;
	}
	Collections.swap(children, index, index - i);
	parentNode.getChildren().setAll(children);
    }

    protected void nonGraphical(String viewJson) {
	textArea = new TextArea(viewJson);
	textArea.setMinHeight(500);
	textArea.setMinWidth(500);

	getDialogPane().setContent(textArea);
    }

}
