package de.uks.dss.gui.controller;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;

public class EntryBox extends TreeItem<HBox> implements Comparable<Object> {

	TextField tagBox;
	Node valueBox;

	HBox tagHBox = new HBox();
	HBox valueHBox = new HBox();
	private int priority = 0;
	private HBox hBox;

	public EntryBox() {
		hBox = new HBox(tagHBox, valueHBox);
		this.setValue(hBox);
		style();
	}

	private void style() {
		this.hBox.setSpacing(2);
		this.hBox.setMaxWidth(Double.MAX_VALUE);
	}

	public void setTagBox(TextField tagBox) {
		Platform.runLater(() -> {
			if (this.tagBox != null) {
				// remove old TagBox
				tagHBox.getChildren().remove(this.tagBox);
				this.tagBox = null;
			}

			if (tagBox != null) {
				this.tagBox = tagBox;
				tagHBox.getChildren().add(tagBox);
			}
		});
	}

	public TextField getTagBox() {
		return tagBox;
	}

	public void setValueBox(Node valueBox) {
		Platform.runLater(() -> {
			if (this.valueBox != null) {
				// remove old ValueBox
				valueHBox.getChildren().remove(this.valueBox);
				this.valueBox = null;
			}

			if (valueBox != null) {
				this.valueBox = valueBox;
				valueHBox.getChildren().add(this.valueBox);
			}
		});
	}

	public Node getValueBox() {
		return valueBox;
	}

	public String getComparableValue() {
		if (getTagBox() != null) {
			return priority + getTagBox().getText().toUpperCase();
		}
		return "";
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public int compareTo(Object o) {
		if (o.getClass().equals(EntryPane.class)) {
			return this.getComparableValue().compareTo(((EntryPane) o).getComparableValue());
		} else if (o.getClass().equals(EntryBox.class)) {
			return this.getComparableValue().compareTo(((EntryBox) o).getComparableValue());
		} else {
			return this.getComparableValue().compareTo(o.toString());
		}
	}

}
