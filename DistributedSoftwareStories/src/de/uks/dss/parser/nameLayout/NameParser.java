package de.uks.dss.parser.nameLayout;

import de.uks.dss.app.DocumentDataCheckManApp;
import de.uks.dss.model.DocumentData;
import de.uks.dss.model.DocumentDataType;
import de.uks.dss.model.Person;
import de.uks.dss.model.Task;

/**
 * This class finds the LayoutInfo for the given Object and asks the
 * NameLayoutParser for the resulting name
 * 
 * @author alexw
 *
 */
public class NameParser {

    private static final String DEFAULT_VALUE = "<name>";

    public static String getName(Object item) {
	// The string, we want to parse..
	String nameLayout = getNameLayout(item);
	return getName(item, nameLayout);
    }

    public static String getName(Object object, String nameLayout) {
	String tmpNameLayout = nameLayout;
	if (object instanceof Person) {
	    return NameLayoutReader.parsePerson((Person) object, nameLayout);
	} else if (object instanceof Task) {
	    return NameLayoutReader.parseTask((Task) object, nameLayout);
	}

	return object.toString();
    }

    private static Task root;

    public static Task getRoot() {
	if (root == null) {
	    root = DocumentDataCheckManApp.getRoot();
	}
	return root;
    }

    public static void setRoot(Task val) {
	root = val;
    }

    private static DocumentData rootUser;

    public static DocumentData getRootUser() {
	if (rootUser == null) {
	    rootUser = DocumentDataCheckManApp.getUser();
	}
	return rootUser;
    }

    public static void setRootUser(DocumentData val) {
	rootUser = val;
    }

    public static DocumentData getNameLayoutData(Object item) {
	// first check, whether there is a layoutInfo for the User only (local
	// Layout)
	DocumentData user = getRootUser();
	DocumentData taskData = null;
	if (user != null) {
	    taskData = user.getSubData().filterType(DocumentDataType.LAYOUTINFO.toString())
		    .filterTag(item.getClass().toGenericString()).first();
	    if (taskData != null) {
		return taskData;
	    }
	}

	// if not, check if there is a global layout info
	if (getRoot() != null) {
	    taskData = getRoot().getTaskData().filterType(DocumentDataType.LAYOUTINFO.toString())
		    .filterTag(item.getClass().toGenericString()).first();
	    if (taskData == null) {
		// create doc data with default value
		taskData = getRoot().createTaskData().withType(DocumentDataType.LAYOUTINFO.toString())
			.withTag(item.getClass().toGenericString()).withValue(DEFAULT_VALUE);
	    }
	    return taskData;
	}
	// there's no layoutInfo:
	return null;
    }

    public static String getNameLayout(Object item) {
	if (getRoot() == null && getRootUser() == null) {
	    // default value, if threre's no root and thus no layout Data
	    return DEFAULT_VALUE;
	}

	// try to find layout data
	DocumentData nameLayoutData = getNameLayoutData(item);
	if (nameLayoutData != null) {
	    // the layout data should contain the Layout of the Name as the
	    // value
	    // e.g. "<name>, <surname>
	    return getNameLayoutData(item).getValue();
	}
	return DEFAULT_VALUE;
    }

    public static void setNameLayout(Object item, String string) {
	if (getRoot() == null) {
	    // default, if threres no root
	    return;
	}
	getNameLayoutData(item).setValue(string);
    }
}
