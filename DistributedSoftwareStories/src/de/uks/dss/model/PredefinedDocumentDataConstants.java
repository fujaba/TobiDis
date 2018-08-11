package de.uks.dss.model;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class PredefinedDocumentDataConstants
{
   public static final String LIST_OF_PERSON = "list of person";
//   public static final String STRING = "String";
//   public static final String DATE = "Date";
//   public static final String FORM = "Form";
//   public static final String ICON = "Icon";
   
   public static final String NAME = "name";
//   public static final String LAYOUTINFO = "layoutInfo";
   
   public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
   public static final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
   public static final DateTimeFormatter dateFormatter  = DateTimeFormatter.ofPattern(dayFormat.toPattern());//new LocalDateTimeStringConverter(); //new SimpleDateFormat("yyyy-MM-dd z");
//   public static final String BOOLEAN = "boolean";
   public static final String TRUE = "true";
   public static final String FALSE = "false";
   
   public static final String STORY_CONTAINER = "storyContainer";
   public static final String SUBSTORY = "subStory";
   public static final String ACTIVE = "active";
   public static final String DONE = "done";
   //TODO ?? public static final String DURATION = "duration";
   public static final String LOCATION = "location";
   
   public static final String MEMBERS = "Members";
   public static final String PARTICIPANTS = "participants";
   public static final String RESPONSIBLES = "responsibles";
   
   public static final String STUDENTS = "students";
   public static final String CLIENTS = "clients";
   
   public static final String PATTERNOBJECT = "patternobject";
   
//   public static final String EMAIL = "E-Mail";
   public static final String NEXTWORKFLOWTASK = "nextWorkflowTask";
}