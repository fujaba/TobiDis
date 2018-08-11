package de.uks.dss.model.util;

import org.sdmlib.models.pattern.util.PatternObjectCreator;
import de.uniks.networkparser.IdMap;
import de.uks.dss.model.DocumentData;

public class DocumentDataPOCreator extends PatternObjectCreator
{
   @Override
   public Object getSendableInstance(boolean reference)
   {
      if(reference) {
          return new DocumentDataPO(new DocumentData[]{});
      } else {
          return new DocumentDataPO();
      }
   }
   
   public static IdMap createIdMap(String sessionID) {
      return de.uks.dss.model.util.CreatorCreator.createIdMap(sessionID);
   }
}
