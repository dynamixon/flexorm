package io.github.dynamixon.flexorm.dialect.batch;

import io.github.dynamixon.flexorm.CoreRunner;
import io.github.dynamixon.flexorm.enums.SqlExecutionInterceptorChainMode;
import io.github.dynamixon.flexorm.misc.ExtraParamInjector;
import io.github.dynamixon.flexorm.misc.SqlExecutionInterceptor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DefaultBatchInserter implements BatchInserter {
    @Override
    public int batchInsert(CoreRunner coreRunner, String table, List<Map<String, Object>> listMap){
        int affected = 0;
        if(CollectionUtils.isNotEmpty(listMap)){
            List<String> columns = new ArrayList<>(listMap.get(0).keySet());
            StringBuilder sql = new StringBuilder("insert into " + table + " (");
            StringBuilder valueSql = new StringBuilder(" values (");
            List<List<Object>> valuesArr = new ArrayList<>();
            for (String column : columns) {
                sql.append(column).append(",");
                valueSql.append("?,");
            }
            sql = new StringBuilder(StringUtils.stripEnd(sql.toString(), ",") + ") ");
            valueSql = new StringBuilder(StringUtils.stripEnd(valueSql.toString(), ",") + ")");
            sql.append(valueSql);
            for (Map<String, Object> map : listMap) {
                List<Object> values = new ArrayList<>();
                for (String column : columns) {
                    Object value = map.get(column);
                    values.add(value);
                }
                valuesArr.add(values);
            }
            Object[][] array = valuesArr.stream()
                    .map(innerList -> innerList.toArray(new Object[0]))
                    .toArray(Object[][]::new);
            int[] nums = coreRunner.genericBatchUpdate(sql.toString(), array);
            affected = Arrays.stream(nums).sum();
        }
        return affected;
    }
}
