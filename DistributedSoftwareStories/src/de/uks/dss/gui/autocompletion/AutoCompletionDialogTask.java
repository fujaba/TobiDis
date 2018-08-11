package de.uks.dss.gui.autocompletion;

import de.uks.dss.model.Task;
import javafx.collections.ObservableList;

public class AutoCompletionDialogTask extends AutoCompletionDialog<Task> {
	public AutoCompletionDialogTask(ObservableList<Task> existingPersonList) {
		super(existingPersonList);

		this.setTitle("Search for existing Task");
		this.setHeaderText("Search for existing Task");
	}

	@Override
	protected void initControl(ObservableList<Task> existingPersonList) {
		control = new AutoCompletionComboBoxTask(existingPersonList);
	}
}
