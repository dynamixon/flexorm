package io.github.dynamixon.test;

import io.github.dynamixon.dataobject.CustomId;
import io.github.dynamixon.flexorm.QueryEntry
import io.github.dynamixon.flexorm.enums.CondAndOr;
import io.github.dynamixon.flexorm.misc.FieldInfoMethodRefUtil;
import io.github.dynamixon.flexorm.pojo.Cond
import io.github.dynamixon.flexorm.pojo.InnerCond;

/**
 * @author Jianfeng.Mao2
 * @date 23-12-15
 */
public class FieldInfoGetterGroovyTest {
    public static void test(QueryEntry qe){
        Cond cond = new Cond(CustomId::getId, "1")
        cond.setInnerCond(new InnerCond(CondAndOr.AND, Collections.singletonList(new Cond(CustomId::getId,"2"))))
        FieldInfoMethodRefUtil.resolveColumnNameFromFieldInfoGetter(qe.getCoreRunner(), cond);
        assert cond.getColumnName().equalsIgnoreCase("id");
        assert cond.getInnerCond().getInnerCondList().get(0).getColumnName().equalsIgnoreCase("id");

    }
}
