package io.github.dynamixon.flexorm.dialect.batch;



import io.github.dynamixon.flexorm.CoreRunner;
import io.github.dynamixon.flexorm.dialect.DatabaseTypeMatcher;

import java.util.List;
import java.util.Map;

public interface BatchInserter extends DatabaseTypeMatcher {
    int batchInsert(CoreRunner coreRunner, String table, List<Map<String, Object>> listMap);
}
