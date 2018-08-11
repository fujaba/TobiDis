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
   
package de.uks.dss.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.sdmlib.StrUtil;

import de.uks.dss.model.util.DocumentDataSet;
import de.uks.dss.model.util.PersonCreator;
import de.uks.dss.model.util.PersonSet;
import de.uks.dss.model.util.TaskSet;
import de.uks.dss.util.PropagatingPropertyChangeSupport;
import de.uniks.networkparser.interfaces.SendableEntity;
   /**
    * 
    * @see <a href='../../../../../src/de/uks/dss/modelcode/BasicModel.java'>BasicModel.java</a>
 */
   public  class Person implements SendableEntity
{

	private static PersonCreator creator = new PersonCreator();

	public static PersonCreator getCreator() {
		return creator;
	}
   
   //==========================================================================
   
	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	public PropertyChangeSupport getPropertyChangeSupport() {
		return listeners;
	}
   
   public boolean addPropertyChangeListener(PropertyChangeListener listener) 
   {
      getPropertyChangeSupport().addPropertyChangeListener(listener);
      return true;
   }
   
   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
      getPropertyChangeSupport().addPropertyChangeListener(propertyName, listener);
      return true;
   }
   
   public boolean removePropertyChangeListener(PropertyChangeListener listener) {
      getPropertyChangeSupport().removePropertyChangeListener(listener);
      return true;
   }

   @Override
   public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
	if(listeners != null){
	    getPropertyChangeSupport().removePropertyChangeListener(propertyName, listener);
	    return true;
	}
	return false;
   }

   
   //==========================================================================
   
   
   public void removeYou()
   {
   
      withoutGroups(this.getGroups().toArray(new Person[this.getGroups().size()]));
      withoutMembers(this.getMembers().toArray(new Person[this.getMembers().size()]));
      withoutTasks(this.getTasks().toArray(new Task[this.getTasks().size()]));
      withoutPersonData(this.getPersonData().toArray(new DocumentData[this.getPersonData().size()]));
      getPropertyChangeSupport().firePropertyChange("REMOVE_YOU", this, null);
   }

   
   //==========================================================================
   
   public static final String PROPERTY_NAME = "name";
   
   private String name;

   public String getName()
   {
      return this.name;
   }
   
   public void setName(String value)
   {
      if ( ! StrUtil.stringEquals(this.name, value)) {
      
         String oldValue = this.name;
         this.name = value;
         getPropertyChangeSupport().firePropertyChange(PROPERTY_NAME, oldValue, value);
      }
   }
   
   public Person withName(String value)
   {
      setName(value);
      return this;
   } 


   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();
      
      result.append(" ").append(this.getName());
      return result.substring(1);
   }


   
   /********************************************************************
    * <pre>
    *              many                       many
    * Person ----------------------------------- Person
    *              members                   groups
    * </pre>
    */
   
   public static final String PROPERTY_GROUPS = "groups";

   private PersonSet groups = null;
   
   public PersonSet getGroups()
   {
      if (this.groups == null)
      {
         return PersonSet.EMPTY_SET;
      }
   
      return this.groups;
   }
   public PersonSet getGroupsTransitive()
   {
      PersonSet result = new PersonSet().with(this);
      return result.getGroupsTransitive();
   }


   public Person withGroups(Person... value)
   {
      if(value==null){
         return this;
      }
      for (Person item : value)
      {
         if (item != null)
         {
            if (this.groups == null)
            {
               this.groups = new PersonSet();
            }
            
            boolean changed = this.groups.add (item);

            if (changed)
            {
               item.withMembers(this);
               getPropertyChangeSupport().firePropertyChange(PROPERTY_GROUPS, null, item);
            }
         }
      }
      return this;
   } 

   public Person withoutGroups(Person... value)
   {
      for (Person item : value)
      {
         if ((this.groups != null) && (item != null))
         {
            if (this.groups.remove(item))
            {
               item.withoutMembers(this);
               getPropertyChangeSupport().firePropertyChange(PROPERTY_GROUPS, item, null);
            }
         }
      }
      return this;
   }

   public Person createGroups()
   {
      Person value = new Person();
      withGroups(value);
      return value;
   } 

   
   /********************************************************************
    * <pre>
    *              many                       many
    * Person ----------------------------------- Person
    *              members                   members
    * </pre>
    */
   
   public static final String PROPERTY_MEMBERS = "members";

   private PersonSet members = null;
   
   public PersonSet getMembers()
   {
      if (this.members == null)
      {
         return PersonSet.EMPTY_SET;
      }
   
      return this.members;
   }
   public PersonSet getMembersTransitive()
   {
      PersonSet result = new PersonSet().with(this);
      return result.getMembersTransitive();
   }


   public Person withMembers(Person... value)
   {
      if(value==null){
         return this;
      }
      for (Person item : value)
      {
         if (item != null)
         {
            if (this.members == null)
            {
               this.members = new PersonSet();
            }
            
            boolean changed = this.members.add (item);

            if (changed)
            {
               item.withGroups(this);
               getPropertyChangeSupport().firePropertyChange(PROPERTY_MEMBERS, null, item);
            }
         }
      }
      return this;
   } 

   public Person withoutMembers(Person... value)
   {
      for (Person item : value)
      {
         if ((this.members != null) && (item != null))
         {
            if (this.members.remove(item))
            {
               item.withoutGroups(this);
               getPropertyChangeSupport().firePropertyChange(PROPERTY_MEMBERS, item, null);
            }
         }
      }
      return this;
   }

     /**
    * 
    * @see <a href='../../../../../src/de/uks/se/dss/test/TestStoryToTaskFlow.java'>TestStoryToTaskFlow.java</a>
 * @see <a href='../../../../../src/de/uks/se/dss/test/TestTaskFlowEngine.java'>TestTaskFlowEngine.java</a>
 * @see <a href='../../../../../src/de/uks/se/dss/test/TestCloneTaskFlow.java'>TestCloneTaskFlow.java</a>
 */
   public Person createMembers()
   {
      Person value = new Person();
      withMembers(value);
      return value;
   } 

   
   /********************************************************************
    * <pre>
    *              many                       many
    * Person ----------------------------------- Task
    *              persons                   tasks
    * </pre>
    */
   
   public static final String PROPERTY_TASKS = "tasks";

   private TaskSet tasks = null;
   
   public TaskSet getTasks()
   {
      if (this.tasks == null)
      {
         return TaskSet.EMPTY_SET;
      }
   
      return this.tasks;
   }

   public Person withTasks(Task... value)
   {
      if(value==null){
         return this;
      }
      for (Task item : value)
      {
         if (item != null)
         {
            if (this.tasks == null)
            {
               this.tasks = new TaskSet();
            }
            
            boolean changed = this.tasks.add (item);

            if (changed)
            {
               item.withPersons(this);
               getPropertyChangeSupport().firePropertyChange(PROPERTY_TASKS, null, item);
            }
         }
      }
      return this;
   } 

   public Person withoutTasks(Task... value)
   {
      for (Task item : value)
      {
         if ((this.tasks != null) && (item != null))
         {
            if (this.tasks.remove(item))
            {
               item.withoutPersons(this);
               getPropertyChangeSupport().firePropertyChange(PROPERTY_TASKS, item, null);
            }
         }
      }
      return this;
   }

   public Task createTasks()
   {
      Task value = new Task();
      withTasks(value);
      return value;
   } 

   
   /********************************************************************
    * <pre>
    *              many                       many
    * Person ----------------------------------- DocumentData
    *              persons                   personData
    * </pre>
    */
   
   public static final String PROPERTY_PERSONDATA = "personData";

   private DocumentDataSet personData = null;
   
   public DocumentDataSet getPersonData()
   {
      if (this.personData == null)
      {
         return DocumentDataSet.EMPTY_SET;
      }
   
      return this.personData;
   }
   
   public DocumentData getPersonData(String tag){
	   if (this.personData != null && this.personData.filterTag(tag) != null) {
			if (this.personData.filterTag(tag).size() > 1) {
				System.out.println("Duplicate DocData.." + this.personData.filterTag(tag));
			}
			return this.personData.filterTag(tag).first();
		}
	   return null;
   }

   public Person withPersonData(DocumentData... value)
   {
      if(value==null){
         return this;
      }
      for (DocumentData item : value)
      {
         if (item != null)
         {
            if (this.personData == null)
            {
               this.personData = new DocumentDataSet();
            }
            
            boolean changed = this.personData.add (item);

            if (changed)
            {
               item.withPersons(this);
               getPropertyChangeSupport().firePropertyChange(PROPERTY_PERSONDATA, null, item);
            }
         }
      }
      return this;
   } 

   public Person withoutPersonData(DocumentData... value)
   {
      for (DocumentData item : value)
      {
         if ((this.personData != null) && (item != null))
         {
            if (this.personData.remove(item))
            {
               item.withoutPersons(this);
               getPropertyChangeSupport().firePropertyChange(PROPERTY_PERSONDATA, item, null);
            }
         }
      }
      return this;
   }

   public DocumentData createPersonData()
   {
      DocumentData value = new DocumentData();
      withPersonData(value);
      return value;
   } 
}
