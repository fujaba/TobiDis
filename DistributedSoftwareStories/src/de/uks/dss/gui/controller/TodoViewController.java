package de.uks.dss.gui.controller;

import java.util.LinkedList;

import de.uks.dss.app.DocumentDataCheckManApp;
import de.uks.dss.gui.OwnTreeCell;
import de.uks.dss.gui.OwnTreeItem;
import de.uks.dss.gui.editor.GenericEditor;
import de.uks.dss.model.Person;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.json.JsonObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

public class TodoViewController {

    protected static final String VIEWS = "views";
    private static final String SEPERATOR = "||";
    @FXML
    TreeView treeView;
    private Person personRoot;
    @FXML
    Button checklistButton;
    @FXML
    Button menuSceneButton;
    @FXML
    Pane editPane;
    @FXML
    Button saveButton;
    @FXML
    Button cancelButton;
    private GenericEditor genericEditor;
    protected OwnTreeItem selectedItem;

    public void init(Person personRoot) {
	this.personRoot = personRoot;
	this.treeView.setCellFactory(c -> new OwnTreeCell<>());
	this.treeView.setRoot(new OwnTreeItem("Todo", personRoot, null));
	treeView.setEditable(true);
    }

    @SuppressWarnings("unchecked")
    public void initialize() {
	genericEditor = new GenericEditor();
	genericEditor.setOnUpdate(t -> {
	    createEditView();
	    return null;
	});
	treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

	    @Override
	    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
		if (newValue instanceof OwnTreeItem) {
		    selectedItem = (OwnTreeItem) newValue;
		    LinkedList<Callback<Boolean, Void>> saveActions = new LinkedList<Callback<Boolean, Void>>();

		    JsonObject viewLayout = genericEditor.getViewLayout();

		    genericEditor.createView(editPane, (SendableEntity) selectedItem.getValue(), viewLayout);

		    // Set actions for Save and Cancel Button
		    saveButton.setOnAction(t -> {
			genericEditor.save(true);
		    });
		    cancelButton.setOnAction(t -> {
			genericEditor.save(false);
		    });
		} else {
		    selectedItem = null;
		    genericEditor.clear();
		    editPane.getChildren().clear();

		    // Set actions for Save and Cancel Button
		    saveButton.setOnAction(null);
		    cancelButton.setOnAction(null);
		}
	    }
	});

	MenuItem editViewNonGraphical = new MenuItem("Edit Views");
	editViewNonGraphical.setOnAction(t -> {
	    genericEditor.editViews(false);
	    createEditView();
	});
	MenuItem editViewGraphical = new MenuItem("Edit Views Graphical");
	editViewGraphical.setOnAction(t -> {
	    genericEditor.editViews(true);
	    createEditView();
	});
	ContextMenu contextMenu = new ContextMenu(editViewNonGraphical, editViewGraphical);
	editPane.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
	    @Override
	    public void handle(ContextMenuEvent event) {
		contextMenu.show(editPane, event.getScreenX(), event.getScreenY());
	    }
	});

    }

    protected void createEditView() {
	if (selectedItem == null) {
	    genericEditor.clear();
	} else {
	    JsonObject viewLayout = genericEditor.getViewLayout();

	    genericEditor.createView(editPane, (SendableEntity) selectedItem.getValue(), viewLayout);

	    // Set actions for Save and Cancel Button
	    saveButton.setOnAction(t -> {
		genericEditor.save(true);
	    });
	    cancelButton.setOnAction(t -> {
		genericEditor.save(false);
	    });
	}

    }

    protected void updateView() {
	// assuming the first is the root...
	OwnTreeItem treeItem = (OwnTreeItem) treeView.getTreeItem(0);
	treeItem.update();
    }

    public void switchToMenuScene() {
	DocumentDataCheckManApp.switchToMenuScene();
    }

    public void switchToChecklistScene() {
	DocumentDataCheckManApp.switchToChecklistScene();
    }

}
