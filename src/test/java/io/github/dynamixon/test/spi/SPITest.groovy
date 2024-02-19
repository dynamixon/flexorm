package io.github.dynamixon.test.spi

import io.github.dynamixon.flexorm.dialect.batch.H2DbBatchInserter
import io.github.dynamixon.test.fakedb.FakeDbBatchInserter
import io.github.dynamixon.flexorm.dialect.batch.BatchInserter
import io.github.dynamixon.flexorm.dialect.batch.DefaultBatchInserter
import io.github.dynamixon.flexorm.dialect.batch.HsqlDbBatchInserter
import io.github.dynamixon.flexorm.dialect.batch.MsSqlBatchInserter
import io.github.dynamixon.flexorm.dialect.batch.MysqlBatchInserter
import io.github.dynamixon.flexorm.dialect.batch.PgBatchInserter
import io.github.dynamixon.flexorm.dialect.batch.SqliteBatchInserter
import org.junit.Test

class SPITest {
    @Test
    void test(){
        List<BatchInserter> batchInserterList = new ArrayList<>()
        ServiceLoader<BatchInserter> batchInserters = ServiceLoader.load(BatchInserter.class)
        for (BatchInserter batchInserter : batchInserters) {
            batchInserterList.add(batchInserter)
        }
        assert batchInserterList.size() == 10
        assert batchInserterList[0] instanceof TestBatchInserter
        assert batchInserterList[1] instanceof GvTestBatchInserter
        assert batchInserterList[2] instanceof FakeDbBatchInserter
        assert batchInserterList[3] instanceof DefaultBatchInserter
        assert batchInserterList[4] instanceof HsqlDbBatchInserter
        assert batchInserterList[5] instanceof MsSqlBatchInserter
        assert batchInserterList[6] instanceof MysqlBatchInserter
        assert batchInserterList[7] instanceof PgBatchInserter
        assert batchInserterList[8] instanceof SqliteBatchInserter
        assert batchInserterList[9] instanceof H2DbBatchInserter
    }
}
