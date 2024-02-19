package io.github.dynamixon.flexorm.pojo;

import java.util.List;

/**
 * @author Jianfeng.Mao2
 * @date 24-2-19
 */
public class LeftJoinInstruction extends JoinInstruction{
    public static final String JOIN_METHOD = "left join";

    public LeftJoinInstruction() {
    }

    public LeftJoinInstruction(String tableName, String tableAlias, List<Cond> joinConds) {
        super(JOIN_METHOD, tableName, tableAlias, joinConds);
    }

    public LeftJoinInstruction(String tableName,String tableAlias, String mainTableCol, String joinTableCol) {
        super(JOIN_METHOD, tableName, tableAlias, mainTableCol, joinTableCol);
    }

    public LeftJoinInstruction(String mainTableAlias, String tableName,String tableAlias, String mainTableCol, String joinTableCol) {
        super(JOIN_METHOD, mainTableAlias, tableName, tableAlias, mainTableCol, joinTableCol);
    }

    public LeftJoinInstruction(String tableName,String tableAlias, Cond ... conds) {
        super(JOIN_METHOD, tableName, tableAlias, conds);
    }
}
