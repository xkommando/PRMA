package com.caibowen.prma.logger.dao;

import java.util.Map;

/**
 * @author BowenCai
 * @since 22-10-2014.
 */
public interface PropertyDAO {//extends Int4DAO<Pair<String, Object>> {

    boolean insert(long eventId, Map<String, String> prop);




//    Map<String, String> mergedMap = mergePropertyMaps(event);
//    insertProperties(mergedMap, connection, eventId);

//    protected void insertProperties(Map<String, String> mergedMap,
//                                    Connection connection, long eventId) throws SQLException {
//        Set<String> propertiesKeys = mergedMap.keySet();
//        if (propertiesKeys.size() > 0) {
//            PreparedStatement insertPropertiesStatement = null;
//            try {
//                insertPropertiesStatement = connection
//                        .prepareStatement(insertPropertiesSQL);
//
//                for (String key : propertiesKeys) {
//                    String value = mergedMap.get(key);
//
//                    insertPropertiesStatement.setLong(1, eventId);
//                    insertPropertiesStatement.setString(2, key);
//                    insertPropertiesStatement.setString(3, value);
//
//                    if (cnxSupportsBatchUpdates) {
//                        insertPropertiesStatement.addBatch();
//                    } else {
//                        insertPropertiesStatement.execute();
//                    }
//                }
//
//                if (cnxSupportsBatchUpdates) {
//                    insertPropertiesStatement.executeBatch();
//                }
//            } finally {
//                closeStatement(insertPropertiesStatement);
//            }
//        }
//    }

}
