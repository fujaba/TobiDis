package de.uks.dss.gui.autocompletion;

import de.uks.dss.model.Person;
import javafx.collections.ObservableList;

public class AutoCompletionDialogPerson extends AutoCompletionDialog<Person> {
	public AutoCompletionDialogPerson(ObservableList<Person> existingPersonList) {
		super(existingPersonList);

		this.setTitle("Search for existing Person");
		this.setHeaderText("Search for existing Person");
	}

	@Override
	protected void initControl(ObservableList<Person> existingPersonList) {
		control = new AutoCompletionComboBoxPerson(existingPersonList);
	}
}
