package io.github.dynamixon.test

import io.github.dynamixon.flexorm.misc.ExtraParamInjector
import io.github.dynamixon.flexorm.pojo.Cond
import org.junit.Test

class LocalLogicTest {

    @Test
    void test(){
        extraCondTest()
        extraOrCondTest()
    }

    void extraCondTest(){
        ExtraParamInjector.addCond([new Cond("field_1","value_1")])
        def extraConds = ExtraParamInjector.getExtraConds()
        assert extraConds.size()==1
        assert extraConds[0].getColumnName() == 'field_1'
        assert extraConds[0].getValue() == 'value_1'
        ExtraParamInjector.addCond([new Cond("field_2","value_2")])
        extraConds = ExtraParamInjector.getExtraConds()
        assert extraConds.size()==2
        assert extraConds[1].getColumnName() == 'field_2'
        assert extraConds[1].getValue() == 'value_2'
        ExtraParamInjector.unsetExtraConds()
    }

    void extraOrCondTest(){
        ExtraParamInjector.addOrCond([new Cond("field_1","value_1")])
        def extraConds = ExtraParamInjector.getExtraOrConds()
        assert extraConds.size()==1
        assert extraConds[0].getColumnName() == 'field_1'
        assert extraConds[0].getValue() == 'value_1'
        ExtraParamInjector.addOrCond([new Cond("field_2","value_2")])
        extraConds = ExtraParamInjector.getExtraOrConds()
        assert extraConds.size()==2
        assert extraConds[1].getColumnName() == 'field_2'
        assert extraConds[1].getValue() == 'value_2'
        ExtraParamInjector.unsetExtraOrConds()
    }
}
