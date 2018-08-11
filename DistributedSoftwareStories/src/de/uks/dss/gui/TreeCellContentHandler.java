package de.uks.dss.gui;

import java.util.LinkedList;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public abstract class TreeCellContentHandler {
	protected static LinkedList<TreeCellContentHandler> handlerPool = new LinkedList<TreeCellContentHandler>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4091913931994198208L;

		{
			add(new TreeCellContentHandlerCategory());
			add(new TreeCellContentHandlerDocumentData());
			add(new TreeCellContentHandlerPerson());
			add(new TreeCellContentHandlerTask());
		}
	};

	public static boolean startEdit(OwnTreeCell cell) {
		Object item = cell.getItem();
		for (TreeCellContentHandler treeCellContentHandler : handlerPool) {
			if (treeCellContentHandler.impl_startEdit(cell, item)) {
				return true;
			}
		}
		return false;
	}

	protected abstract boolean impl_startEdit(OwnTreeCell cell, Object item);

	public static boolean saveValue(OwnTreeCell cell) {
		if (!cell.save)
			return true;
		Object item = cell.getItem();
		for (TreeCellContentHandler treeCellContentHandler : handlerPool) {
			if (treeCellContentHandler.impl_saveValue(cell, item)) {
				return true;
			}
		}
		return false;
	}

	protected abstract boolean impl_saveValue(OwnTreeCell cell, Object item);

	public static boolean cancelEdit(OwnTreeCell cell) {
		for (TreeCellContentHandler treeCellContentHandler : handlerPool) {
			if (treeCellContentHandler.impl_cancelEdit(cell)) {
				return true;
			}
		}
		return false;
	}

	protected abstract boolean impl_cancelEdit(OwnTreeCell cell);

	public static boolean commitEdit(OwnTreeCell cell, Object newValue) {
		for (TreeCellContentHandler treeCellContentHandler : handlerPool) {
			if (treeCellContentHandler.impl_commitEdit(cell, newValue)) {
				return true;
			}
		}
		return false;
	}

	protected abstract boolean impl_commitEdit(OwnTreeCell cell, Object newValue);

	public static boolean updateItem(OwnTreeCell cell, Object item, boolean empty) {
		for (TreeCellContentHandler treeCellContentHandler : handlerPool) {
			if (treeCellContentHandler.impl_updateItem(cell, item, empty)) {
				return true;
			}
		}
		return false;
	}

	protected abstract boolean impl_updateItem(OwnTreeCell cell, Object item, boolean empty);

	protected static boolean createCtxMenu(OwnTreeCell cell, TreeItem item) {
		if (item == null) {
			cell.setContextMenu(null);
			return true;
		}
		cell.setContextMenu(null);
		for (TreeCellContentHandler treeCellContentHandler : handlerPool) {
			LinkedList<MenuItem> ctxMenu = treeCellContentHandler.impl_createCtxMenu(cell, item);
			if (ctxMenu != null) {
				setCtxMenu(cell, ctxMenu);
				return true;
			}
		}
		return false;
	}

	private static void setCtxMenu(OwnTreeCell cell, LinkedList<MenuItem> menuItems) {
		if (menuItems.size() > 0) {
			ContextMenu ctxMenu = new ContextMenu();
			for (MenuItem menuItem : menuItems) {
				ctxMenu.getItems().add(menuItem);
			}
			cell.setContextMenu(ctxMenu);
		}
	}

	protected abstract LinkedList<MenuItem> impl_createCtxMenu(OwnTreeCell cell, TreeItem item);

	protected Node addAction(OwnTreeCell cell, Node textField) {
		cell.save = true;
		textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (KeyCode.ESCAPE.equals(event.getCode())) {
					// reset without saving
					cell.save = false;
					cell.cancelEdit();
				} else if (KeyCode.ENTER.equals(event.getCode())) {
					cell.saveValue();
					// now saved, so don't save again
					cell.save = false;
				}
			}
		});
		textField.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (oldVal == true && newVal == false) {
				// on Focusloss, save the changes
				cell.saveValue();
			}
		});
		return textField;
	}
}
