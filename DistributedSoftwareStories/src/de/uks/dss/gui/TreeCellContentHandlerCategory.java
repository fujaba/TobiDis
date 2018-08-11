package de.uks.dss.gui;

import java.util.LinkedList;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class TreeCellContentHandlerCategory extends TreeCellContentHandler {

	@Override
	protected boolean impl_startEdit(OwnTreeCell cell, Object item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean impl_saveValue(OwnTreeCell cell, Object item) {
		// TODO Auto-generated method stub
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
		if (item instanceof CategoryTreeItem) {
			cell.setText(((CategoryTreeItem) item).getText());
			cell.setEditable(false);
			createCtxMenu(cell, cell.getTreeItem());
			return true;
		}
		return false;
	}

	@Override
	protected LinkedList<MenuItem> impl_createCtxMenu(OwnTreeCell cell, TreeItem item) {
		if (item instanceof CategoryTreeItem) {
			return ((CategoryTreeItem) item).getMenuItems();
		}
		return null;
	}

}
