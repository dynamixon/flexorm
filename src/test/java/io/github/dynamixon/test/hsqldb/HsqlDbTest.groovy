package io.github.dynamixon.test.hsqldb


import io.github.dynamixon.dataobject.hsqldb.DummyTableHsqlDb
import io.github.dynamixon.dataobject.hsqldb.DummyTableHsqlDbAlt
import io.github.dynamixon.dataobject.hsqldb.DummyTableHsqlDbGv
import io.github.dynamixon.dataobject.hsqldb.DummyTableHsqlDbReg
import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.test.CommonTest
import org.junit.Test

class HsqlDbTest extends CommonTest{

    @Override
    protected Map<String,Object> configMap(){
        def configMap = super.configMap()
        configMap.putAll([
            "recordClass":[DummyTableHsqlDb, DummyTableHsqlDbAlt, DummyTableHsqlDbGv, DummyTableHsqlDbReg],
            "dbType":DialectConst.HSQLDB,
            "idInt":true,
            "classNeedRegMeta": DummyTableHsqlDbReg,
            "sumTestField":"integer_f",
        ])
        return configMap
    }

    @Test
    @Override
    void test(){
        super.test()
    }
}
