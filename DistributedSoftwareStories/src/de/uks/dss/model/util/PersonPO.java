package de.uks.dss.model.util;

import org.sdmlib.models.pattern.PatternObject;
import de.uks.dss.model.Person;
import org.sdmlib.models.pattern.AttributeConstraint;
import de.uks.dss.model.util.PersonPO;
import de.uks.dss.model.util.PersonSet;
import de.uks.dss.model.util.TaskPO;
import de.uks.dss.model.Task;
import de.uks.dss.model.util.TaskSet;
import de.uks.dss.model.util.DocumentDataPO;
import de.uks.dss.model.DocumentData;
import de.uks.dss.model.util.DocumentDataSet;

public class PersonPO extends PatternObject<PersonPO, Person>
{

    public PersonSet allMatches()
   {
      this.setDoAllMatches(true);
      
      PersonSet matches = new PersonSet();

      while (this.getPattern().getHasMatch())
      {
         matches.add((Person) this.getCurrentMatch());
         
         this.getPattern().findMatch();
      }
      
      return matches;
   }


   public PersonPO(){
      newInstance(null);
   }

   public PersonPO(Person... hostGraphObject) {
      if(hostGraphObject==null || hostGraphObject.length<1){
         return ;
      }
      newInstance(null, hostGraphObject);
   }
   public PersonPO filterName(String value)
   {
      new AttributeConstraint()
      .withAttrName(Person.PROPERTY_NAME)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public PersonPO filterName(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(Person.PROPERTY_NAME)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public PersonPO createName(String value)
   {
      this.startCreate().filterName(value).endCreate();
      return this;
   }
   
   public String getName()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((Person) getCurrentMatch()).getName();
      }
      return null;
   }
   
   public PersonPO withName(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((Person) getCurrentMatch()).setName(value);
      }
      return this;
   }
   
   public PersonPO filterGroups()
   {
      PersonPO result = new PersonPO(new Person[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(Person.PROPERTY_GROUPS, result);
      
      return result;
   }

   public PersonPO createGroups()
   {
      return this.startCreate().filterGroups().endCreate();
   }

   public PersonPO filterGroups(PersonPO tgt)
   {
      return hasLinkConstraint(tgt, Person.PROPERTY_GROUPS);
   }

   public PersonPO createGroups(PersonPO tgt)
   {
      return this.startCreate().filterGroups(tgt).endCreate();
   }

   public PersonSet getGroups()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((Person) this.getCurrentMatch()).getGroups();
      }
      return null;
   }

   public PersonPO filterMembers()
   {
      PersonPO result = new PersonPO(new Person[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(Person.PROPERTY_MEMBERS, result);
      
      return result;
   }

   public PersonPO createMembers()
   {
      return this.startCreate().filterMembers().endCreate();
   }

   public PersonPO filterMembers(PersonPO tgt)
   {
      return hasLinkConstraint(tgt, Person.PROPERTY_MEMBERS);
   }

   public PersonPO createMembers(PersonPO tgt)
   {
      return this.startCreate().filterMembers(tgt).endCreate();
   }

   public PersonSet getMembers()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((Person) this.getCurrentMatch()).getMembers();
      }
      return null;
   }

   public TaskPO filterTasks()
   {
      TaskPO result = new TaskPO(new Task[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(Person.PROPERTY_TASKS, result);
      
      return result;
   }

   public TaskPO createTasks()
   {
      return this.startCreate().filterTasks().endCreate();
   }

   public PersonPO filterTasks(TaskPO tgt)
   {
      return hasLinkConstraint(tgt, Person.PROPERTY_TASKS);
   }

   public PersonPO createTasks(TaskPO tgt)
   {
      return this.startCreate().filterTasks(tgt).endCreate();
   }

   public TaskSet getTasks()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((Person) this.getCurrentMatch()).getTasks();
      }
      return null;
   }

   public DocumentDataPO filterPersonData()
   {
      DocumentDataPO result = new DocumentDataPO(new DocumentData[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(Person.PROPERTY_PERSONDATA, result);
      
      return result;
   }

   public DocumentDataPO createPersonData()
   {
      return this.startCreate().filterPersonData().endCreate();
   }

   public PersonPO filterPersonData(DocumentDataPO tgt)
   {
      return hasLinkConstraint(tgt, Person.PROPERTY_PERSONDATA);
   }

   public PersonPO createPersonData(DocumentDataPO tgt)
   {
      return this.startCreate().filterPersonData(tgt).endCreate();
   }

   public DocumentDataSet getPersonData()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((Person) this.getCurrentMatch()).getPersonData();
      }
      return null;
   }

}
