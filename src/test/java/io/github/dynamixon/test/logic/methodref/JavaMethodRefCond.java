package io.github.dynamixon.test.logic.methodref;

import io.github.dynamixon.flexorm.pojo.Cond;
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
}
