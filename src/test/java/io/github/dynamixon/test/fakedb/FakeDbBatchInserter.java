package io.github.dynamixon.test.fakedb;

import io.github.dynamixon.flexorm.dialect.batch.HsqlDbBatchInserter;

public class FakeDbBatchInserter extends HsqlDbBatchInserter {
    @Override
    public String getDatabaseType() {
        return FakeDBConst.DB_TYPE;
    }
}
