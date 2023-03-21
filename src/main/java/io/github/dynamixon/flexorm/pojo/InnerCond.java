package io.github.dynamixon.flexorm.pojo;

import io.github.dynamixon.flexorm.enums.CondAndOr;

import java.util.List;

/**
 * @author maojianfeng
 * @date 22-9-8
 */
public class InnerCond {
    private final CondAndOr innerCondAndOr;
    private final List<Cond> innerCondList;

    public InnerCond(CondAndOr innerCondAndOr,List<Cond> innerCondList) {
        this.innerCondAndOr = innerCondAndOr;
        this.innerCondList = innerCondList;
    }

    public InnerCond(List<Cond> innerCondList) {
        this(null,innerCondList);
    }

    public CondAndOr getInnerCondAndOr() {
        return innerCondAndOr;
    }

    public List<Cond> getInnerCondList() {
        return innerCondList;
    }
}
