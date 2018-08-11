package de.uks.dss.model.util;

import de.uniks.networkparser.IdMap;

class CreatorCreator{

   public static IdMap createIdMap(String sessionID)
   {
      IdMap jsonIdMap = new IdMap().withSession(sessionID);
      jsonIdMap.with(new DocumentDataCreator());
      jsonIdMap.with(new DocumentDataPOCreator());
      jsonIdMap.with(new TaskCreator());
      jsonIdMap.with(new TaskPOCreator());
      jsonIdMap.with(new PersonCreator());
      jsonIdMap.with(new PersonPOCreator());
      return jsonIdMap;
   }
}
