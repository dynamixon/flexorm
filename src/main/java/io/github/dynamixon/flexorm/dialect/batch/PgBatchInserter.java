package io.github.dynamixon.flexorm.dialect.batch;

import io.github.dynamixon.flexorm.CoreRunner;
import io.github.dynamixon.flexorm.dialect.DialectConst;

import java.util.List;
import java.util.Map;

public class PgBatchInserter implements BatchInserter {
    @Override
    public String getDatabaseType() {
        return DialectConst.PG;
    }
    @Override
    public int batchInsert(CoreRunner coreRunner, String table, List<Map<String, Object>> listMap){
        return StandardBatchInserter.batchInsert(coreRunner, table, listMap);
    }
}
