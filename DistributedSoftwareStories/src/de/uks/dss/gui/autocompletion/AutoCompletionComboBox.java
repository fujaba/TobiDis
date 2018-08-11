package de.uks.dss.gui.autocompletion;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AutoCompletionComboBox<T> extends ComboBox<T> {
	private static final String ENTER_CHAR = "\r";

	private final class KeyHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent event) {
			if (!KeyCode.UP.equals(event.getCode()) && !KeyCode.DOWN.equals(event.getCode())
					&& !KeyCode.RIGHT.equals(event.getCode()) && !KeyCode.LEFT.equals(event.getCode())) {
				autoComplete(getEditor().getText());
			}
			// if (KeyCode.ENTER.equals(event.getCode())) {
			// fireOnValueSet(getValue());
			// }
		}
	}

	java.util.function.Consumer<T> OnValueSet;

	public Consumer<T> getOnValueSet() {
		return OnValueSet;
	}

	public void setOnValueSet(Consumer<T> onValueSet) {
		OnValueSet = onValueSet;
	}

	private void fireOnValueSet(T newValue) {
		if (OnValueSet != null)
			OnValueSet.accept(newValue);
	}

	public AutoCompletionComboBox(ObservableList<T> existingPersonList) {
		super();
		this.getItems().setAll(existingPersonList);

		this.setEditable(true);
		this.withAvailableItems(existingPersonList);

		this.getEditor().setOnKeyReleased(new KeyHandler());
		this.setOnKeyPressed(event -> {
			if (KeyCode.ENTER.equals(event.getCode())) {
				fireOnValueSet(getValue());
			}
		});
	}

	/**
	 * T is a entry of the observableList, String is the Text of the editor
	 * Boolean is the result, whether the entry matches to the text in the
	 * editor
	 */
	private BiFunction<T, String, Boolean> autoCompletionFilter = null;

	public void setAutoCompletionFilter(BiFunction<T, String, Boolean> autoCompletionFilter) {
		this.autoCompletionFilter = autoCompletionFilter;
	}

	private ObservableList<T> availableItems = null;

	public AutoCompletionComboBox<T> withAvailableItems(ObservableList<T> availableItems) {
		this.availableItems = availableItems;
		return this;
	}

	public List<T> filter(ObservableList<T> availableItems, String text) {
		return availableItems.parallelStream().filter(t -> autoCompletionFilter.apply(t, text))
				.collect(Collectors.toList());
	}

	private void autoComplete(String text) {
		if (this.getSelectionModel().getSelectedItem() != null
				&& text.equals(this.getSelectionModel().getSelectedItem().toString())) {
			return;
		}

		if (availableItems == null) {
			return;
		}

		this.hide();

		if (autoCompletionFilter == null) {
			// wrap autoCompletionFilter in order to take the
			throw new RuntimeException("No AutoCompletionFilter set!");
		}

		List<T> filtered = filter(availableItems, text);

		this.getSelectionModel().clearSelection();

		this.getItems().setAll(filtered);
		this.show();
	}
}
