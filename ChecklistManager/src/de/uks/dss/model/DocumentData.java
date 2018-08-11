/*
   Copyright (c) 2015 zuendorf
   
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

import de.uniks.networkparser.interfaces.SendableEntity;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import org.sdmlib.StrUtil;
import de.uks.dss.model.util.DocumentDataSet;
   /**
    * 
    * @see <a href='../../../../../src/de/uks/dss/model/BasicModel.java'>BasicModel.java</a>/n */
   public  class DocumentData implements SendableEntity
{

   
   //==========================================================================
   
   protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
   
   public PropertyChangeSupport getPropertyChangeSupport()
   {
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

   
   //==========================================================================
   
   
   public void removeYou()
   {
   
      withoutSubData(this.getSubData().toArray(new DocumentData[this.getSubData().size()]));
      withoutParentData(this.getParentData().toArray(new DocumentData[this.getParentData().size()]));
      getPropertyChangeSupport().firePropertyChange("REMOVE_YOU", this, null);
   }

   
   //==========================================================================
   
   public static final String PROPERTY_TAG = "tag";
   
   private String tag;

   public String getTag()
   {
      return this.tag;
   }
   
   public void setTag(String value)
   {
      if ( ! StrUtil.stringEquals(this.tag, value)) {
      
         String oldValue = this.tag;
         this.tag = value;
         getPropertyChangeSupport().firePropertyChange(PROPERTY_TAG, oldValue, value);
      }
   }
   
   public DocumentData withTag(String value)
   {
      setTag(value);
      return this;
   } 


   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getTag());
      result.append(" ").append(this.getValue());
      result.append(" ").append(this.getType());
      result.append(" ").append(this.getLastEditor());
      result.append(" ").append(this.getLastModified());
      return result.substring(1);
   }


   
   //==========================================================================
   
   public static final String PROPERTY_VALUE = "value";
   
   private String value;

   public String getValue()
   {
      return this.value;
   }
   
   public void setValue(String value)
   {
      if ( ! StrUtil.stringEquals(this.value, value)) {
      
         String oldValue = this.value;
         this.value = value;
         getPropertyChangeSupport().firePropertyChange(PROPERTY_VALUE, oldValue, value);
      }
   }
   
   public DocumentData withValue(String value)
   {
      setValue(value);
      return this;
   } 

   
   //==========================================================================
   
   public static final String PROPERTY_TYPE = "type";
   
   private String type;

   public String getType()
   {
      return this.type;
   }
   
   public void setType(String value)
   {
      if ( ! StrUtil.stringEquals(this.type, value)) {
      
         String oldValue = this.type;
         this.type = value;
         getPropertyChangeSupport().firePropertyChange(PROPERTY_TYPE, oldValue, value);
      }
   }
   
   public DocumentData withType(String value)
   {
      setType(value);
      return this;
   } 

   
   //==========================================================================
   
   public static final String PROPERTY_LASTEDITOR = "lastEditor";
   
   private String lastEditor;

   public String getLastEditor()
   {
      return this.lastEditor;
   }
   
   public void setLastEditor(String value)
   {
      if ( ! StrUtil.stringEquals(this.lastEditor, value)) {
      
         String oldValue = this.lastEditor;
         this.lastEditor = value;
         getPropertyChangeSupport().firePropertyChange(PROPERTY_LASTEDITOR, oldValue, value);
      }
   }
   
   public DocumentData withLastEditor(String value)
   {
      setLastEditor(value);
      return this;
   } 

   
   //==========================================================================
   
   public static final String PROPERTY_LASTMODIFIED = "lastModified";
   
   private String lastModified;

   public String getLastModified()
   {
      return this.lastModified;
   }
   
   public void setLastModified(String value)
   {
      if ( ! StrUtil.stringEquals(this.lastModified, value)) {
      
         String oldValue = this.lastModified;
         this.lastModified = value;
         getPropertyChangeSupport().firePropertyChange(PROPERTY_LASTMODIFIED, oldValue, value);
      }
   }
   
   public DocumentData withLastModified(String value)
   {
      setLastModified(value);
      return this;
   } 

   
   /********************************************************************
    * <pre>
    *              many                       many
    * DocumentData ----------------------------------- DocumentData
    *              parentData                   subData
    * </pre>
    */
   
   public static final String PROPERTY_SUBDATA = "subData";

   private DocumentDataSet subData = null;
   
   public DocumentDataSet getSubData()
   {
      if (this.subData == null)
      {
         return DocumentDataSet.EMPTY_SET;
      }
   
      return this.subData;
   }
   public DocumentDataSet getSubDataTransitive()
   {
      DocumentDataSet result = new DocumentDataSet().with(this);
      return result.getSubDataTransitive();
   }


   public DocumentData withSubData(DocumentData... value)
   {
      if(value==null){
         return this;
      }
      for (DocumentData item : value)
      {
         if (item != null)
         {
            if (this.subData == null)
            {
               this.subData = new DocumentDataSet();
            }
            
            boolean changed = this.subData.add (item);

            if (changed)
            {
               item.withParentData(this);
               getPropertyChangeSupport().firePropertyChange(PROPERTY_SUBDATA, null, item);
            }
         }
      }
      return this;
   } 

   public DocumentData withoutSubData(DocumentData... value)
   {
      for (DocumentData item : value)
      {
         if ((this.subData != null) && (item != null))
         {
            if (this.subData.remove(item))
            {
               item.withoutParentData(this);
               getPropertyChangeSupport().firePropertyChange(PROPERTY_SUBDATA, item, null);
            }
         }
      }
      return this;
   }

   public DocumentData createSubData()
   {
      DocumentData value = new DocumentData();
      withSubData(value);
      return value;
   } 

   
   /********************************************************************
    * <pre>
    *              many                       many
    * DocumentData ----------------------------------- DocumentData
    *              subData                   parentData
    * </pre>
    */
   
   public static final String PROPERTY_PARENTDATA = "parentData";

   private DocumentDataSet parentData = null;
   
   public DocumentDataSet getParentData()
   {
      if (this.parentData == null)
      {
         return DocumentDataSet.EMPTY_SET;
      }
   
      return this.parentData;
   }
   public DocumentDataSet getParentDataTransitive()
   {
      DocumentDataSet result = new DocumentDataSet().with(this);
      return result.getParentDataTransitive();
   }


   public DocumentData withParentData(DocumentData... value)
   {
      if(value==null){
         return this;
      }
      for (DocumentData item : value)
      {
         if (item != null)
         {
            if (this.parentData == null)
            {
               this.parentData = new DocumentDataSet();
            }
            
            boolean changed = this.parentData.add (item);

            if (changed)
            {
               item.withSubData(this);
               getPropertyChangeSupport().firePropertyChange(PROPERTY_PARENTDATA, null, item);
            }
         }
      }
      return this;
   } 

   public DocumentData withoutParentData(DocumentData... value)
   {
      for (DocumentData item : value)
      {
         if ((this.parentData != null) && (item != null))
         {
            if (this.parentData.remove(item))
            {
               item.withoutSubData(this);
               getPropertyChangeSupport().firePropertyChange(PROPERTY_PARENTDATA, item, null);
            }
         }
      }
      return this;
   }

   public DocumentData createParentData()
   {
      DocumentData value = new DocumentData();
      withParentData(value);
      return value;
   } 
}
