package io.github.dynamixon.test.logic.methodref

import io.github.dynamixon.flexorm.pojo.Cond
import io.github.dynamixon.moredata.LogicTableA

/**
 * @author Jianfeng.Mao2
 * @date 23-12-29
 */
class GroovyMethodRefCond {

    static Cond idCond(){
        return new Cond(LogicTableA::getId,10)
    }

    static Cond allLowerGetterCond(){
        return new Cond(LogicTableA::getalllowergetter,'abc')
    }
}
