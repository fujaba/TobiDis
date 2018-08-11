package de.uks.dss.gui;

import java.util.LinkedList;
import java.util.Optional;

import com.sun.xml.internal.bind.v2.util.EditDistance;

import de.uks.dss.app.DocumentDataCheckManApp;
import de.uks.dss.gui.autocompletion.AutoCompletionDialogPerson;
import de.uks.dss.model.Person;
import de.uks.dss.model.PredefinedDocumentDataConstants;
import de.uks.dss.model.Task;
import de.uks.dss.model.util.PersonSet;
import de.uks.dss.parser.nameLayout.NameParser;
import de.uniks.networkparser.ext.javafx.dialog.DialogBox;
import de.uniks.networkparser.interfaces.SendableEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;

public class TreeCellContentHandlerPerson extends TreeCellContentHandler {

    @Override
    protected boolean impl_startEdit(OwnTreeCell cell, Object item) {
	if (item instanceof Person) {
	    cell.setText(null);
	    cell.setGraphic(addAction(cell, new TextField(((Person) item).getName())));
	    return true;
	}
	return false;
    }

    @Override
    protected boolean impl_saveValue(OwnTreeCell cell, Object item) {
	if (item instanceof Person) {
	    ((Person) item).setName(((TextInputControl) cell.getGraphic()).getText());
	    ((Person) item).getPropertyChangeSupport().firePropertyChange("update", null, null);
	    return true;
	}
	return false;
    }

    @Override
    protected boolean impl_cancelEdit(OwnTreeCell cell) {
	if (cell.getItem() instanceof Person) {
	    return true;
	}
	return false;
    }

    @Override
    protected boolean impl_commitEdit(OwnTreeCell cell, Object newValue) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    protected boolean impl_updateItem(OwnTreeCell cell, Object item, boolean empty) {
	if (item instanceof Person) {
	    cell.setText(NameParser.getName(item));

	    // Editable only, if there's no PersonData with Tag "name"
	    cell.setEditable((((Person) item).getPersonData(PredefinedDocumentDataConstants.NAME) == null));
	    TreeCellContentHandler.createCtxMenu(cell, cell.getTreeItem());
	    return true;
	}
	return false;
    }

    @Override
    protected LinkedList<MenuItem> impl_createCtxMenu(OwnTreeCell cell, TreeItem item) {
	if (item instanceof OwnTreeItem && item.getValue() instanceof Person) {
	    LinkedList<MenuItem> menuItems = new LinkedList(((OwnTreeItem) item).getMenuItems());
	    MenuItem editTag = new MenuItem("Delete Person");
	    editTag.setOnAction(t -> {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete Person");
		alert.setHeaderText("Are you shure?");
		alert.setContentText("Please confirm");

		Optional<ButtonType> shure = alert.showAndWait();

		if (shure.get() == ButtonType.OK) {
		    ((Person) item.getValue()).removeYou();
		}
	    });
	    menuItems.add(editTag);

	    MenuItem moveItem = new MenuItem("Move Person");
	    moveItem.setOnAction(new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
		    TreeItem parent = item.getParent().getParent();
		    Object value = parent.getValue();
		    move((Person) item.getValue(), (SendableEntity) (item.getParent().getParent().getValue()));
		}
	    });
	    menuItems.add(moveItem);

	    MenuItem changeLayout = new MenuItem("ChangeLayout");
	    changeLayout.setOnAction(t -> {
		TextInputDialog dialog = new TextInputDialog(NameParser.getNameLayout(item.getValue()));

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(r -> {
		    NameParser.setNameLayout(item.getValue(), r);
		    CategoryTreeItem parent = (CategoryTreeItem) ((OwnTreeItem) item).getParent();
		    if (parent != null)
			parent.update();
		    else
			((OwnTreeItem) item).update();
		});
	    });
	    menuItems.add(changeLayout);

	    return menuItems;
	}
	return null;
    }

    private void move(Person value, SendableEntity parentGroup) {
	System.out.println(parentGroup);
	if (!(parentGroup instanceof Person)) {
	    return;
	}
	PersonSet existingPersons = DocumentDataCheckManApp.getRoot().getPersons().getMembersTransitive();
	ObservableList<Person> existingPersonList = FXCollections.observableArrayList(existingPersons);
	AutoCompletionDialogPerson dialog = new AutoCompletionDialogPerson(existingPersonList);
	Optional<Person> result = dialog.showAndWait();
	result.ifPresent(t -> {
	    if (t != null) {
		t.withMembers(value);
		((Person) parentGroup).withoutMembers(value);
	    }
	});
    }
}
