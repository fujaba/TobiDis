package de.uks.se.checkman.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.text.DateFormatter;

import org.sdmlib.modelspace.ModelSpace;
import org.sdmlib.modelspace.ModelSpace.ApplicationType;

import de.uks.dss.model.DocumentData;
import de.uks.dss.model.PredefinedDocumentDataConstants;

import static de.uks.dss.model.PredefinedDocumentDataConstants.*;
import de.uks.dss.model.util.DocumentDataCreator;
import de.uniks.networkparser.json.JsonIdMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DocumentDataCheckManApp extends Application
{
   
   public static void main(String[] args)
   {
      launch(args);
   }

   private Stage stage;
   private String userName;
   private String sessionId;
   private JsonIdMap idMap;
   private ModelSpace modelSpace;
   private DocumentDataController rootController;

   @Override
   public void start(Stage stage) throws Exception
   {
      // first load the current data model
      this.stage = stage;
      
      //init model space
      List<String> parameters = this.getParameters().getRaw();
      userName = parameters.get(0);

      sessionId = userName + System.currentTimeMillis();

      idMap = DocumentDataCreator.createIdMap(sessionId);
      
      DocumentData dssRoot = new DocumentData();
      
      idMap.put("root", dssRoot);
      
      modelSpace = new ModelSpace(idMap, userName, ApplicationType.JavaFX).open("documentData/segroup");
      
      // find login in list of persons or add if missing.
      String type = dssRoot.getType();
      
      if (! LIST_OF_PERSON.equals(type))
      {
         // ups
         dssRoot.withType(LIST_OF_PERSON)
         .withLastEditor(userName)
         .withLastModified(dateFormat.format(new Date(System.currentTimeMillis())));

         DocumentData nameProperty = dssRoot.createSubData()
               .withTag("name")
               .withValue("group name?")
               .withLastEditor(userName)
               .withLastModified(dateFormat.format(new Date(System.currentTimeMillis())))
               .withType(STRING);
      }
      

      VBox rootVBox = new VBox(24);
      rootVBox.setPadding(new Insets(24));

      
      rootController = new DocumentDataController(rootVBox, dssRoot, userName)
            .start();
      
      
      // show root editor
      Scene scene = new Scene(rootVBox, 600, 800);
      stage.setScene(scene);
      
      stage.setOnCloseRequest(e -> System.exit(0));
      
      stage.show();
   }

}
