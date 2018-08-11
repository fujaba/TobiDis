package de.uks.dss.gui;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;

public class OwnTreeCell<Object> extends TreeCell<Object> {

	static enum EditType {
		TAG, VALUE
	}

	Node graphicTmpEdit;
	String textTmpEdit;

	public OwnTreeCell() {
	}

	@Override
	public void startEdit() {
		if (isEditing())
			return;
		super.startEdit();
		save = true;
		TreeView<Object> tree = getTreeView();
		if (isEditable() && (tree != null && tree.isEditable())) {
			graphicTmpEdit = this.getGraphic();
			textTmpEdit = this.getText();

			Object item = getItem();
			if (TreeCellContentHandler.startEdit(this)) {
				// handled, everything is ok..
			} else {
				// shouldn't happen, because not editable...
				System.out.println("OwnTreeCell.startEdit()" + item);
			}

		}
	}

	boolean save = true;
	EditType editType;

	void saveValue() {
		if (!save)
			return;
		Object item = OwnTreeCell.this.getItem();
		if (TreeCellContentHandler.saveValue(this)) {
		} else {
			// shouldn't happen, because not editable...
			System.out.println("OwnTreeCell.saveValue()" + item);
		}
		commitEdit(item);
	}

	@Override
	public void cancelEdit() {
		if (!isEditing())
			return;
		TreeCellContentHandler.cancelEdit(this);
		super.cancelEdit();
		this.setGraphic(graphicTmpEdit);
		this.setText(textTmpEdit);
		graphicTmpEdit = null;
		textTmpEdit = null;
		this.editType = EditType.VALUE;
	}

	@Override
	public void commitEdit(Object newValue) {
		TreeCellContentHandler.commitEdit(this, newValue);
		super.commitEdit(newValue);
		this.editType = EditType.VALUE;
		cancelEdit();
	}

	@Override
	protected void updateItem(Object item, boolean empty) {
		super.updateItem(item, empty);

		// reset old things
		this.setTooltip(null);
		this.setContextMenu(null);
		setText(null);
		setGraphic(null);
		this.setEditable(false);

		//
		if (!(empty || item == null)) {
			{
				if (TreeCellContentHandler.updateItem(this, item, empty)) {
					// Handled -> everything fine
				} else {
					this.setEditable(false);
					setText(item.toString());
				}
			}
		}
	}

}
