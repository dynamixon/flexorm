package io.github.dynamixon.flexorm.pojo;

import java.util.List;

/**
 * @author Jianfeng.Mao2
 * @date 24-2-19
 */
public class InnerJoin extends Join {
    public static final String JOIN_METHOD = "inner join";

    public InnerJoin() {
    }

    public InnerJoin(String tableName, String tableAlias, List<Cond> joinConds) {
        super(JOIN_METHOD, tableName, tableAlias, joinConds);
    }

    public InnerJoin(String tableName, String tableAlias, String mainTableCol, String joinTableCol) {
        super(JOIN_METHOD, tableName, tableAlias, mainTableCol, joinTableCol);
    }

    public InnerJoin(String mainTableAlias, String tableName, String tableAlias, String mainTableCol, String joinTableCol) {
        super(JOIN_METHOD, mainTableAlias, tableName, tableAlias, mainTableCol, joinTableCol);
    }

    public InnerJoin(String tableName, String tableAlias, Cond ... conds) {
        super(JOIN_METHOD, tableName, tableAlias, conds);
    }
}
