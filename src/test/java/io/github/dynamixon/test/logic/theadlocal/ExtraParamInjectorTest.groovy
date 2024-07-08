package io.github.dynamixon.test.logic.theadlocal

import io.github.dynamixon.flexorm.misc.ExtraParamInjector
import io.github.dynamixon.flexorm.misc.GeneralThreadLocal
import io.github.dynamixon.flexorm.pojo.Cond
import io.github.dynamixon.flexorm.pojo.OrderCond
import io.github.dynamixon.flexorm.pojo.Paginator
import io.github.dynamixon.test.logic.LogicTestBase
import org.junit.Test

class ExtraParamInjectorTest implements LogicTestBase {
    @Test
    @Override
    void test() {
        def origMap = GeneralThreadLocal.get()
        try {
            purgeTest()
            forgivablePagingTest()
        } finally {
            GeneralThreadLocal.set(origMap)
        }
    }

    static void purgeTest(){
        ExtraParamInjector.sqlId('dummy-sql-id')
        ExtraParamInjector.selectColumns('col1', 'col2')
        ExtraParamInjector.addCond(new Cond('col1', 'val1'))

        ExtraParamInjector.purgeExtraParam()

        def sqlId = ExtraParamInjector.getSqlId()
        def selectColumns = ExtraParamInjector.getSelectColumns()
        def conds = ExtraParamInjector.getExtraConds()
        assert sqlId==null
        assert selectColumns==null
        assert conds==null
    }

    static void forgivablePagingTest(){
        ExtraParamInjector.paging(Paginator.paging(0, 10,true,new OrderCond('col1', 'asc')))
        assert ExtraParamInjector.getPaginator()!=null
        assert ExtraParamInjector.getPaginator().getOffset()==null
        assert ExtraParamInjector.getPaginator().getLimit()==null
        assert ExtraParamInjector.getPaginator().getNeedCount()==true
        assert ExtraParamInjector.getPaginator().getOrderConds()!=null
    }
}
