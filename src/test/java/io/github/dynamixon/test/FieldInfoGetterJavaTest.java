package io.github.dynamixon.test;

import io.github.dynamixon.dataobject.CustomId;
import io.github.dynamixon.dataobject.DummyTable;
import io.github.dynamixon.flexorm.QueryEntry;
import io.github.dynamixon.flexorm.enums.CondAndOr;
import io.github.dynamixon.flexorm.misc.FieldInfoMethodRefUtil;
import io.github.dynamixon.flexorm.misc.GeneralThreadLocal;
import io.github.dynamixon.flexorm.misc.MiscUtil;
import io.github.dynamixon.flexorm.pojo.Cond;
import io.github.dynamixon.flexorm.pojo.InnerCond;
import io.github.dynamixon.flexorm.pojo.MethodRef;
import io.github.dynamixon.test.methodref.Child;
import io.github.dynamixon.test.methodref.GChild;
import io.github.dynamixon.test.methodref.Parent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Jianfeng.Mao2
 * @date 23-12-15
 */
public class FieldInfoGetterJavaTest{
    private final QueryEntry qe;

    public FieldInfoGetterJavaTest(QueryEntry qe) {
        this.qe = qe;
    }

    public void test(){
        Cond cond = new Cond(CustomId::getId, "1");
        cond.setInnerCond(new InnerCond(CondAndOr.AND, Collections.singletonList(new Cond(CustomId::getId,"2"))));
        FieldInfoMethodRefUtil.resolveColumnNameFromFieldInfoGetter(qe.getCoreRunner(), cond);
        assert cond.getColumnName().equalsIgnoreCase("id");
        assert cond.getInnerCond().getInnerCondList().get(0).getColumnName().equalsIgnoreCase("id");

        List<Cond> conds = Arrays.asList(new Cond("idX", "1"),new Cond(CustomId::getId, "2"));
        FieldInfoMethodRefUtil.resolveColumnNameFromFieldInfoGetter(qe.getCoreRunner(), conds);
        assert conds.get(1).getColumnName().equalsIgnoreCase("id");

        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords");
        DummyTable record = list.get(0);
        Object id = null;
        try {
            id = MiscUtil.extractFieldValueFromObj(record,"id");
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
        int count = qe.count(GeneralThreadLocal.get("CurrentClass"), new Cond(CustomId::getId, id));
        assert count == 1;
    }

    public static void extraTest(){
        Cond cond = new Cond(GChild::getName, "2");
        MethodRef methodRef = FieldInfoMethodRefUtil.getMethodRefFromJava(cond.getFieldInfoGetter());
        assert methodRef.getClazz() == GChild.class;

        cond = new Cond(Child::getName, "2");
        methodRef = FieldInfoMethodRefUtil.getMethodRefFromJava(cond.getFieldInfoGetter());
        assert methodRef.getClazz() == Child.class;

        cond = new Cond(Parent::getName, "2");
        methodRef = FieldInfoMethodRefUtil.getMethodRefFromJava(cond.getFieldInfoGetter());
        assert methodRef.getClazz() == Parent.class;
    }
}
