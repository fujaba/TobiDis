package de.uks.dss.gui.controller;

import java.beans.PropertyChangeEvent;
import java.util.Date;

import org.sdmlib.models.pattern.util.PatternObjectSet;

import de.uks.dss.gui.autocompletion.AutoCompletionComboBoxPatternObject;
import de.uks.dss.gui.autocompletion.AutoCompletionComboBoxPerson;
import de.uks.dss.gui.autocompletion.AutoCompletionComboBoxTask;
import de.uks.dss.model.DocumentData;
import de.uks.dss.model.DocumentDataType;
import de.uks.dss.model.Person;
import de.uks.dss.model.PredefinedDocumentDataConstants;
import de.uks.dss.model.Task;
import de.uks.dss.model.util.PersonPO;
import de.uks.dss.model.util.PersonSet;
import de.uks.dss.model.util.TaskSet;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class ActController {

	private static final String ESCAPE_CHAR = "";
	private static final String ENTER_CHAR = "\r";

	// model
	private Task act;

	// view
	private Pane counterTop;
	private VBox elementsBox;
	private Ellipse actEllipse;
	private TextField actTitleText;
	private HBox responsiblesBox;
	private HBox participantsBox;

	// parentController
	private SoftwareStoryController storyController;

	public void start() {
		showOnUIAndRegisterListeners();
	}

	private void showOnUIAndRegisterListeners() {
		Double xPos = Double.valueOf(act.getTaskData(DocumentDataType.LAYOUTINFO.toString() + ".xPos").getValue());
		Double yPos = Double.valueOf(act.getTaskData(DocumentDataType.LAYOUTINFO.toString() + ".yPos").getValue());

		act.addPropertyChangeListener(Task.PROPERTY_NAME, e1 -> handleTitleChange(e1.getNewValue()));
		act.getTaskData(DocumentDataType.LAYOUTINFO.toString() + ".xPos").addPropertyChangeListener(e2 -> handleXPosChange(e2.getNewValue()));
		act.getTaskData(DocumentDataType.LAYOUTINFO.toString() + ".yPos").addPropertyChangeListener(e3 -> handleYPosChange(e3.getNewValue()));
		act.getPersons().filterName(PredefinedDocumentDataConstants.RESPONSIBLES).first().addPropertyChangeListener(Person.PROPERTY_MEMBERS, e4 -> handlePersonChange(e4));
		act.getPersons().filterName(PredefinedDocumentDataConstants.PARTICIPANTS).first().addPropertyChangeListener(Person.PROPERTY_MEMBERS, e4 -> handlePersonChange(e4));
		

		actEllipse = new Ellipse(xPos, yPos, 100, 120);
		actEllipse.setFill(Color.WHITE);
		actEllipse.setStroke(Color.BLACK);
		actEllipse.setStrokeWidth(2f);

		actEllipse.setOnMouseReleased(e4 -> changeActPosition(e4));
		actEllipse.setOnMouseDragReleased(e5 -> changeActPosition(e5));
		actEllipse.setOnMouseDragged(e6 -> changeActPosition(e6));
		counterTop.getChildren().add(actEllipse);

		// put all elements into HBox: title, timestamp, location, participants
		String title = act.getName();
		if (title == null || title == "") {
			title = "act title?";
		}
		actTitleText = new TextField(title);
		actTitleText.setMaxWidth(150f);
		actTitleText.setMinWidth(150f);
		actTitleText.setMaxHeight(25f);
		actTitleText.setMinHeight(25f);
		actTitleText.setOnAction(e7 -> changeTitle(e7));

		String timestamp = act.getTaskData(DocumentDataType.DATE.toString()).getValue();
		if (timestamp == null || timestamp == "") {
			timestamp = PredefinedDocumentDataConstants.dateFormat.format(new Date(System.currentTimeMillis()));
		}
		TextField actTimestampText = new TextField(timestamp);
		actTimestampText.setMaxWidth(150f);
		actTimestampText.setMinWidth(150f);
		actTimestampText.setMaxHeight(25f);
		actTimestampText.setMinHeight(25f);

		String location = act.getTaskData(PredefinedDocumentDataConstants.LOCATION).getValue();
		if (location == null || location == "") {
			location = PredefinedDocumentDataConstants.dateFormat.format(new Date(System.currentTimeMillis()));
		}
		TextField actLocationText = new TextField(location);
		actLocationText.setMaxWidth(150f);
		actLocationText.setMinWidth(150f);
		actLocationText.setMaxHeight(25f);
		actLocationText.setMinHeight(25f);

		HBox personsBox = initPersonsBox();

		//TODO existing pattern objects
		/*PersonPO personPO = act.getParentTasksTransitive().getPersons().getGroupsTransitive().filterPersonPO();
		personPO.withName("Lee");
		personPO.getLHSPatternObjectName();
		PatternObjectSet poSet = new PatternObjectSet();
		poSet.add(personPO);
		AutoCompletionComboBoxPatternObject poComboBox = new AutoCompletionComboBoxPatternObject(
				FXCollections.observableArrayList(poSet));
		poComboBox.setPromptText("PatternObject");
		//poComboBox.setOnValueSet(act::withPersons);*/

		//sub story
		TaskSet existingStorySet = storyController.getStoryRoot().getSubTasks();
		AutoCompletionComboBoxTask subStoryComboBox = new AutoCompletionComboBoxTask(FXCollections.observableArrayList(existingStorySet));
		subStoryComboBox.setPromptText("Sub story");
		subStoryComboBox.setOnValueSet(act::withSubTasks);

		Button deleteButton = new Button("delete Act");
		deleteButton.setOnMouseClicked(e8 -> deleteAct(e8));

		elementsBox = new VBox(actTitleText, actTimestampText, actLocationText, personsBox, /*TODO poComboBox,*/ subStoryComboBox, deleteButton);
		elementsBox.layoutXProperty()
		.bind(actEllipse.centerXProperty().subtract(elementsBox.widthProperty().divide(2)));
		elementsBox.layoutYProperty()
		.bind(actEllipse.centerYProperty().subtract(elementsBox.heightProperty().divide(2)));
		actEllipse.radiusXProperty().bind(elementsBox.widthProperty().multiply(0.75));
		actEllipse.radiusYProperty().bind(elementsBox.heightProperty().multiply(0.75));
		counterTop.getChildren().add(elementsBox);
	}

	private HBox initPersonsBox() {
		// persons: responsibles and participants

		// responsibles
		SimpleSet<Person> responsibles = act.getPersons().filterName(PredefinedDocumentDataConstants.RESPONSIBLES).getMembers();

		SimpleList<VBox> responsiblesBoxes = new SimpleList<VBox>();
		for (Person tmpResponsible : responsibles) {
			String imageUrl = tmpResponsible.getPersonData().filterType(DocumentDataType.ICON.toString()).first().getValue();
			ImageView responsibleImageView = new ImageView(imageUrl);
			Label responsibleName = new Label(tmpResponsible.getName());
			VBox responsibleBox = new VBox(responsibleImageView, responsibleName);
			responsiblesBoxes.add(responsibleBox);
		}
		responsiblesBox = new HBox(responsiblesBoxes.toArray(new VBox[responsiblesBoxes.size()]));
		createAndAddLastItem(responsiblesBox, "more responsibles?", true);

		// participants
		SimpleSet<Person> participants = act.getPersons().filterName(PredefinedDocumentDataConstants.PARTICIPANTS).getMembers();
		SimpleList<VBox> participantsBoxes = new SimpleList<VBox>();
		for (Person tmpParticipant : participants) {
			Label participantName = new Label(tmpParticipant.getName());
			ImageView participantImageView = new ImageView();

			DocumentData tmpParticipantIconData = tmpParticipant.getPersonData().filterType(DocumentDataType.ICON.toString()).first();
			if(tmpParticipantIconData != null)
			{				
				String imageUrl = tmpParticipantIconData.getValue();
				participantImageView = new ImageView(imageUrl);
			}

			VBox participantBox = new VBox(participantImageView, participantName);
			participantsBoxes.add(participantBox);
		}
		participantsBox = new HBox(participantsBoxes.toArray(new VBox[participantsBoxes.size()]));
		createAndAddLastItem(participantsBox, "more participants?", false);

		HBox result = new HBox(responsiblesBox, participantsBox);
		result.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		return result;
	}

	private void handleTitleChange(Object newValue) {
		actTitleText.setText(newValue.toString());
	}

	private void handleXPosChange(Object newValue) {
		if(newValue != null)
		{			
			Double xPos = Double.valueOf(newValue.toString());
			actEllipse.setCenterX(xPos);

			storyController.updateConnections();
		}
	}

	private void handleYPosChange(Object newValue) {
		if(newValue != null)
		{	
			Double yPos = Double.valueOf(newValue.toString());
			actEllipse.setCenterY(yPos);

			storyController.updateConnections();
		}
	}

	private void handlePersonChange(PropertyChangeEvent e) {
		Person tmpPerson = (Person) e.getNewValue();
		if (e.getNewValue() != null && tmpPerson.getGroups() != null
				&& act.getPersons().filterName(PredefinedDocumentDataConstants.RESPONSIBLES).getMembers().contains(tmpPerson)) {
			// new responsible added
			// update responsiblesBox
			ImageView responsibleImageView = new ImageView();

			DocumentData tmpResponsibleIconData = tmpPerson.getPersonData().filterType(DocumentDataType.ICON.toString()).first();
			if(tmpResponsibleIconData != null)
			{				
				String imageUrl = tmpResponsibleIconData.getValue();
				responsibleImageView = new ImageView(imageUrl);
			}

			Label responsibleName = new Label(tmpPerson.getName());
			VBox personBox = new VBox(responsibleImageView, responsibleName);

			responsiblesBox.getChildren().add(personBox);
			personBox.requestFocus();
		}
		else if (e.getNewValue() != null && tmpPerson.getGroups() != null
				&& act.getPersons().filterName(PredefinedDocumentDataConstants.PARTICIPANTS).getMembers().contains(tmpPerson)) {
			// new participant added
			// update participantsBox
			ImageView participantImageView = new ImageView();

			DocumentData tmpParticipantIconData = tmpPerson.getPersonData().filterType(DocumentDataType.ICON.toString()).first();
			if(tmpParticipantIconData != null)
			{				
				String imageUrl = tmpParticipantIconData.getValue();
				participantImageView = new ImageView(imageUrl);
			}
			Label participantName = new Label(tmpPerson.getName());
			VBox personBox = new VBox(participantImageView, participantName);

			participantsBox.getChildren().add(personBox);
			personBox.requestFocus();
		}
	}

	private Label createAndAddLastItem(HBox personBox, String text, boolean client) {
		Label lastItem = new Label(text);
		lastItem.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		lastItem.setOnMouseClicked(e -> offerPersonSelection(personBox, lastItem, text, client));
		personBox.getChildren().add(lastItem);
		return lastItem;
	}

	private void offerPersonSelection(HBox personBox, Label lastItem, String text, boolean isResponsible) {
		Task root = storyController.getStoryRoot().getParentTasks().first();
		PersonSet persons = new PersonSet();
		// FIXME members transitive ??
		SimpleSet<Person> personSimpleSet = new SimpleSet<Person>();

		if(isResponsible)
		{
			//just members can be responsible
			personSimpleSet = root.getPersons().filterName(PredefinedDocumentDataConstants.MEMBERS).getMembers();
		}
		else
		{
			//anyone can be participant
			personSimpleSet = root.getPersons().getMembersTransitive().filter(value -> {
				if(value.getMembers().isEmpty())
					return true;
				else
					return false;
			});
		}

		persons.addAll(personSimpleSet);

		AutoCompletionComboBoxPerson personComboBox = new AutoCompletionComboBoxPerson(
				FXCollections.observableArrayList(persons));
		personComboBox.setPromptText(text);
		personComboBox.setOnValueSet(t -> {
			if(isResponsible)
			{
				act.getPersons().filterName(PredefinedDocumentDataConstants.RESPONSIBLES).withMembers(t);
			}
			else
			{
				act.getPersons().filterName(PredefinedDocumentDataConstants.PARTICIPANTS).withMembers(t);
			}
		});

		personComboBox.focusedProperty()
		.addListener((ov, o, n) -> removeComboBox(n, personBox, personComboBox, text, isResponsible));

		personBox.getChildren().remove(lastItem);
		personBox.getChildren().add(personComboBox);
		personComboBox.requestFocus();
	}

	/*private void autoComplete(ComboBox<String> comboBox, String o, String n) {
		if (n.equals(comboBox.getSelectionModel().getSelectedItem())) {
			return;
		}

		final ObservableList<String> items = FXCollections.observableArrayList(comboBox.getItems());

		comboBox.hide();
		final FilteredList<String> filtered = items.filtered(s -> s.toLowerCase().contains(n.toLowerCase()));
		if (filtered.isEmpty()) {
			comboBox.getItems().setAll(items);
		} else {
			comboBox.getItems().setAll(filtered);
			comboBox.show();
		}
	}

	private void addNewParticipantOrRemoveCombobox(HBox personBox, ComboBox<String> comboBox, KeyEvent e, String text,
			boolean isClient) {
		if (e.getCharacter().equals(ESCAPE_CHAR)) {
			return;
		}

		String selectedPersonName = comboBox.getEditor().getText();
		if (!e.getCharacter().equals(ENTER_CHAR) || !comboBox.getItems().contains(selectedPersonName)) {
			return;
		}

		Task root = storyController.getStoryRoot().getParentTasks().first();
		Person selectedPerson = root.getPersons().getMembersTransitive().filterName(selectedPersonName).first();
		act.withPersons(selectedPerson);
	}*/

	private void removeComboBox(boolean focusGained, HBox personBox, ComboBox<?> personComboBox, String text,
			boolean isClient) {
		if (!focusGained) {
			personBox.getChildren().remove(personComboBox);
			createAndAddLastItem(personBox, text, isClient);
		}
	}

	private void changeActPosition(MouseEvent event) {
		double xPos = event.getX();
		double yPos = event.getY();

		if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
			act.getTaskData(DocumentDataType.LAYOUTINFO.toString() + ".xPos").withValue(String.valueOf(xPos));
			act.getTaskData(DocumentDataType.LAYOUTINFO.toString() + ".yPos").withValue(String.valueOf(yPos));
		} else if (event.getEventType() == MouseDragEvent.MOUSE_DRAGGED) {
			actEllipse.setCenterX(xPos);
			actEllipse.setCenterY(yPos);

			// TODO update while dragging ??
			//storyController.updateConnections();
		}
		event.consume();
	}

	private void changeTitle(ActionEvent event) {
		act.withName(actTitleText.getText());
		event.consume();
	}

	private void deleteAct(MouseEvent e8) {
		Task successor = act.getSubTasks().first();
		act.getParentTasks().first().withSubTasks(successor);
		act.removeYou();
	}

	public void setAct(Task act) {
		this.act = act;
	}

	public ActController withAct(Task act) {
		setAct(act);
		return this;
	}

	public void setCounterTop(Pane counterTop) {
		this.counterTop = counterTop;
	}

	public ActController withCounterTop(Pane counterTop) {
		setCounterTop(counterTop);
		return this;
	}

	public void setStoryController(SoftwareStoryController storyController) {
		this.storyController = storyController;
	}

	public ActController withStoryController(SoftwareStoryController storyController) {
		setStoryController(storyController);
		return this;
	}
}
