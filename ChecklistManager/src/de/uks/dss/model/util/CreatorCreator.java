package de.uks.dss.model.util;

import de.uniks.networkparser.json.JsonIdMap;

class CreatorCreator{

   public static JsonIdMap createIdMap(String sessionID)
   {
      JsonIdMap jsonIdMap = new JsonIdMap().withSessionId(sessionID);
      jsonIdMap.with(new DocumentDataCreator());
      jsonIdMap.with(new DocumentDataPOCreator());
      return jsonIdMap;
   }
}
