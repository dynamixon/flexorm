package io.github.dynamixon.test

import io.github.dynamixon.dataobject.CustomId
import io.github.dynamixon.dataobject.DummyTable
import io.github.dynamixon.flexorm.QueryEntry
import io.github.dynamixon.flexorm.enums.CondAndOr
import io.github.dynamixon.flexorm.misc.FieldInfoMethodRefUtil
import io.github.dynamixon.flexorm.misc.GeneralThreadLocal
import io.github.dynamixon.flexorm.misc.MiscUtil
import io.github.dynamixon.flexorm.pojo.Cond
import io.github.dynamixon.flexorm.pojo.InnerCond
import io.github.dynamixon.test.methodref.Child
import io.github.dynamixon.test.methodref.GChild
import io.github.dynamixon.test.methodref.Parent

/**
 * @author Jianfeng.Mao2
 * @date 23-12-15
 */
class FieldInfoGetterGroovyTest{
    private  CommonTest commonTest

    void test(){
        QueryEntry qe = commonTest.qe
        Cond cond = new Cond(CustomId::getId, "1")
        cond.setInnerCond(new InnerCond(CondAndOr.AND, Collections.singletonList(new Cond(CustomId::getId,"2"))))
        FieldInfoMethodRefUtil.resolveColumnNameFromFieldInfoGetter(qe.getCoreRunner(), cond);
        assert cond.getColumnName().equalsIgnoreCase("id");
        assert cond.getInnerCond().getInnerCondList().get(0).getColumnName().equalsIgnoreCase("id");

        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def record = list.get(0)
        def id = MiscUtil.extractFieldValueFromObj(record,"id")
        def count = qe.count(CommonTest.getCurrentClass(), new Cond(CustomId::getId, id))
        assert count==1
    }

    static void extraTest(){
        Cond cond = new Cond(GChild::getName, "2")
        def methodRef = FieldInfoMethodRefUtil.getMethodRefFromGroovyProxy(cond.getFieldInfoGetter())
        assert methodRef.clazz == GChild.class

        cond = new Cond(Child::getName, "2")
        methodRef = FieldInfoMethodRefUtil.getMethodRefFromGroovyProxy(cond.getFieldInfoGetter())
        assert methodRef.clazz == Child.class

        cond = new Cond(Parent::getName, "2")
        methodRef = FieldInfoMethodRefUtil.getMethodRefFromGroovyProxy(cond.getFieldInfoGetter())
        assert methodRef.clazz == Parent.class
    }
}
