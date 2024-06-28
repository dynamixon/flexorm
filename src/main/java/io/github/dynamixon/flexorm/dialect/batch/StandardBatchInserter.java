package io.github.dynamixon.flexorm.dialect.batch;

import io.github.dynamixon.flexorm.CoreRunner;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jianfeng.Mao2
 * @date 24-2-6
 */
public class StandardBatchInserter {

    public static int batchInsert(CoreRunner coreRunner, String table, List<Map<String, Object>> listMap) {
        int affectedNum = 0;
        if(CollectionUtils.isNotEmpty(listMap)){
            List<String> columns = new ArrayList<>(listMap.get(0).keySet());
            int colNum = columns.size();
            StringBuilder sql = new StringBuilder("insert into " + table + " (");
            StringBuilder valueSql = new StringBuilder(" values");
            List<Object> values = new ArrayList<>();
            for (String column : columns) {
                sql.append(column).append(",");
            }
            sql = new StringBuilder(StringUtils.stripEnd(sql.toString(), ",") + ") ");
            for (Map<String, Object> map : listMap) {
                valueSql.append(" ( ");
                for(int i=0;i<colNum;i++){
                    String column = columns.get(i);
                    Object value = map.get(column);
                    valueSql.append("?,");
                    values.add(value);
                }
                valueSql = new StringBuilder(StringUtils.stripEnd(valueSql.toString(), ",") + ") ,");
            }
            valueSql = new StringBuilder(StringUtils.stripEnd(valueSql.toString(), ","));
            sql.append(valueSql);
            affectedNum = coreRunner.genericUpdate(sql.toString(),values.toArray());
        }
        return affectedNum;
    }
}
