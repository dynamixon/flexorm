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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Jianfeng.Mao2
 * @date 23-12-15
 */
public class FieldInfoGetterJavaTest{
    private final CommonTest commonTest;

    public FieldInfoGetterJavaTest(CommonTest commonTest) {
        this.commonTest = commonTest;
    }

    public void test(){
        QueryEntry qe = commonTest.qe;
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
        int count = qe.count(CommonTest.getCurrentClass(), new Cond(CustomId::getId, id));
        assert count == 1;
    }
}
