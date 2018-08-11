package de.uks.dss.model.util;

import org.sdmlib.models.pattern.PatternObject;
import de.uks.dss.model.DocumentData;
import org.sdmlib.models.pattern.AttributeConstraint;
import de.uks.dss.model.util.DocumentDataPO;
import de.uks.dss.model.util.DocumentDataSet;

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
      newInstance(de.uks.dss.model.util.CreatorCreator.createIdMap("PatternObjectType"));
   }

   public DocumentDataPO(DocumentData... hostGraphObject) {
      if(hostGraphObject==null || hostGraphObject.length<1){
         return ;
      }
      newInstance(de.uks.dss.model.util.CreatorCreator.createIdMap("PatternObjectType"), hostGraphObject);
   }
   public DocumentDataPO hasTag(String value)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_TAG)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.hasAttr();
      
      return this;
   }
   
   public DocumentDataPO hasTag(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_TAG)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.hasAttr();
      
      return this;
   }
   
   public DocumentDataPO createTag(String value)
   {
      this.startCreate().hasTag(value).endCreate();
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
   
   public DocumentDataPO hasValue(String value)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_VALUE)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.hasAttr();
      
      return this;
   }
   
   public DocumentDataPO hasValue(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_VALUE)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.hasAttr();
      
      return this;
   }
   
   public DocumentDataPO createValue(String value)
   {
      this.startCreate().hasValue(value).endCreate();
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
   
   public DocumentDataPO hasType(String value)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_TYPE)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.hasAttr();
      
      return this;
   }
   
   public DocumentDataPO hasType(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_TYPE)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.hasAttr();
      
      return this;
   }
   
   public DocumentDataPO createType(String value)
   {
      this.startCreate().hasType(value).endCreate();
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
   
   public DocumentDataPO hasLastEditor(String value)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_LASTEDITOR)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.hasAttr();
      
      return this;
   }
   
   public DocumentDataPO hasLastEditor(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_LASTEDITOR)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.hasAttr();
      
      return this;
   }
   
   public DocumentDataPO createLastEditor(String value)
   {
      this.startCreate().hasLastEditor(value).endCreate();
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
   
   public DocumentDataPO hasLastModified(String value)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_LASTMODIFIED)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.hasAttr();
      
      return this;
   }
   
   public DocumentDataPO hasLastModified(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(DocumentData.PROPERTY_LASTMODIFIED)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.hasAttr();
      
      return this;
   }
   
   public DocumentDataPO createLastModified(String value)
   {
      this.startCreate().hasLastModified(value).endCreate();
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
   
   public DocumentDataPO hasSubData()
   {
      DocumentDataPO result = new DocumentDataPO(new DocumentData[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(DocumentData.PROPERTY_SUBDATA, result);
      
      return result;
   }

   public DocumentDataPO createSubData()
   {
      return this.startCreate().hasSubData().endCreate();
   }

   public DocumentDataPO hasSubData(DocumentDataPO tgt)
   {
      return hasLinkConstraint(tgt, DocumentData.PROPERTY_SUBDATA);
   }

   public DocumentDataPO createSubData(DocumentDataPO tgt)
   {
      return this.startCreate().hasSubData(tgt).endCreate();
   }

   public DocumentDataSet getSubData()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((DocumentData) this.getCurrentMatch()).getSubData();
      }
      return null;
   }

   public DocumentDataPO hasParentData()
   {
      DocumentDataPO result = new DocumentDataPO(new DocumentData[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(DocumentData.PROPERTY_PARENTDATA, result);
      
      return result;
   }

   public DocumentDataPO createParentData()
   {
      return this.startCreate().hasParentData().endCreate();
   }

   public DocumentDataPO hasParentData(DocumentDataPO tgt)
   {
      return hasLinkConstraint(tgt, DocumentData.PROPERTY_PARENTDATA);
   }

   public DocumentDataPO createParentData(DocumentDataPO tgt)
   {
      return this.startCreate().hasParentData(tgt).endCreate();
   }

   public DocumentDataSet getParentData()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((DocumentData) this.getCurrentMatch()).getParentData();
      }
      return null;
   }

}
