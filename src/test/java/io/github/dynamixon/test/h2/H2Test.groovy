package io.github.dynamixon.test.h2


import io.github.dynamixon.dataobject.h2.DummyTableH2
import io.github.dynamixon.dataobject.h2.DummyTableH2Alt
import io.github.dynamixon.dataobject.h2.DummyTableH2Gv
import io.github.dynamixon.dataobject.h2.DummyTableH2Reg
import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.test.CommonTest
import org.junit.Test

class H2Test extends CommonTest{

    @Override
    protected Map<String,Object> configMap(){
        def configMap = super.configMap()
        configMap.putAll([
            "recordClass":[DummyTableH2, DummyTableH2Alt, DummyTableH2Gv, DummyTableH2Reg],
            "dbType":DialectConst.H2,
            "classNeedRegMeta":DummyTableH2Reg,
            "sumTestField":"integer_f",
            "autoGenColName":"id",
        ])
        return configMap
    }

    @Test
    @Override
    void test(){
        super.test()
    }
}
