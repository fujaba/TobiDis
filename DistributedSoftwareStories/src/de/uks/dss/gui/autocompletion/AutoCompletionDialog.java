package de.uks.dss.gui.autocompletion;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

public abstract class AutoCompletionDialog<T> extends Dialog<T> {
	protected AutoCompletionComboBox<T> control = null;

	protected AutoCompletionDialog(ObservableList<T> existingObjectsList) {
		super();

		initControl(existingObjectsList);

		this.setResultConverter(new Callback<ButtonType, T>() {
			@Override
			public T call(ButtonType param) {
				if (param.equals(ButtonType.APPLY)) {
					return control.getValue();
				}
				return null;
			}
		});

		Group pane = new Group(control);
		this.getDialogPane().setPadding(new Insets(20));

		this.getDialogPane().setContent(pane);

		this.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

		control.setOnValueSet(t -> AutoCompletionDialog.this.setResult(control.getValue()));

		control.setOnValueSet(t -> this.setResult(control.getValue()));
	}

	protected abstract void initControl(ObservableList<T> list);
}
