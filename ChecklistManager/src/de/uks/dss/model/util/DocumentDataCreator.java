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
   
package de.uks.dss.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonIdMap;
import de.uks.dss.model.DocumentData;

public class DocumentDataCreator implements SendableEntityCreator
{
   private final String[] properties = new String[]
   {
      DocumentData.PROPERTY_TAG,
      DocumentData.PROPERTY_VALUE,
      DocumentData.PROPERTY_TYPE,
      DocumentData.PROPERTY_LASTEDITOR,
      DocumentData.PROPERTY_LASTMODIFIED,
      DocumentData.PROPERTY_SUBDATA,
      DocumentData.PROPERTY_PARENTDATA,
   };
   
   @Override
   public String[] getProperties()
   {
      return properties;
   }
   
   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new DocumentData();
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

      if (DocumentData.PROPERTY_TAG.equalsIgnoreCase(attribute))
      {
         return ((DocumentData) target).getTag();
      }

      if (DocumentData.PROPERTY_VALUE.equalsIgnoreCase(attribute))
      {
         return ((DocumentData) target).getValue();
      }

      if (DocumentData.PROPERTY_TYPE.equalsIgnoreCase(attribute))
      {
         return ((DocumentData) target).getType();
      }

      if (DocumentData.PROPERTY_LASTEDITOR.equalsIgnoreCase(attribute))
      {
         return ((DocumentData) target).getLastEditor();
      }

      if (DocumentData.PROPERTY_LASTMODIFIED.equalsIgnoreCase(attribute))
      {
         return ((DocumentData) target).getLastModified();
      }

      if (DocumentData.PROPERTY_SUBDATA.equalsIgnoreCase(attribute))
      {
         return ((DocumentData) target).getSubData();
      }

      if (DocumentData.PROPERTY_PARENTDATA.equalsIgnoreCase(attribute))
      {
         return ((DocumentData) target).getParentData();
      }
      
      return null;
   }
   
   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (DocumentData.PROPERTY_LASTMODIFIED.equalsIgnoreCase(attrName))
      {
         ((DocumentData) target).withLastModified((String) value);
         return true;
      }

      if (DocumentData.PROPERTY_LASTEDITOR.equalsIgnoreCase(attrName))
      {
         ((DocumentData) target).withLastEditor((String) value);
         return true;
      }

      if (DocumentData.PROPERTY_TYPE.equalsIgnoreCase(attrName))
      {
         ((DocumentData) target).withType((String) value);
         return true;
      }

      if (DocumentData.PROPERTY_VALUE.equalsIgnoreCase(attrName))
      {
         ((DocumentData) target).withValue((String) value);
         return true;
      }

      if (DocumentData.PROPERTY_TAG.equalsIgnoreCase(attrName))
      {
         ((DocumentData) target).withTag((String) value);
         return true;
      }

      if (JsonIdMap.REMOVE.equals(type) && value != null)
      {
         attrName = attrName + type;
      }

      if (DocumentData.PROPERTY_SUBDATA.equalsIgnoreCase(attrName))
      {
         ((DocumentData) target).withSubData((DocumentData) value);
         return true;
      }
      
      if ((DocumentData.PROPERTY_SUBDATA + JsonIdMap.REMOVE).equalsIgnoreCase(attrName))
      {
         ((DocumentData) target).withoutSubData((DocumentData) value);
         return true;
      }

      if (DocumentData.PROPERTY_PARENTDATA.equalsIgnoreCase(attrName))
      {
         ((DocumentData) target).withParentData((DocumentData) value);
         return true;
      }
      
      if ((DocumentData.PROPERTY_PARENTDATA + JsonIdMap.REMOVE).equalsIgnoreCase(attrName))
      {
         ((DocumentData) target).withoutParentData((DocumentData) value);
         return true;
      }
      
      return false;
   }
   public static JsonIdMap createIdMap(String sessionID)
   {
      return de.uks.dss.model.util.CreatorCreator.createIdMap(sessionID);
   }
   
   //==========================================================================
      public void removeObject(Object entity)
   {
      ((DocumentData) entity).removeYou();
   }
}
