package io.github.dynamixon.test.logic.methodref;

import io.github.dynamixon.flexorm.pojo.ColumnValuePair4Update;
import io.github.dynamixon.flexorm.pojo.Cond;
import io.github.dynamixon.flexorm.pojo.OrderCond;
import io.github.dynamixon.moredata.LogicTableA;

/**
 * @author Jianfeng.Mao2
 * @date 23-12-29
 */
public class JavaMethodRefCond {

    public static Cond idCond(){
        return new Cond(LogicTableA::getId,10);
    }

    public static Cond allLowerGetterCond(){
        return new Cond(LogicTableA::getalllowergetter,"abc");
    }

    public static OrderCond allLowerGetterOrderCond(){
        return new OrderCond(LogicTableA::getalllowergetter,"desc");
    }

    public static ColumnValuePair4Update allLowerGetterColumnValuePair4Update(){
        return new ColumnValuePair4Update(LogicTableA::getalllowergetter,"xxx");
    }
}
