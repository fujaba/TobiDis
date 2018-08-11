package de.uks.dss.gui;

import java.util.LinkedList;
import java.util.Optional;

import de.uks.dss.model.DocumentData;
import de.uks.dss.model.Person;
import static de.uks.dss.model.PredefinedDocumentDataConstants.*;
import de.uks.dss.model.Task;
import de.uks.dss.parser.nameLayout.NameParser;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;

public class TreeCellContentHandlerTask extends TreeCellContentHandler {

	@Override
	protected boolean impl_startEdit(OwnTreeCell cell, Object item) {
		if (item instanceof Task) {
			cell.setText(null);
			cell.setGraphic(addAction(cell, new TextField(((Task) item).getName())));
			return true;
		}
		return false;
	}

	@Override
	protected boolean impl_saveValue(OwnTreeCell cell, Object item) {
		if (item instanceof Task) {
			((Task) item).setName(((TextInputControl) cell.getGraphic()).getText());
			((Task) item).getPropertyChangeSupport().firePropertyChange("update", null, null);
			return true;
		}
		return false;
	}

	@Override
	protected boolean impl_cancelEdit(OwnTreeCell cell) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean impl_commitEdit(OwnTreeCell cell, Object newValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean impl_updateItem(OwnTreeCell cell, Object item, boolean empty) {
		if (item instanceof Task) {
			cell.setText(NameParser.getName(item));

			// Editable only, if there's no PersonData with Tag "name"
			cell.setEditable((((Task) item).getTaskData(NAME) == null));
			
			TreeCellContentHandler.createCtxMenu(cell, cell.getTreeItem());
			return true;
		}
		return false;
	}

	@Override
	protected LinkedList<MenuItem> impl_createCtxMenu(OwnTreeCell cell, TreeItem item) {
		if (item instanceof OwnTreeItem && item.getValue() instanceof Task) {
			LinkedList<MenuItem> menuItems = new LinkedList(((OwnTreeItem) item).getMenuItems());
			MenuItem editTag = new MenuItem("Delete Task");
			editTag.setOnAction(t -> {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Delete Task");
				alert.setHeaderText("Are you shure?");
				alert.setContentText("Please confirm");

				Optional<ButtonType> shure = alert.showAndWait();

				if (shure.get() == ButtonType.OK) {
					((Task) item.getValue()).removeYou();
				}
			});
			menuItems.add(editTag);

			MenuItem changeLayout = new MenuItem("ChangeLayout");
			changeLayout.setOnAction(t -> {
				TextInputDialog dialog = new TextInputDialog(NameParser.getNameLayout(item.getValue()));

				Optional<String> result = dialog.showAndWait();
				result.ifPresent(r -> {
					NameParser.setNameLayout(item.getValue(), r);
					(((OwnTreeItem) item)).update();
				});
			});
			menuItems.add(changeLayout);

			return menuItems;
		}
		return null;
	}

}
