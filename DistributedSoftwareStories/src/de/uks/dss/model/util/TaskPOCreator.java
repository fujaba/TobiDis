package de.uks.dss.model.util;

import org.sdmlib.models.pattern.util.PatternObjectCreator;
import de.uniks.networkparser.IdMap;
import de.uks.dss.model.Task;

public class TaskPOCreator extends PatternObjectCreator
{
   @Override
   public Object getSendableInstance(boolean reference)
   {
      if(reference) {
          return new TaskPO(new Task[]{});
      } else {
          return new TaskPO();
      }
   }
   
   public static IdMap createIdMap(String sessionID) {
      return de.uks.dss.model.util.CreatorCreator.createIdMap(sessionID);
   }
}
