package io.github.dynamixon.test;

import io.github.dynamixon.dataobject.CustomId;
import io.github.dynamixon.flexorm.QueryEntry;
import io.github.dynamixon.flexorm.misc.CondUtil;
import io.github.dynamixon.flexorm.pojo.Cond;

/**
 * @author Jianfeng.Mao2
 * @date 23-12-15
 */
public class FieldInfoGetterTest {
    public static void test(QueryEntry qe){
        Cond cond = new Cond(CustomId::getId, '1');
        CondUtil.resolveColumnNameFromFieldInfoGetter(qe.getCoreRunner(), cond);
        assert cond.getColumnName().equalsIgnoreCase("id");

    }
}
