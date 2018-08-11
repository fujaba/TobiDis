package de.uks.se.checkman.app;


import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import de.uks.dss.model.DocumentData;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DocumentDataController
{

   private VBox parentVBox;
   private DocumentData docData;
   private VBox myVBox;
   private HBox tagValueHBox;
   private TextField tagTextField;
   private TextField valueTextField;
   private ArrayList<DocumentDataController> subControllers;
   private VBox mySubDataVBox;
   private Button addDataFieldButton;
   private Button addSubDocumentButton;
   private String userName;

   public DocumentDataController(VBox parentVBox, DocumentData docData, String userName)
   {
      this.parentVBox = parentVBox;
      this.docData = docData;
      this.userName = userName;
   }
   
   public DocumentDataController start()
   {
      // create a vbox for this documet data object
      myVBox = new VBox(6);
      
      parentVBox.getChildren().add(myVBox);
      
      // if I have tag value, add hbox with textFields
      if (docData.getTag() != null)
      {
         tagValueHBox = new HBox(8);
         
         myVBox.getChildren().add(tagValueHBox);
         
         tagTextField = new TextField(docData.getTag());
         tagTextField.setAlignment(Pos.CENTER_RIGHT);
         tagTextField.setTooltip(new Tooltip("Last modified by: " + userName + " " + docData.getLastModified()));
         new TextValueController(tagTextField, docData, DocumentData.PROPERTY_TAG, userName);
         
         valueTextField = new TextField(docData.getValue());
         valueTextField.setTooltip(new Tooltip("Last modified by: " + userName + " " + docData.getLastModified()));
         new TextValueController(valueTextField, docData, DocumentData.PROPERTY_VALUE, userName);
         
         tagValueHBox.getChildren().addAll(tagTextField, valueTextField);
      }
      
      // add subData 
      mySubDataVBox = new VBox(6);
      myVBox.getChildren().add(mySubDataVBox);
      
      docData.addPropertyChangeListener(DocumentData.PROPERTY_SUBDATA, (e) -> subDataChange(e));
      
      subControllers = new ArrayList<DocumentDataController>();
      
      for (DocumentData subData : docData.getSubData())
      {
         DocumentDataController subController = new DocumentDataController(mySubDataVBox, subData, userName).start();
         subControllers.add(subController);
      }
      
      if (docData.getTag() == null)
      {
         HBox buttonBox = new HBox(8);
         myVBox.getChildren().add(buttonBox);
         addDataFieldButton = new Button("Add Data Field");
         addDataFieldButton.setOnAction((e)-> addDataFieldAction());

         addSubDocumentButton = new Button("Add Sub Document");
         addSubDocumentButton.setOnAction((e)-> addSubDocumentAction());

         buttonBox.getChildren().addAll(addDataFieldButton, addSubDocumentButton);
      }
      
      return this;
   }

   private void addDataFieldAction()
   {
      DocumentData subData = new DocumentData();
      
      subData.setTag("tag?");
      subData.setValue("value?");
      
      docData.withSubData(subData);
   }

   private void subDataChange(PropertyChangeEvent e)
   {
      if (e.getNewValue() != null)
      {
         DocumentDataController subController = new DocumentDataController(mySubDataVBox, (DocumentData) e.getNewValue(), userName).start();
         subControllers.add(subController);
      }
   }

   private void addSubDocumentAction()
   {  
      DocumentData subData = docData.createSubData();
   }

}
