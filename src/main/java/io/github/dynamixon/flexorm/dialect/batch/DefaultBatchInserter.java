package io.github.dynamixon.flexorm.dialect.batch;

import io.github.dynamixon.flexorm.CoreRunner;
import io.github.dynamixon.flexorm.enums.SqlExecutionInterceptorChainMode;
import io.github.dynamixon.flexorm.misc.ExtraParamInjector;
import io.github.dynamixon.flexorm.misc.SqlExecutionInterceptor;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

public class DefaultBatchInserter implements BatchInserter {
    @Override
    public int batchInsert(CoreRunner coreRunner, String table, List<Map<String, Object>> listMap){
        int affected = 0;
        if(CollectionUtils.isNotEmpty(listMap)){
            String sqlId = ExtraParamInjector.getSqlId();
            SqlExecutionInterceptor sqlExecutionInterceptor = ExtraParamInjector.getSqlInterceptor();
            SqlExecutionInterceptorChainMode sqlInterceptorChainMode = ExtraParamInjector.getSqlInterceptorChainMode();
            boolean interceptorSpan = sqlExecutionInterceptor !=null&& sqlExecutionInterceptor.spanWithin();
            for (Map<String, Object> map : listMap) {
                ExtraParamInjector.sqlId(sqlId);
                if(interceptorSpan){
                    ExtraParamInjector.interceptWithChainMode(sqlExecutionInterceptor,sqlInterceptorChainMode!=null?sqlInterceptorChainMode:SqlExecutionInterceptorChainMode.CHAIN_AFTER_GLOBAL);
                }
                affected += coreRunner.insert(table,map);
            }
        }
        return affected;
    }
}
