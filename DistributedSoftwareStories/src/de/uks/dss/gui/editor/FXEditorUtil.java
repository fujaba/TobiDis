package de.uks.dss.gui.editor;

import static de.uks.dss.model.PredefinedDocumentDataConstants.dateFormatter;

import java.time.LocalDate;

import de.uks.dss.model.DocumentDataType;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FXEditorUtil {
    public static Node docDataTypeToNode(DocumentDataType type) {
	switch (type) {
	case BOOLEAN:
	    return new CheckBox();
	case DATE:
	    return new DatePicker();
	case ICON:
	    return new ImageView();
	case FORM:
	    return new Hyperlink();
	case DOCUMENT_DATA_TYPE:
	    return new ChoiceBox<DocumentDataType>(FXCollections.observableArrayList(DocumentDataType.values()));
	case EMAIL:
	case STRING:
	default:
	    return new TextField();
	}
    }

    /**
     * CheckBox expects boolean. DatePicker expects LocalDate
     * 
     * @param node
     * @param value
     * @return
     */
    public static boolean setValue(Node node, Object value) {
	if(value == null){
	    value = "";
	}
	if (node instanceof CheckBox) {
	    if (value instanceof Boolean) {
		((CheckBox) node).setSelected((Boolean) value);
		return true;
	    } else {
		// try to parse value as a string
		((CheckBox) node).setSelected(Boolean.parseBoolean(value.toString()));
		return true;
	    }
	} else if (node instanceof DatePicker) {
	    if (value instanceof LocalDate) {
		((DatePicker) node).setValue((LocalDate) value);
		return true;
	    } else {
		// try to parse value as a string
		LocalDate date;
		try {
		    date = LocalDate.parse(value.toString(), dateFormatter);
		} catch (Exception e) {
		    date = LocalDate.now();
		}
		LocalDate finalDate = date;
		FXEditorUtil.setValue(node, finalDate);
		return true;
	    }
	} else if (node instanceof ImageView) {
	    if (value instanceof Image) {
		((ImageView) node).setImage((Image) value);
		return true;
	    } else {
		// try to parse value as a string
		try {
		    ((ImageView) node).setImage(new Image(value.toString(), 50, 50, true, true));
		} catch (Exception e) {
		    System.err.println("Invalid URL for Image...");
		    return false;
		}
		return true;
	    }
	} else if (node instanceof Hyperlink) {
	    if (value instanceof String) {
		((Hyperlink) node).setText((String) value);
		return true;
	    } else {
		// try to parse value as a string
		((Hyperlink) node).setText(value.toString());
		return true;
	    }
	} else if (node instanceof TextField) {
	    if (value instanceof String) {
		((TextField) node).setText((String) value);
		return true;
	    } else {
		((TextField) node).setText(value.toString());
		return true;
	    }
	} else if (node instanceof ChoiceBox) {
	    if (value instanceof String) {
		value = DocumentDataType.valueOf(value.toString());
	    }
	    ((ChoiceBox) node).setValue(value);
	}
	return false;
    }

    public static Object getValue(Node node) {
	if (node instanceof CheckBox) {
	    return ((CheckBox) node).isSelected();
	} else if (node instanceof DatePicker) {
	    return ((DatePicker) node).getValue();
	} else if (node instanceof ImageView) {
	    return ((ImageView) node).getImage();
	} else if (node instanceof Hyperlink) {
	    return ((Hyperlink) node).getText();
	} else if (node instanceof ChoiceBox) {
	    return ((ChoiceBox) node).getValue();
	} else if (node instanceof TextField) {
	    return ((TextField) node).getText();
	}
	return null;
    }
}
