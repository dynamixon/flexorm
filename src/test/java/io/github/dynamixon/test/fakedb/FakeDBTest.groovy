package io.github.dynamixon.test.fakedb


import io.github.dynamixon.dataobject.hsqldb.DummyTableHsqlDb
import io.github.dynamixon.dataobject.hsqldb.DummyTableHsqlDbAlt
import io.github.dynamixon.dataobject.hsqldb.DummyTableHsqlDbGv
import io.github.dynamixon.dataobject.hsqldb.DummyTableHsqlDbReg
import io.github.dynamixon.test.CommonTest
import org.junit.Test

class FakeDBTest extends CommonTest{

    @Override
    protected Map<String,Object> configMap(){
        def configMap = super.configMap()
        configMap.putAll([
            "recordClass":[DummyTableHsqlDb, DummyTableHsqlDbAlt, DummyTableHsqlDbGv, DummyTableHsqlDbReg],
            "dbType": FakeDBConst.DB_TYPE,
            "setupDbType": FakeDBConst.DB_TYPE,
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
