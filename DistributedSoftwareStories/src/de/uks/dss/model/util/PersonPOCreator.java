package de.uks.dss.model.util;

import org.sdmlib.models.pattern.util.PatternObjectCreator;
import de.uniks.networkparser.IdMap;
import de.uks.dss.model.Person;

public class PersonPOCreator extends PatternObjectCreator
{
   @Override
   public Object getSendableInstance(boolean reference)
   {
      if(reference) {
          return new PersonPO(new Person[]{});
      } else {
          return new PersonPO();
      }
   }
   
   public static IdMap createIdMap(String sessionID) {
      return de.uks.dss.model.util.CreatorCreator.createIdMap(sessionID);
   }
}
