package io.github.dynamixon.test.spi;

import io.github.dynamixon.flexorm.dialect.batch.DefaultBatchInserter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestBatchInserter extends DefaultBatchInserter {
    public TestBatchInserter() {
        log.info("== TestBatchInserter ==");
    }
}
