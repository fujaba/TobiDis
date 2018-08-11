package de.uks.dss.gui;

import static de.uks.dss.model.PredefinedDocumentDataConstants.NAME;
import static de.uks.dss.model.PredefinedDocumentDataConstants.dateFormat;
import static de.uks.dss.model.PredefinedDocumentDataConstants.dateFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;

import org.sdmlib.modelcouch.CouchDBAdapter;
import org.sdmlib.modelcouch.connection.ContentType;
import org.sdmlib.modelcouch.connection.ReturnObject;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;

import de.uks.dss.app.DocumentDataCheckManApp;
import de.uks.dss.gui.OwnTreeCell.EditType;
import de.uks.dss.model.DocumentData;
import de.uks.dss.model.DocumentDataType;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;

public class TreeCellContentHandlerDocumentData extends TreeCellContentHandler {

    @Override
    protected boolean impl_startEdit(OwnTreeCell cell, Object item) {
	if (item instanceof DocumentData) {
	    if (cell.editType != null && cell.editType.equals(EditType.TAG)) {
		cell.setText(null);
		cell.setGraphic(addAction(cell, new TextField(((DocumentData) item).getTag())));
	    } else {
		cell.setText(null);
		if (((DocumentData) item).getType() == null) {
		    // if type is not set, handle as STRING
		    cell.setGraphic(addAction(cell, new TextField(((DocumentData) item).getValue())));
		    return true;
		}

		FileChooser f = new FileChooser();
		switch (DocumentDataType.valueOf(((DocumentData) item).getType())) {
		case STRING:
		    cell.setGraphic(addAction(cell, new TextField(((DocumentData) item).getValue())));
		    break;
		case DATE:
		    LocalDate date;
		    try {
			date = LocalDate.parse(((DocumentData) item).getValue(), dateFormatter);
		    } catch (Exception e) {
			date = LocalDate.now();
		    }
		    cell.setGraphic(addAction(cell, new DatePicker(date)));
		    break;
		case FORM:
		    File selectedForm = f.showOpenDialog(null);
		    if (selectedForm != null) {
			((DocumentData) item).setValue("newForm");
			String string = selectedForm.getPath().toString();
			cell.setText(string);
			saveValue(cell);
		    } else {
			cancelEdit(cell);
		    }
		    break;
		case ICON:
		    File selectedIcon = f.showOpenDialog(null);
		    if (selectedIcon != null) {
			((DocumentData) item).setValue("newIcon");
			String string = selectedIcon.getPath().toString();
			cell.setText(string);
			saveValue(cell);
		    } else {
			cancelEdit(cell);
		    }
		    break;
		case BOOLEAN:
		    // boolean done = Boolean.parseBoolean(((DocumentData)
		    // item).getValue());
		    // CheckBox checkBox = new CheckBox(((DocumentData)
		    // item).getTag());
		    // checkBox.setSelected(done);
		    // cell.setGraphic(checkBox);

		    /*
		     * Currently at updateItem...
		     */
		    break;
		default:
		    cell.setGraphic(addAction(cell, new TextField(((DocumentData) item).getValue())));
		    break;
		}
	    }
	    return true;
	}
	return false;
    }

    @Override
    protected boolean impl_saveValue(OwnTreeCell cell, Object item) {
	if (item instanceof DocumentData) {
	    if (cell.editType != null && cell.editType.equals(EditType.TAG)) {
		((DocumentData) item).setTag(((TextInputControl) cell.getGraphic()).getText());
	    } else {
		if (cell.getGraphic() instanceof TextInputControl) {
		    ((DocumentData) item).setValue(((TextInputControl) cell.getGraphic()).getText());
		} else if (cell.getGraphic() instanceof DatePicker) {
		    ((DocumentData) item).setValue(((DatePicker) cell.getGraphic()).getValue().toString());
		} else if (cell.getGraphic() instanceof CheckBox) {
		    ((DocumentData) item).setValue(Boolean.toString(((CheckBox) cell.getGraphic()).isSelected()));
		} else if (DocumentDataType.ICON.toString().equals(((DocumentData) item).getType())
			|| DocumentDataType.FORM.toString().equals(((DocumentData) item).getType())) {
		    // upload attachment
		    CouchDBAdapter couchAdapter = DocumentDataCheckManApp.getCouchAdapter();

		    ReturnObject send = couchAdapter
			    .createEmptyDocument(DocumentDataCheckManApp.attachmentDatabaseName);

		    String url = cell.getText();

		    ContentType contentType = null;
		    if (DocumentDataType.ICON.toString().equals(((DocumentData) item).getType())) {
			contentType = ContentType.IMAGE;
		    } else {
			contentType = ContentType.TEXT_PLAIN;
		    }

		    ReturnObject addAttachment = couchAdapter.addAttachment(send, Paths.get(url), ContentType.IMAGE);

		    ((DocumentData) item).setValue(addAttachment.getHeaderFields().get("Location").get(0));
		}
		((DocumentData) item).withLastEditor(DocumentDataCheckManApp.getUsername())
			.withLastModified(dateFormat.format(new Date()));
	    }
	    return true;
	}
	return false;
    }

    @Override
    protected boolean impl_cancelEdit(OwnTreeCell cell) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    protected boolean impl_commitEdit(OwnTreeCell cell, Object newValue) {
	// TODO Auto-generated method stub
	return false;
    }

    private static SimpleDoubleProperty greatestTagLength = new SimpleDoubleProperty(0);
    private static FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();

    @Override
    protected boolean impl_updateItem(OwnTreeCell cell, Object item, boolean empty) {
	if (item instanceof DocumentData) {
	    cell.setText(null);

	    HBox hBox = new HBox(5);

	    String tag = ((DocumentData) item).getTag();
	    Label txtTag = new Label(tag);
	    // Compute the needed space of the text
	    float computeStringWidth = fontLoader.computeStringWidth(tag, txtTag.getFont());
	    if (computeStringWidth > greatestTagLength.get()) {
		greatestTagLength.set(computeStringWidth);
	    }
	    txtTag.prefWidthProperty().bind(greatestTagLength.add(hBox.spacingProperty()));
	    txtTag.minWidthProperty().bind(greatestTagLength.add(hBox.spacingProperty()));

	    if (DocumentDataType.BOOLEAN.toString().equals(((DocumentData) item).getType())) {
		boolean done = Boolean.parseBoolean(((DocumentData) item).getValue());
		CheckBox checkBox = new CheckBox(((DocumentData) item).getTag());

		checkBox.setOnAction(t -> {
		    ((DocumentData) item).setValue(Boolean.toString(checkBox.isSelected()));
		});

		checkBox.setSelected(done);
		hBox.getChildren().addAll(checkBox);
	    } else if (DocumentDataType.ICON.toString().equals(((DocumentData) item).getType())
		    && !"value?".equals(((DocumentData) item).getValue())) {
		try {
		    ImageView imgView = new ImageView(new Image(((DocumentData) item).getValue(),50,50,true,true));
		    hBox.getChildren().addAll(txtTag, imgView);
		} catch (Exception e) {
		    System.err.println("Invalid URL for Icon...");
		    hBox.getChildren().addAll(txtTag);
		}
	    } else if (DocumentDataType.FORM.toString().equals(((DocumentData) item).getType())) {
		Hyperlink link = new Hyperlink();
		link.setText(((DocumentData) item).getTag());
		link.setOnAction(t -> {
		    String value = ((DocumentData) item).getValue();
		    // first remove "http"

		    CouchDBAdapter couchAdapter = DocumentDataCheckManApp.getCouchAdapter();

		    byte[] attachment = couchAdapter.getAttachment(((DocumentData) item).getValue());

		    FileOutputStream fos;
		    String location = ((DocumentData) item).getValue();
		    String[] split = location.split("/");
		    String filename = split[split.length - 1];

		    try {
			Path path = Paths.get("./tmp");
			if (Files.notExists(path)) {
			    Files.createDirectories(path);
			}
			Path file = Paths.get("./tmp/" + filename);
			if (Files.notExists(file)) {
			    Files.createFile(file);
			}
			Path write = Files.write(file, attachment);

			DocumentDataCheckManApp.getDocumentDataCheckManApp().getHostServices()
				.showDocument(write.toAbsolutePath().toString());
		    } catch (Exception e) {
			// e.printStackTrace();
		    }
		});
		hBox.getChildren().addAll(link);
	    } else {
		String value = "";
		if (DocumentDataType.DATE.toString().equals(((DocumentData) item).getType())) {
		    // Here we can format the Date as desired...
		    value = ((DocumentData) item).getValue();
		} else {
		    value = ((DocumentData) item).getValue();
		}
		Label txtValue = new Label(value);
		txtValue.setTextAlignment(TextAlignment.RIGHT);
		hBox.getChildren().addAll(txtTag, txtValue);
	    }

	    cell.setGraphic(hBox);
	    cell.setEditable(true);

	    TreeCellContentHandler.createCtxMenu(cell, cell.getTreeItem());

	    Tooltip tooltip = new Tooltip();
	    Date date;
	    try {
		date = dateFormat.parse(((DocumentData) item).getLastModified());
	    } catch (Exception e) {
		date = new Date();
	    }
	    tooltip.setText("Last edited: " + date + ", by " + ((DocumentData) item).getLastEditor());
	    cell.setTooltip(tooltip);

	    return true;
	}
	return false;
    }

    @Override
    protected LinkedList<MenuItem> impl_createCtxMenu(OwnTreeCell cell, TreeItem item) {
	if (item instanceof OwnTreeItem && item.getValue() instanceof DocumentData) {
	    LinkedList<MenuItem> menuItems = new LinkedList(((OwnTreeItem) item).getMenuItems());
	    MenuItem editTag = new MenuItem("Edit Tag");
	    editTag.setOnAction(t -> {
		cell.editType = OwnTreeCell.EditType.TAG;
		cell.startEdit();
	    });
	    menuItems.add(editTag);

	    if (!DocumentDataType.STRING.toString().equals(((DocumentData) item.getValue()).getType())) {
		MenuItem switchType = new MenuItem("Switch to String");
		switchType.setOnAction(t -> {
		    ((DocumentData) item.getValue()).setType(DocumentDataType.STRING.toString());
		});
		menuItems.add(switchType);
	    }
	    if (!DocumentDataType.DATE.toString().equals(((DocumentData) item.getValue()).getType())) {
		MenuItem switchType = new MenuItem("Switch to Date");
		switchType.setOnAction(t -> {
		    ((DocumentData) item.getValue()).setType(DocumentDataType.DATE.toString());
		});
		menuItems.add(switchType);
	    }
	    if (!DocumentDataType.BOOLEAN.toString().equals(((DocumentData) item.getValue()).getType())) {
		MenuItem switchType = new MenuItem("Switch to Boolean");
		switchType.setOnAction(t -> {
		    ((DocumentData) item.getValue()).setType(DocumentDataType.BOOLEAN.toString());
		});
		menuItems.add(switchType);
	    }
	    if (!DocumentDataType.FORM.toString().equals(((DocumentData) item.getValue()).getType())) {
		MenuItem switchType = new MenuItem("Switch to form");
		switchType.setOnAction(t -> {
		    ((DocumentData) item.getValue()).setType(DocumentDataType.FORM.toString());
		});
		menuItems.add(switchType);
	    }
	    if (!DocumentDataType.ICON.toString().equals(((DocumentData) item.getValue()).getType())) {
		MenuItem switchType = new MenuItem("Switch to icon");
		switchType.setOnAction(t -> {
		    ((DocumentData) item.getValue()).setType(DocumentDataType.ICON.toString());
		});
		menuItems.add(switchType);
	    }
	    if (!DocumentDataType.LAYOUTINFO.toString().equals(((DocumentData) item.getValue()).getType())) {
		MenuItem switchType = new MenuItem("Switch to LayoutInfo");
		switchType.setOnAction(t -> {
		    ((DocumentData) item.getValue()).setType(DocumentDataType.LAYOUTINFO.toString());
		});
		menuItems.add(switchType);
	    }
	    if (!NAME.equals(((DocumentData) item.getValue()).getTag())) {
		MenuItem switchType = new MenuItem("Delete Data");
		switchType.setOnAction(t -> {
		    Alert alert = new Alert(AlertType.CONFIRMATION);
		    alert.setTitle("Delete data");
		    alert.setHeaderText("Are you shure?");
		    alert.setContentText("Please confirm");

		    Optional<ButtonType> shure = alert.showAndWait();

		    if (shure.get() == ButtonType.OK) {
			((DocumentData) item.getValue()).removeYou();
		    }
		});
		menuItems.add(switchType);
	    }
	    // MenuItem menuItem = new MenuItem("show type");
	    // menuItem.setOnAction(t -> {
	    // String type = ((DocumentData) item.getValue()).getType();
	    // System.out.println(type);
	    // });
	    // menuItems.add(menuItem);

	    return menuItems;
	}
	return null;
    }

}
