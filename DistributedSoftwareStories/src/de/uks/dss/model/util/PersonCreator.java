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
import de.uks.dss.model.Person;
import de.uks.dss.model.Task;
import de.uks.dss.model.DocumentData;

public class PersonCreator implements SendableEntityCreator
{
   private final String[] properties = new String[]
   {
      Person.PROPERTY_NAME,
      Person.PROPERTY_GROUPS,
      Person.PROPERTY_MEMBERS,
      Person.PROPERTY_TASKS,
      Person.PROPERTY_PERSONDATA,
   };
   
   @Override
   public String[] getProperties()
   {
      return properties;
   }
   
   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new Person();
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

      if (Person.PROPERTY_NAME.equalsIgnoreCase(attribute))
      {
         return ((Person) target).getName();
      }

      if (Person.PROPERTY_GROUPS.equalsIgnoreCase(attribute))
      {
         return ((Person) target).getGroups();
      }

      if (Person.PROPERTY_MEMBERS.equalsIgnoreCase(attribute))
      {
         return ((Person) target).getMembers();
      }

      if (Person.PROPERTY_TASKS.equalsIgnoreCase(attribute))
      {
         return ((Person) target).getTasks();
      }

      if (Person.PROPERTY_PERSONDATA.equalsIgnoreCase(attribute))
      {
         return ((Person) target).getPersonData();
      }
      
      return null;
   }
   
   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (Person.PROPERTY_NAME.equalsIgnoreCase(attrName))
      {
         ((Person) target).withName((String) value);
         return true;
      }

      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attrName = attrName + type;
      }

      if (Person.PROPERTY_GROUPS.equalsIgnoreCase(attrName))
      {
         ((Person) target).withGroups((Person) value);
         return true;
      }
      
      if ((Person.PROPERTY_GROUPS + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Person) target).withoutGroups((Person) value);
         return true;
      }

      if (Person.PROPERTY_MEMBERS.equalsIgnoreCase(attrName))
      {
         ((Person) target).withMembers((Person) value);
         return true;
      }
      
      if ((Person.PROPERTY_MEMBERS + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Person) target).withoutMembers((Person) value);
         return true;
      }

      if (Person.PROPERTY_TASKS.equalsIgnoreCase(attrName))
      {
         ((Person) target).withTasks((Task) value);
         return true;
      }
      
      if ((Person.PROPERTY_TASKS + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Person) target).withoutTasks((Task) value);
         return true;
      }

      if (Person.PROPERTY_PERSONDATA.equalsIgnoreCase(attrName))
      {
         ((Person) target).withPersonData((DocumentData) value);
         return true;
      }
      
      if ((Person.PROPERTY_PERSONDATA + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Person) target).withoutPersonData((DocumentData) value);
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
      ((Person) entity).removeYou();
   }
}
