package de.uks.se.checkman.app;

import static de.uks.dss.model.PredefinedDocumentDataConstants.dateFormat;

import java.util.Date;

import de.uks.dss.model.DocumentData;
import de.uks.dss.model.util.DocumentDataCreator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class TextValueController
{

   private TextField textField;
   private DocumentData docData;
   private String propertyName;

   private static DocumentDataCreator dataCreator = new DocumentDataCreator();
   private String userName;
   
   public TextValueController(TextField textField, DocumentData docData, String propertyName, String userName)
   {
      this.textField = textField;
      this.docData = docData;
      this.propertyName = propertyName;
      this.userName = userName;
      
      // subscribe at textField
      textField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> textFieldChange());

      docData.addPropertyChangeListener(propertyName, (e) -> objectAttributeChange());
   }

   private void objectAttributeChange()
   {
      String text = (String) dataCreator.getValue(docData, propertyName);
      
      if (text != null)
      {
         this.textField.setText(text);
         
         docData.setLastEditor(userName);
         docData.setLastModified(dateFormat.format(new Date(System.currentTimeMillis())));
         
         Tooltip toolTip = new Tooltip("Last modified by: " + userName + " " + docData.getLastModified());
         textField.setTooltip(toolTip);
      }
   }

   private void textFieldChange()
   {
      String text = this.textField.getText();
      
      dataCreator.setValue(docData, propertyName, text, null);
   }

}
