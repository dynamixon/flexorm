package io.github.dynamixon.flexorm.pojo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Jianfeng.Mao2
 * @date 24-2-4
 */
public class JoinInstruction {
    private String joinMethod;
    private String tableName;
    private String tableAlias;
    private List<Cond> joinConds;

    public JoinInstruction() {
    }

    public JoinInstruction(String joinMethod, String tableName, String tableAlias, List<Cond> joinConds) {
        this.joinMethod = joinMethod;
        this.tableName = tableName;
        this.tableAlias = tableAlias;
        this.joinConds = joinConds;
    }

    public JoinInstruction(String tableName,String tableAlias, String mainTableCol, String joinTableCol) {
        this("left join", tableName, tableAlias, mainTableCol, joinTableCol);
    }

    public JoinInstruction(String joinMethod, String tableName,String tableAlias, String mainTableCol, String joinTableCol) {
        this(joinMethod, tableName, tableAlias, Collections.singletonList(new Cond.Builder().columnName(mainTableCol).compareOpr(" = "+joinTableCol).ignoreNull(true).build()));
    }

    public JoinInstruction(String tableName,String tableAlias, Cond ... conds) {
        this("left join", tableName, tableAlias, Arrays.asList(conds));
    }

    public JoinInstruction(String joinMethod, String tableName,String tableAlias, Cond ... conds) {
        this(joinMethod, tableName, tableAlias, Arrays.asList(conds));
    }

    public String getJoinMethod() {
        return joinMethod;
    }

    public void setJoinMethod(String joinMethod) {
        this.joinMethod = joinMethod;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public List<Cond> getJoinConds() {
        return joinConds;
    }

    public void setJoinConds(List<Cond> joinConds) {
        this.joinConds = joinConds;
    }
}
