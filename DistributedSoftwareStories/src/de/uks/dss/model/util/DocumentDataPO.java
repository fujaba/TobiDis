package de.uks.dss.model.util;

import org.sdmlib.models.pattern.PatternObject;
import de.uks.dss.model.DocumentData;
import org.sdmlib.models.pattern.AttributeConstraint;
import de.uks.dss.model.util.DocumentDataPO;
import de.uks.dss.model.util.DocumentDataSet;
import de.uks.dss.model.util.TaskPO;
import de.uks.dss.model.Task;
import de.uks.dss.model.util.TaskSet;
import de.uks.dss.model.util.PersonPO;
import de.uks.dss.model.Person;
import de.uks.dss.model.util.PersonSet;

public class DocumentDataPO extends PatternObject<DocumentDataPO, DocumentData>
{

    public DocumentDataSet allMatches()
   {
      this.setDoAllMatches(true);
      
      DocumentDataSet matches = new DocumentDataSet();

      while (this.getPattern().getHasMatch())
      {
         matches.add((DocumentData) this.getCurrentMatch());
         
         this.getPattern().findMatch();
      }
      
      return matches;
   }


   public DocumentDataPO(){
      newInstance(null);
   }

   public DocumentDataPO(DocumentData... hostGraphObject) {
      if(hostGraphObject==null || hostGraphObject.length<1){
         return ;
      }
      newInstance(null, hostGraphObject);
   }
   public DocumentDataPO filterTag(String value)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_TAG)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public DocumentDataPO filterTag(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_TAG)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public DocumentDataPO createTag(String value)
   {
      this.startCreate().filterTag(value).endCreate();
      return this;
   }
   
   public String getTag()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((DocumentData) getCurrentMatch()).getTag();
      }
      return null;
   }
   
   public DocumentDataPO withTag(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((DocumentData) getCurrentMatch()).setTag(value);
      }
      return this;
   }
   
   public DocumentDataPO filterValue(String value)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_VALUE)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public DocumentDataPO filterValue(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_VALUE)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public DocumentDataPO createValue(String value)
   {
      this.startCreate().filterValue(value).endCreate();
      return this;
   }
   
   public String getValue()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((DocumentData) getCurrentMatch()).getValue();
      }
      return null;
   }
   
   public DocumentDataPO withValue(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((DocumentData) getCurrentMatch()).setValue(value);
      }
      return this;
   }
   
   public DocumentDataPO filterType(String value)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_TYPE)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public DocumentDataPO filterType(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_TYPE)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public DocumentDataPO createType(String value)
   {
      this.startCreate().filterType(value).endCreate();
      return this;
   }
   
   public String getType()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((DocumentData) getCurrentMatch()).getType();
      }
      return null;
   }
   
   public DocumentDataPO withType(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((DocumentData) getCurrentMatch()).setType(value);
      }
      return this;
   }
   
   public DocumentDataPO filterLastEditor(String value)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_LASTEDITOR)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public DocumentDataPO filterLastEditor(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_LASTEDITOR)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public DocumentDataPO createLastEditor(String value)
   {
      this.startCreate().filterLastEditor(value).endCreate();
      return this;
   }
   
   public String getLastEditor()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((DocumentData) getCurrentMatch()).getLastEditor();
      }
      return null;
   }
   
   public DocumentDataPO withLastEditor(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((DocumentData) getCurrentMatch()).setLastEditor(value);
      }
      return this;
   }
   
   public DocumentDataPO filterLastModified(String value)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_LASTMODIFIED)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public DocumentDataPO filterLastModified(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_LASTMODIFIED)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public DocumentDataPO createLastModified(String value)
   {
      this.startCreate().filterLastModified(value).endCreate();
      return this;
   }
   
   public String getLastModified()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((DocumentData) getCurrentMatch()).getLastModified();
      }
      return null;
   }
   
   public DocumentDataPO withLastModified(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((DocumentData) getCurrentMatch()).setLastModified(value);
      }
      return this;
   }
   
   public DocumentDataPO filterParentData()
   {
      DocumentDataPO result = new DocumentDataPO(new DocumentData[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(DocumentData.PROPERTY_PARENTDATA, result);
      
      return result;
   }

   public DocumentDataPO createParentData()
   {
      return this.startCreate().filterParentData().endCreate();
   }

   public DocumentDataPO filterParentData(DocumentDataPO tgt)
   {
      return hasLinkConstraint(tgt, DocumentData.PROPERTY_PARENTDATA);
   }

   public DocumentDataPO createParentData(DocumentDataPO tgt)
   {
      return this.startCreate().filterParentData(tgt).endCreate();
   }

   public DocumentDataSet getParentData()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((DocumentData) this.getCurrentMatch()).getParentData();
      }
      return null;
   }

   public DocumentDataPO filterSubData()
   {
      DocumentDataPO result = new DocumentDataPO(new DocumentData[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(DocumentData.PROPERTY_SUBDATA, result);
      
      return result;
   }

   public DocumentDataPO createSubData()
   {
      return this.startCreate().filterSubData().endCreate();
   }

   public DocumentDataPO filterSubData(DocumentDataPO tgt)
   {
      return hasLinkConstraint(tgt, DocumentData.PROPERTY_SUBDATA);
   }

   public DocumentDataPO createSubData(DocumentDataPO tgt)
   {
      return this.startCreate().filterSubData(tgt).endCreate();
   }

   public DocumentDataSet getSubData()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((DocumentData) this.getCurrentMatch()).getSubData();
      }
      return null;
   }

   public TaskPO filterTasks()
   {
      TaskPO result = new TaskPO(new Task[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(DocumentData.PROPERTY_TASKS, result);
      
      return result;
   }

   public TaskPO createTasks()
   {
      return this.startCreate().filterTasks().endCreate();
   }

   public DocumentDataPO filterTasks(TaskPO tgt)
   {
      return hasLinkConstraint(tgt, DocumentData.PROPERTY_TASKS);
   }

   public DocumentDataPO createTasks(TaskPO tgt)
   {
      return this.startCreate().filterTasks(tgt).endCreate();
   }

   public TaskSet getTasks()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((DocumentData) this.getCurrentMatch()).getTasks();
      }
      return null;
   }

   public PersonPO filterPersons()
   {
      PersonPO result = new PersonPO(new Person[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(DocumentData.PROPERTY_PERSONS, result);
      
      return result;
   }

   public PersonPO createPersons()
   {
      return this.startCreate().filterPersons().endCreate();
   }

   public DocumentDataPO filterPersons(PersonPO tgt)
   {
      return hasLinkConstraint(tgt, DocumentData.PROPERTY_PERSONS);
   }

   public DocumentDataPO createPersons(PersonPO tgt)
   {
      return this.startCreate().filterPersons(tgt).endCreate();
   }

   public PersonSet getPersons()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((DocumentData) this.getCurrentMatch()).getPersons();
      }
      return null;
   }

}
