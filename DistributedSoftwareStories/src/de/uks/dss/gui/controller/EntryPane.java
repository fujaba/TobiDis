package de.uks.dss.gui.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Comparator;

import de.uks.dss.app.DocumentDataCheckManApp;
import de.uks.dss.model.DocumentData;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class EntryPane<TT extends Control, ET extends TreeItem> extends TreeItem<Control>
		implements Comparable<Object> {

	private final class ComparatorImplementation implements Comparator<TreeItem> {
		@Override
		public int compare(TreeItem o1, TreeItem o2) {
			if (Comparable.class.isAssignableFrom(o1.getClass())) {
				if (Comparable.class.isAssignableFrom(o2.getClass())) {
					// System.out.println("Comparable.." + o1 + ", " + o2);
					return ((Comparable<Object>) o1).compareTo(o2);
				} else {
					System.err.println("Not comparable.." + o1 + ", " + o2);
					// TODO if type = Entry
					// -> take tagBox of Entry
					// return 0;
				}
			} else {
				return 0;
			}
			return 0;
		}
	}

	private CheckBox foldCheckBox = new CheckBox();
	private Control tagBox;
	private boolean editable = true;
	private VBox valueBox;
	private PropertyChangeListener foldCheckBoxListener;
	private int priority = 0;
	private EventHandler<ContextMenuEvent> contextMenuEvent;

	private boolean sorted = true;

	public EntryPane(DocumentData foldData) {
		this.expandedProperty().addListener((o, oldVal, newVal) -> {
			foldChange(foldData);
			String newValue = "" + this.expandedProperty().get();
			foldData.setValue(newValue);
		});
		foldChange(foldData);
	}

	/**
	 * The Constructor only for the Root Element.. The resulting EntryPane has
	 * no Checkbox and TagBox, only a ValueBox
	 */
	public EntryPane() {
	}

	private void style(Region node) {
		node.setMaxWidth(Double.MAX_VALUE);
	}

	public void setFoldCheckBox(CheckBox foldCheckBox, DocumentData foldData) {
		if (this.foldCheckBox != null) {
			// remove listener
			Platform.runLater(() -> this.getChildren().remove(this.foldCheckBox));
			this.foldCheckBox.setOnAction(null);
			// TODO - remove propertyChangeListener from the old foldData...
			this.foldCheckBox = null;
		}
		foldCheckBoxListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				foldChange(foldData);
			}
		};

		this.foldCheckBox = foldCheckBox;
		foldCheckBox.setOnAction((e) -> {
			String newValue = "" + foldCheckBox.isSelected();
			foldData.setValue(newValue);
		});

		foldData.addPropertyChangeListener(DocumentData.PROPERTY_VALUE, foldCheckBoxListener);
		foldChange(foldData);
	}

	public CheckBox getFoldCheckBox() {
		return foldCheckBox;
	}

	public VBox getValueBox() {
		if (valueBox == null) {
			valueBox = new VBox(6);
			style(valueBox);
		}
		return valueBox;
	}

	/**
	 * TODO - Maybe use Objects directly and use TreeCellFactory instead of
	 * setting the nodes manually...
	 */
	public void setTagBox(Control tagBox) {
		if (this.tagBox != null) {
			// Platform.runLater(() -> this.getChildren().remove(this.tagBox));
			Platform.runLater(() -> this.setValue(null));
			if (contextMenuEvent != null) {
				tagBox.removeEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, this.contextMenuEvent);
			}
			this.tagBox = null;
		}
		this.tagBox = tagBox;

		if (tagBox.getClass().equals(Label.class)) {
			Label tagLabel = (Label) tagBox;
			if (editable) {
				initEditable(tagLabel);
			}
		} else if (tagBox.getClass().equals(TextField.class)) {
			TextField textField = (TextField) tagBox;
		}

		Platform.runLater(() -> {
			Platform.runLater(() -> this.setValue(tagBox));
			if (contextMenuEvent != null) {
				tagBox.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, this.contextMenuEvent);
			}
		});
	}

	public String getTagBoxValue() {
		if (tagBox == null) {
			return "";
		}
		if (tagBox.getClass().equals(Label.class)) {
			Label tagLabel = (Label) tagBox;
			return tagLabel.getText().toUpperCase();
		} else if (tagBox.getClass().equals(TextField.class)) {
			TextField textField = (TextField) tagBox;
			return textField.getText().toUpperCase();
		} else {
			return "";
		}
	}

	public String getComparableValue() {
		if (this.getValue() == null) {
			return priority + "";
		}
		if (this.getValue().getClass().equals(Label.class)) {
			Label tagLabel = (Label) this.getValue();
			return priority + tagLabel.getText().toUpperCase();
		} else if (this.getValue().getClass().equals(TextField.class)) {
			TextField textField = (TextField) this.getValue();
			return priority + textField.getText().toUpperCase();
		} else {
			return priority + "";
		}
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void addEntry(TreeItem... entry) {
		Platform.runLater(() -> {
			for (TreeItem et : entry) {
				this.getChildren().add(et);
			}
			if (isSorted()) {
				sort();
			}
		});
	}

	public void sort() {
		Platform.runLater(() -> {
			ObservableList<TreeItem<Control>> observableArrayList = FXCollections
					.observableArrayList(this.getChildren());
			Collections.sort(observableArrayList, new ComparatorImplementation());
			this.getChildren().setAll(observableArrayList);
		});
	}

	/**
	 * Adds tagListVBox to topGrid2, if tagFoldCheck is selected, and removes
	 * it, if tagFoldCheck is not selected
	 * 
	 * @param tagUnfoldedData
	 * @param tagFoldCheck
	 * @param topGrid2
	 * @param tagListVBox
	 */
	private void foldChange(DocumentData tagUnfoldedData) {
		if (Boolean.parseBoolean(tagUnfoldedData.getValue()) != this.expandedProperty().get()) {
			Platform.runLater(() -> this.expandedProperty().set(Boolean.parseBoolean(tagUnfoldedData.getValue())));
		}
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

	public void setOnContextMenuRequested(EventHandler<ContextMenuEvent> event) {
		this.contextMenuEvent = event;
		if (tagBox != null) {
			tagBox.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, this.contextMenuEvent);
		}
	}

	public boolean isSorted() {
		return sorted;
	}

	public void setSorted(boolean sorted) {
		this.sorted = sorted;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@SuppressWarnings("unchecked")
	private void initEditable(Label label) {
		// label.setOnMouseClicked
		DocumentDataCheckManApp.getTreeView().editingItemProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue o, Object old, Object newval) {
				if (newval == null || newval.equals(false)) {
					return;
				}
				if (Observable.class.isAssignableFrom(o.getClass())) {
					Observable o2 = (Observable) o;
					if (newval != null && newval.equals(EntryPane.this)) {
						EntryPane<TT, ET> entryPane = EntryPane.this;
						if (entryPane.tagBox != null && entryPane.tagBox.getClass().equals(Label.class)) {
							// Label should be switched to TextField
							Label graphic2 = (Label) entryPane.tagBox;
							TextField textField = new TextField();
							textField.textProperty().bindBidirectional(graphic2.textProperty());

							textField.setPrefWidth(Control.USE_COMPUTED_SIZE);
							textField.setMaxWidth(Double.MAX_VALUE);

							textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
								@Override
								public void handle(KeyEvent event) {
									if (KeyCode.ENTER.equals(event.getCode())) {
										// save text
										graphic2.fireEvent(event);

										// reset graphic
										Platform.runLater(() -> {
											entryPane.setTagBox(graphic2);
										});
										graphic2.requestFocus();
									} else if (KeyCode.ESCAPE.equals(event.getCode())) {
										// reset text..
										graphic2.fireEvent(event);

										// reset graphic
										Platform.runLater(() -> {
											entryPane.setTagBox(graphic2);
										});
									}
								}
							});

							final class ChangeListenerImplementation implements ChangeListener {
								private ReadOnlyBooleanProperty readOnlyBooleanProperty;

								public ChangeListenerImplementation(ReadOnlyBooleanProperty readOnlyBooleanProperty) {
									this.readOnlyBooleanProperty = readOnlyBooleanProperty;
								}

								@Override
								public void changed(ObservableValue observable, Object oldValue, Object newValue) {
									if (newValue != null && newValue.equals(false)) {
										KeyEvent keyEvent = new KeyEvent(graphic2, graphic2, KeyEvent.ANY, "ENTER",
												"ENTER", KeyCode.ENTER, false, false, false, false);
										graphic2.getOnKeyPressed().handle(keyEvent);

										// reset graphic
										entryPane.setTagBox(graphic2);

										readOnlyBooleanProperty.removeListener(this);
									}
								}
							}

							textField.focusedProperty()
									.addListener(new ChangeListenerImplementation(textField.focusedProperty()));

							entryPane.setTagBox(textField);

							Platform.runLater(() -> {
								textField.requestFocus();
							});
						}
					}
				} else {
					return;
				}

			}
		});
	}
}
