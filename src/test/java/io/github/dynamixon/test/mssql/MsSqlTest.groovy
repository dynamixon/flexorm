package io.github.dynamixon.test.mssql


import io.github.dynamixon.dataobject.mssql.DummyTableMsSql
import io.github.dynamixon.dataobject.mssql.DummyTableMsSqlAlt
import io.github.dynamixon.dataobject.mssql.DummyTableMsSqlGv
import io.github.dynamixon.dataobject.mssql.DummyTableMsSqlReg
import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.test.CommonTest
import org.junit.Test

class MsSqlTest extends CommonTest{

    @Override
    protected Map<String,Object> configMap(){
        def configMap = super.configMap()
        configMap.putAll([
            "recordClass":[DummyTableMsSqlReg, DummyTableMsSql, DummyTableMsSqlAlt, DummyTableMsSqlGv],
            "dropTableSql":"""
IF OBJECT_ID(N'dbo.TABLE_PLACEHOLDER', N'U') IS NOT NULL
BEGIN
    drop table TABLE_PLACEHOLDER
END
""",
            "dbType":DialectConst.MSSQL,
            "classNeedRegMeta": DummyTableMsSqlReg,
            "sumTestField":"int_f",
            "autoGenColName":"GENERATED_KEYS",
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
