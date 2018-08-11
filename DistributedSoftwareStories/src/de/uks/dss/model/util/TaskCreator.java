/*
   Copyright (c) 2016 alexw
   
   Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
   and associated documentation files (the "Software"), to deal in the Software without restriction, 
   including without limitation the rights to use, copy, modify, merge, publish, distribute, 
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
   furnished to do so, subject to the following conditions: 
   
   The above copyright notice and this permission notice shall be included in all copies or 
   substantial portions of the Software. 
   
   The Software shall be used for Good, not Evil. 
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
   
package de.uks.dss.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.IdMap;
import de.uks.dss.model.Task;
import de.uks.dss.model.Person;
import de.uks.dss.model.DocumentData;

public class TaskCreator implements SendableEntityCreator
{
   private final String[] properties = new String[]
   {
      Task.PROPERTY_NAME,
      Task.PROPERTY_PARENTTASKS,
      Task.PROPERTY_SUBTASKS,
      Task.PROPERTY_PERSONS,
      Task.PROPERTY_TASKDATA,
   };
   
   @Override
   public String[] getProperties()
   {
      return properties;
   }
   
   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new Task();
   }
   
   @Override
   public Object getValue(Object target, String attrName)
   {
      int pos = attrName.indexOf('.');
      String attribute = attrName;
      
      if (pos > 0)
      {
         attribute = attrName.substring(0, pos);
      }

      if (Task.PROPERTY_NAME.equalsIgnoreCase(attribute))
      {
         return ((Task) target).getName();
      }

      if (Task.PROPERTY_PARENTTASKS.equalsIgnoreCase(attribute))
      {
         return ((Task) target).getParentTasks();
      }

      if (Task.PROPERTY_SUBTASKS.equalsIgnoreCase(attribute))
      {
         return ((Task) target).getSubTasks();
      }

      if (Task.PROPERTY_PERSONS.equalsIgnoreCase(attribute))
      {
         return ((Task) target).getPersons();
      }

      if (Task.PROPERTY_TASKDATA.equalsIgnoreCase(attribute))
      {
         return ((Task) target).getTaskData();
      }
      
      return null;
   }
   
   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (Task.PROPERTY_NAME.equalsIgnoreCase(attrName))
      {
         ((Task) target).withName((String) value);
         return true;
      }

      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attrName = attrName + type;
      }

      if (Task.PROPERTY_PARENTTASKS.equalsIgnoreCase(attrName))
      {
         ((Task) target).withParentTasks((Task) value);
         return true;
      }
      
      if ((Task.PROPERTY_PARENTTASKS + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Task) target).withoutParentTasks((Task) value);
         return true;
      }

      if (Task.PROPERTY_SUBTASKS.equalsIgnoreCase(attrName))
      {
         ((Task) target).withSubTasks((Task) value);
         return true;
      }
      
      if ((Task.PROPERTY_SUBTASKS + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Task) target).withoutSubTasks((Task) value);
         return true;
      }

      if (Task.PROPERTY_PERSONS.equalsIgnoreCase(attrName))
      {
         ((Task) target).withPersons((Person) value);
         return true;
      }
      
      if ((Task.PROPERTY_PERSONS + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Task) target).withoutPersons((Person) value);
         return true;
      }

      if (Task.PROPERTY_TASKDATA.equalsIgnoreCase(attrName))
      {
         ((Task) target).withTaskData((DocumentData) value);
         return true;
      }
      
      if ((Task.PROPERTY_TASKDATA + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Task) target).withoutTaskData((DocumentData) value);
         return true;
      }
      
      return false;
   }
   public static IdMap createIdMap(String sessionID)
   {
      return de.uks.dss.model.util.CreatorCreator.createIdMap(sessionID);
   }
   
   //==========================================================================
      public void removeObject(Object entity)
   {
      ((Task) entity).removeYou();
   }
}
