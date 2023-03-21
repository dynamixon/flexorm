package io.github.dynamixon.test.sqlite


import io.github.dynamixon.dataobject.sqlite.DummyTableSqlite
import io.github.dynamixon.dataobject.sqlite.DummyTableSqliteAlt
import io.github.dynamixon.dataobject.sqlite.DummyTableSqliteGv
import io.github.dynamixon.dataobject.sqlite.DummyTableSqliteReg
import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.test.CommonTest
import org.junit.Test

class SqliteTest extends CommonTest{

    @Override
    protected Map<String,Object> configMap(){
        def configMap = super.configMap()
        configMap.putAll([
            "recordClass":[DummyTableSqlite, DummyTableSqliteAlt, DummyTableSqliteGv, DummyTableSqliteReg],
            "dbType":DialectConst.SQLITE,
            "cleanUpSql":"delete from dummy_table",
            "nullField4Test":["textF","text_f"],
            "classNeedRegMeta": DummyTableSqliteReg,
            "sumTestField":"numeric_f",
            "autoGenColName":"last_insert_rowid()",
        ])
        return configMap
    }

//    @Ignore
    @Test
    @Override
    void test(){
        super.test()
    }
}
