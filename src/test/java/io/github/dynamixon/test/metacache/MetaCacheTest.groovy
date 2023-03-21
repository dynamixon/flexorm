package io.github.dynamixon.test.metacache

import io.github.dynamixon.flexorm.logic.TableObjectMetaCache
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MetaCacheTest {
    protected static final Logger logger = LoggerFactory.getLogger(MetaCacheTest.class)

    @Test
    void test(){
        // todo
    }

    static void printCurrentCache(){
        def metaMap = TableObjectMetaCache.getMetaMap()
        metaMap.each {
            logger.info("=== datasource:${it.getKey()} === ")
            def metaHolder = it.getValue()
            logger.info("TableNameMap:${metaHolder.getTableNameMap()}")
            logger.info("PrimaryFieldsClassMap:${metaHolder.getPrimaryFieldsClassMap()}")
            logger.info("FieldToColumnClassMap:${metaHolder.getFieldToColumnClassMap()}")
            logger.info("ColumnToFieldClassMap:${metaHolder.getColumnToFieldClassMap()}")
        }
    }
}
