package de.uks.dss.gui.autocompletion;

import java.util.Collections;
import java.util.Comparator;

import org.sdmlib.models.pattern.PatternObject;

import de.uks.dss.model.Person;
import de.uks.dss.parser.nameLayout.NameParser;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.util.StringConverter;

public class AutoCompletionComboBoxPatternObject extends AutoCompletionComboBox<PatternObject> {

	public AutoCompletionComboBoxPatternObject(ObservableList<PatternObject> existingPOList) {
		super(existingPOList);

		// sort the PersonList
		Collections.sort(existingPOList, new Comparator<PatternObject>() {
			@Override
			public int compare(PatternObject o1, PatternObject o2) {
				return o1.getLHSPatternObjectName().compareToIgnoreCase(o2.getLHSPatternObjectName());
			}
		});

		/**
		 * Set the AutoCompletion Filter
		 * 
		 * -> this Is A BiFunction, that takes 1. a PatternObject, 2. the Text to
		 * compare with
		 */
		this.setAutoCompletionFilter((t, text) -> t.getLHSPatternObjectName().toUpperCase().contains(text.toUpperCase()));

		/**
		 * Set the Converter, that converts String to Person and vice-versa
		 */
		/*this.setConverter(new StringConverter<PatternObject>() {
			@Override
			public String toString(PatternObject object) {
				object.getLHSPatternObjectName();
				return null;
			}

			@Override
			public PatternObject fromString(String string) {
				FilteredList<PatternObject> filtered = existingPOList.filtered(t -> t.getLHSPatternObjectName().equals(string));
				if (filtered.size() == 1) {
					return filtered.get(0);
				}
				return null;
			}
		});*/
	}

}
