package de.uks.dss.gui;

import static de.uks.dss.model.PredefinedDocumentDataConstants.FALSE;
import static de.uks.dss.model.PredefinedDocumentDataConstants.TRUE;
import static de.uks.dss.model.PredefinedDocumentDataConstants.dateFormat;

import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;

import de.uks.dss.app.DocumentDataCheckManApp;
import de.uks.dss.gui.autocompletion.AutoCompletionDialogPerson;
import de.uks.dss.gui.autocompletion.AutoCompletionDialogTask;
import de.uks.dss.model.DocumentData;
import de.uks.dss.model.DocumentDataType;
import de.uks.dss.model.Person;
import de.uks.dss.model.PredefinedDocumentDataConstants;
import de.uks.dss.model.Task;
import de.uks.dss.model.util.PersonSet;
import de.uks.dss.model.util.TaskSet;
import de.uniks.networkparser.interfaces.SendableEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class OwnTreeItem extends TreeItem<Object> {
    private LinkedList<CategoryTreeItem> categories = new LinkedList<>();
    private TreeItem<Object> parentTreeitem;
    private LinkedList<MenuItem> menuItems = new LinkedList<>();
    private String tagAddition;

    public LinkedList<MenuItem> getMenuItems() {
	return menuItems;
    }

    public OwnTreeItem(String tagAddition, SendableEntity value, TreeItem<Object> parentTreeitem,
	    MenuItem... MenuItems) {
	super(value);
	this.tagAddition = tagAddition;
	this.parentTreeitem = parentTreeitem;

	for (MenuItem menuItem : MenuItems) {
	    menuItems.add(menuItem);
	}

	if (value instanceof Task) {
	    // Persons of Task
	    MenuItem addPerson = new MenuItem("Add new Person to Task");
	    addPerson.setOnAction(t -> ((Task) value).createPersons().withName("PersonName?").createPersonData()
		    .withTag(PredefinedDocumentDataConstants.NAME).withValue("PersonName?"));
	    MenuItem addExistingPerson = new MenuItem("Add existing Person to Task");
	    addExistingPerson.setOnAction(t -> this.getExistingPerson((Task) value));
	    CategoryTreeItem categoryTreeItem = new CategoryTreeItem(this.tagAddition, "Persons",
		    t -> ((Task) value).getPersons(), this, addPerson, addExistingPerson);
	    categories.add(categoryTreeItem);
	    this.getChildren().add(categoryTreeItem);

	    // Taskdata
	    MenuItem addData = new MenuItem("Add Data to Task");
	    addData.setOnAction(t -> createDocData(((Task) value).createTaskData()));
	    CategoryTreeItem dataCategory = new CategoryTreeItem(this.tagAddition, "Data", t -> ((Task) value)
		    .getTaskData().filter(dd -> !DocumentDataType.LAYOUTINFO.toString().equals(dd.getType())), this,
		    addData);
	    categories.add(dataCategory);
	    this.getChildren().add(dataCategory);

	    // "Todo"
	    MenuItem addTask = new MenuItem("Add SubTask");
	    addTask.setOnAction(t -> ((Task) value).createSubTasks().withName("TaskName?"));
	    MenuItem addExistingTask = new MenuItem("Add existing Task to SubTasks");
	    addExistingTask.setOnAction(t -> this.getExistingTask((Task) value));
	    CategoryTreeItem subTaskCategory = new CategoryTreeItem(this.tagAddition, "ToDo",
		    t -> ((Task) value).getSubTasks().filter(d -> {
			if (d.getParentTasksTransitive().getTaskData()
				.filterTag(PredefinedDocumentDataConstants.STORY_CONTAINER).size() == 0)
			    return true;
			else
			    return false;
		    }).filter(d -> {
			// Filter for already done
			String done = d.getTaskData().filterTag(PredefinedDocumentDataConstants.DONE).getValue()
				.first();
			if (done != null && Boolean.parseBoolean(done)) {
			    return false;
			} else {
			    return true;
			}
		    }), this, addTask, addExistingTask);
	    categories.add(subTaskCategory);
	    this.getChildren().add(subTaskCategory);

	    // "DONE"
	    CategoryTreeItem doneTasksCategory = new CategoryTreeItem(this.tagAddition, "Done",
		    t -> ((Task) value).getSubTasks().filter(d -> {
			if (d.getParentTasksTransitive().getTaskData()
				.filterTag(PredefinedDocumentDataConstants.STORY_CONTAINER).size() == 0)
			    return true;
			else
			    return false;
		    }).filter(d -> {
			// Filter for already done
			String done = d.getTaskData().filterTag(PredefinedDocumentDataConstants.DONE).getValue()
				.first();
			if (done != null && Boolean.parseBoolean(done)) {
			    return true;
			} else {
			    return false;
			}
		    }), this);
	    categories.add(doneTasksCategory);
	    this.getChildren().add(doneTasksCategory);
	} else if (value instanceof Person) {
	    MenuItem addPerson = new MenuItem("Add new Member");
	    addPerson.setOnAction(t -> ((Person) value).createMembers().withName("PersonName?").createPersonData()
		    .withTag(PredefinedDocumentDataConstants.NAME).withValue("PersonName?"));
	    MenuItem addExistingPerson = new MenuItem("Add existing Person to Group");
	    addExistingPerson.setOnAction(t -> getExistingPerson((Person) value));
	    CategoryTreeItem personCategory = new CategoryTreeItem(this.tagAddition, "Persons",
		    (param) -> ((Person) value).getMembers(), this, addPerson, addExistingPerson);
	    categories.add(personCategory);
	    this.getChildren().add(personCategory);

	    MenuItem addData = new MenuItem("Add Data to Person");
	    addData.setOnAction(t -> createDocData(((Person) value).createPersonData()));
	    CategoryTreeItem dataCategory = new CategoryTreeItem(this.tagAddition, "Data", param -> ((Person) value)
		    .getPersonData().filter(dd -> !DocumentDataType.LAYOUTINFO.toString().equals(dd.getType())), this,
		    addData);
	    categories.add(dataCategory);
	    this.getChildren().add(dataCategory);

	    MenuItem addTask = new MenuItem("Add new Task");
	    addTask.setOnAction(t -> ((Person) value).createTasks().withName("TaskName?").createTaskData()
		    .withTag(PredefinedDocumentDataConstants.NAME).withValue("TaskName?"));
	    CategoryTreeItem categoryTreeItem = new CategoryTreeItem(this.tagAddition, "ToDo",
		    t -> ((Person) value).getTasks().filter(d -> {
			if (d.getParentTasksTransitive().getTaskData()
				.filterTag(PredefinedDocumentDataConstants.STORY_CONTAINER).size() == 0)
			    return true;
			else
			    return false;
		    }).filter(d -> {
			// Filter for already done
			String done = d.getTaskData().filterTag(PredefinedDocumentDataConstants.DONE).getValue()
				.first();
			if (done != null && Boolean.parseBoolean(done)) {
			    return false;
			} else {
			    return true;
			}
		    }), this, addTask);
	    categories.add(categoryTreeItem);
	    this.getChildren().add(categoryTreeItem);

	    // "DONE"
	    CategoryTreeItem doneTasksCategory = new CategoryTreeItem(this.tagAddition, "Done",
		    t -> ((Person) value).getTasks().filter(d -> {
			if (d.getParentTasksTransitive().getTaskData()
				.filterTag(PredefinedDocumentDataConstants.STORY_CONTAINER).size() == 0)
			    return true;
			else
			    return false;
		    }).filter(d -> {
			// Filter for already done
			String done = d.getTaskData().filterTag(PredefinedDocumentDataConstants.DONE).getValue()
				.first();
			if (done != null && Boolean.parseBoolean(done)) {
			    return true;
			} else {
			    return false;
			}
		    }), this);
	    categories.add(doneTasksCategory);
	    this.getChildren().add(doneTasksCategory);
	} else if (value instanceof DocumentData) {
	    // no children should be shown
	}

	if (value instanceof SendableEntity) {
	    value.addPropertyChangeListener(p -> {
		if (this.parentTreeitem != null) {
		    // try to update the parent
		    ((CategoryTreeItem) this.parentTreeitem).update();
		} else {
		    // if there's no parent, update the root...
		    update();
		}
	    });
	}

	// Add listener to Expanded State
	this.expandedProperty().addListener((t, oldVal, newVal) -> {
	    if (newVal == true) {
		update();
	    }
	    ;
	});

	this.setExpanded(TRUE.equals(getLayoutData().getValue()));
	this.expandedProperty().addListener((t, oldVal, newVal) -> {
	    getLayoutData().setValue(newVal.toString());
	});

    }

    public void update() {
	if (this.isExpanded()) {
	    // Only Update the Childrens, if this item is Expanded
	    for (CategoryTreeItem categoryTreeItem : categories) {
		categoryTreeItem.update();
	    }
	}
    }

    public DocumentData createDocData(DocumentData newDocData) {
	// TODO - get username
	newDocData.withTag("tag?").withValue("value?").withLastEditor("USERNAME")
		.withLastModified(dateFormat.format(new Date(System.currentTimeMillis())))
		.withType(DocumentDataType.STRING.toString());
	return newDocData;
    }

    private String layoutTagNameCache = null;

    public String getLayoutTagName() {
	if (layoutTagNameCache == null) {
	    TreeItem<Object> parent = this.parentTreeitem;
	    StringBuilder res = new StringBuilder();
	    res.append(this.tagAddition);
	    if (parent != null) {
		if (parent instanceof CategoryTreeItem) {
		    res.append(((CategoryTreeItem) parent).getLayoutTagName() + ".");
		} else {
		    // there shouldn't be a OwnTreeItem as Parent...
		}
	    }

	    res.append(DocumentDataCheckManApp.getIdMap().getId(this.getValue()));
	    layoutTagNameCache = res.toString();
	}
	return layoutTagNameCache;
    }

    private DocumentData layoutData = null;

    public DocumentData getLayoutData() {
	if (layoutData == null) {
	    DocumentData user = DocumentDataCheckManApp.getUser();
	    user.getOrCreateDocDataValue(getLayoutTagName(), DocumentDataType.BOOLEAN.toString(), FALSE);
	    layoutData = user.getSubData(getLayoutTagName());
	}
	return layoutData;
    }

    @Override
    public String toString() {
	return this.getValue().toString();
    }

    private Person getExistingPerson(Task taskWhereToAdd) {
	PersonSet existingPersons = DocumentDataCheckManApp.getRoot().getPersons().getMembersTransitive();
	ObservableList<Person> existingPersonList = FXCollections.observableArrayList(existingPersons);

	AutoCompletionDialogPerson dialog = new AutoCompletionDialogPerson(existingPersonList);

	Optional<Person> result = dialog.showAndWait();
	result.ifPresent(t -> {
	    if (t != null) {
		taskWhereToAdd.withPersons(t);
	    }
	});

	return null;
    }

    private Object getExistingPerson(Person personWheretoAdd) {
	PersonSet existingPersons = DocumentDataCheckManApp.getRoot().getPersons().getMembersTransitive();
	ObservableList<Person> existingPersonList = FXCollections.observableArrayList(existingPersons);

	AutoCompletionDialogPerson dialog = new AutoCompletionDialogPerson(existingPersonList);

	Optional<Person> result = dialog.showAndWait();
	result.ifPresent(t -> {
	    if (t != null) {
		personWheretoAdd.withMembers(t);
	    }
	});
	return null;
    }

    private Person getExistingTask(Task taskWhereToAdd) {
	TaskSet existingPersons = DocumentDataCheckManApp.getRoot().getSubTasksTransitive();
	ObservableList<Task> existingPersonList = FXCollections.observableArrayList(existingPersons);

	AutoCompletionDialogTask dialog = new AutoCompletionDialogTask(existingPersonList);

	Optional<Task> result = dialog.showAndWait();
	result.ifPresent(t -> {
	    if (t != null) {
		taskWhereToAdd.withSubTasks(t);
	    }
	});

	return null;
    }

}
