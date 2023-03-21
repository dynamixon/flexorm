package io.github.dynamixon.test.pg


import io.github.dynamixon.dataobject.pg.DummyTablePG
import io.github.dynamixon.dataobject.pg.DummyTablePGAlt
import io.github.dynamixon.dataobject.pg.DummyTablePGGv
import io.github.dynamixon.dataobject.pg.DummyTablePGReg
import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.test.CommonTest
import org.junit.Test

class PgTest extends CommonTest{

    @Override
    protected Map<String,Object> configMap(){
        def configMap = super.configMap()
        configMap.putAll([
            "recordClass":[DummyTablePGReg, DummyTablePG, DummyTablePGAlt, DummyTablePGGv],
            "dbType":DialectConst.PG,
            "classNeedRegMeta": DummyTablePGReg,
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
