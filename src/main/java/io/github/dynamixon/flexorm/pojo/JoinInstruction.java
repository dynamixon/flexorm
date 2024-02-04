package io.github.dynamixon.flexorm.pojo;

import java.util.List;

/**
 * @author Jianfeng.Mao2
 * @date 24-2-4
 */
public class JoinInstruction {
    private String joinType;
    private String tableName;
    private String alias;
    private List<Cond> joinConds;

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<Cond> getJoinConds() {
        return joinConds;
    }

    public void setJoinConds(List<Cond> joinConds) {
        this.joinConds = joinConds;
    }
}
