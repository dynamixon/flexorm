package io.github.dynamixon.test.spi

import io.github.dynamixon.flexorm.dialect.batch.DefaultBatchInserter
import groovy.util.logging.Slf4j

@Slf4j
class GvTestBatchInserter extends DefaultBatchInserter{
    GvTestBatchInserter() {
        log.info("== GvTestBatchInserter ==")
    }
}
