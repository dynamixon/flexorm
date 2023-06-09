package io.github.dynamixon.flexorm.dialect.batch;

import io.github.dynamixon.flexorm.CoreRunner;
import io.github.dynamixon.flexorm.misc.ExtraParamInjector;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

public class DefaultBatchInserter implements BatchInserter {
    @Override
    public int batchInsert(CoreRunner coreRunner, String table, List<Map<String, Object>> listMap){
        int affected = 0;
        if(CollectionUtils.isNotEmpty(listMap)){
            String sqlId = ExtraParamInjector.getSqlId();
            for (Map<String, Object> map : listMap) {
                ExtraParamInjector.sqlId(sqlId);
                affected += coreRunner.insert(table,map);
            }
        }
        return affected;
    }
}
