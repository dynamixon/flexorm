package io.github.dynamixon.test.mysql


import io.github.dynamixon.dataobject.mysql.DummyTableMysql
import io.github.dynamixon.dataobject.mysql.DummyTableMysqlAlt
import io.github.dynamixon.dataobject.mysql.DummyTableMysqlGv
import io.github.dynamixon.dataobject.mysql.DummyTableMysqlReg
import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.test.CommonTest
import org.junit.Test

class MysqlTest extends CommonTest{

    @Override
    protected Map<String,Object> configMap(){
        def configMap = super.configMap()
        configMap.putAll([
            "recordClass":[DummyTableMysqlReg, DummyTableMysql, DummyTableMysqlAlt, DummyTableMysqlGv],
            "dbType":DialectConst.MYSQL,
            "classNeedRegMeta": DummyTableMysqlReg,
            "sumTestField":"int_f",
            "autoGenColName":"GENERATED_KEY",
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
