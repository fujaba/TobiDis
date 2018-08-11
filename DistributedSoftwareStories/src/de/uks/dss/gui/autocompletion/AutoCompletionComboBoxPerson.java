package de.uks.dss.gui.autocompletion;

import java.util.Collections;
import java.util.Comparator;

import de.uks.dss.model.Person;
import de.uks.dss.parser.nameLayout.NameParser;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.util.StringConverter;

public class AutoCompletionComboBoxPerson extends AutoCompletionComboBox<Person> {

	public AutoCompletionComboBoxPerson(ObservableList<Person> existingPersonList) {
		super(existingPersonList);

		// sort the PersonList
		Collections.sort(existingPersonList, new Comparator<Person>() {
			@Override
			public int compare(Person o1, Person o2) {
				if (!o1.getMembers().isEmpty()) {
					if (!o2.getMembers().isEmpty()) {
						// both are groups...
						return o1.getName().compareToIgnoreCase(NameParser.getName(o2));
					} else {
						// 1 is group and 2 not
						return -1;
					}
				} else {
					if (!o2.getMembers().isEmpty()) {
						// 2 is group and 1 not
						return 1;
					} else {
						// both are no Groups...
						return NameParser.getName(o1).compareToIgnoreCase(NameParser.getName(o2).toUpperCase());
					}
				}
			}
		});

		/**
		 * Set the AutoCompletion Filter
		 * 
		 * -> this Is A BiFunction, that takes 1. a Person, 2. the Text to
		 * compare with
		 */
		this.setAutoCompletionFilter((t, text) -> NameParser.getName(t).toUpperCase().contains(text.toUpperCase()));

		/**
		 * Set the Converter, that converts String to Person and vice-versa
		 */
		this.setConverter(new StringConverter<Person>() {
			@Override
			public String toString(Person object) {
				if (object != null) {
					return NameParser.getName(object);
				}
				return null;
			}

			@Override
			public Person fromString(String string) {
				FilteredList<Person> filtered = existingPersonList.filtered(t -> NameParser.getName(t).equals(string));
				if (filtered.size() == 1) {
					return filtered.get(0);
				}
				return null;
			}
		});
	}

}
