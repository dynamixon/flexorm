package io.github.dynamixon.flexorm.pojo;

import java.util.List;

public class QueryConditionBundle extends ConditionBundle {
    private Class<?> tableClass;
    private Class<?> resultClass;
    private List<String> selectColumns;
    private List<String> excludedColumns;
    private boolean onlyCount;
    private String tableAliasForJoin;
    private List<Join> joins;
    private List<String> groupByColumns;
    private List<Cond> havingConds;
    private List<OrderCond> orderConds;
    private Integer offset;
    private Integer limit;

    public QueryConditionBundle() {
    }

    private QueryConditionBundle(Builder builder) {
        setTargetTable(builder.targetTable);
        setConditionAndList(builder.conditionAndList);
        setConditionOrList(builder.conditionOrList);
        setTableClass(builder.tableClass);
        setResultClass(builder.resultClass);
        setSelectColumns(builder.selectColumns);
        setExcludedColumns(builder.excludedColumns);
        setOnlyCount(builder.onlyCount);
        setTableAliasForJoin(builder.tableAliasForJoin);
        setJoins(builder.joins);
        setGroupByColumns(builder.groupByColumns);
        setHavingConds(builder.havingConds);
        setOrderConds(builder.orderConds);
        setOffset(builder.offset);
        setLimit(builder.limit);
    }

    public Class<?> getResultClass() {
        return resultClass;
    }

    public Class<?> getTableClass() {
        return tableClass;
    }

    public void setTableClass(Class<?> tableClass) {
        this.tableClass = tableClass;
    }

    public void setResultClass(Class<?> resultClass) {
        this.resultClass = resultClass;
    }

    public List<String> getSelectColumns() {
        return selectColumns;
    }

    public void setSelectColumns(List<String> selectColumns) {
        this.selectColumns = selectColumns;
    }

    public List<String> getExcludedColumns() {
        return excludedColumns;
    }

    public void setExcludedColumns(List<String> excludedColumns) {
        this.excludedColumns = excludedColumns;
    }

    public boolean isOnlyCount() {
        return onlyCount;
    }

    public void setOnlyCount(boolean onlyCount) {
        this.onlyCount = onlyCount;
    }

    public String getTableAliasForJoin() {
        return tableAliasForJoin;
    }

    public void setTableAliasForJoin(String tableAliasForJoin) {
        this.tableAliasForJoin = tableAliasForJoin;
    }

    public List<Join> getJoins() {
        return joins;
    }

    public void setJoins(List<Join> joins) {
        this.joins = joins;
    }

    public List<String> getGroupByColumns() {
        return groupByColumns;
    }

    public void setGroupByColumns(List<String> groupByColumns) {
        this.groupByColumns = groupByColumns;
    }

    public List<Cond> getHavingConds() {
        return havingConds;
    }

    public void setHavingConds(List<Cond> havingConds) {
        this.havingConds = havingConds;
    }

    public List<OrderCond> getOrderConds() {
        return orderConds;
    }

    public void setOrderConds(List<OrderCond> orderConds) {
        this.orderConds = orderConds;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public static final class Builder {
        private String targetTable;
        private List<Cond> conditionAndList;
        private List<Cond> conditionOrList;
        private Class<?> tableClass;
        private Class<?> resultClass;
        private List<String> selectColumns;
        private List<String> excludedColumns;
        private boolean onlyCount;
        private String tableAliasForJoin;
        private List<Join> joins;
        private List<String> groupByColumns;
        private List<Cond> havingConds;
        private List<OrderCond> orderConds;
        private Integer offset;
        private Integer limit;

        public Builder() {
        }

        public Builder targetTable(String val) {
            targetTable = val;
            return this;
        }

        public Builder conditionAndList(List<Cond> val) {
            conditionAndList = val;
            return this;
        }

        public Builder conditionOrList(List<Cond> val) {
            conditionOrList = val;
            return this;
        }

        public Builder tableClass(Class<?> val) {
            tableClass = val;
            return this;
        }

        public Builder resultClass(Class<?> val) {
            resultClass = val;
            return this;
        }

        public Builder selectColumns(List<String> val) {
            selectColumns = val;
            return this;
        }

        public Builder excludedColumns(List<String> val) {
            excludedColumns = val;
            return this;
        }

        public Builder onlyCount(boolean val) {
            onlyCount = val;
            return this;
        }

        public Builder tableAliasForJoin(String val) {
            tableAliasForJoin = val;
            return this;
        }

        public Builder joins(List<Join> val) {
            joins = val;
            return this;
        }

        public Builder groupByColumns(List<String> val) {
            groupByColumns = val;
            return this;
        }

        public Builder havingConds(List<Cond> val) {
            havingConds = val;
            return this;
        }

        public Builder orderConds(List<OrderCond> val) {
            orderConds = val;
            return this;
        }

        public Builder offset(Integer val) {
            offset = val;
            return this;
        }

        public Builder limit(Integer val) {
            limit = val;
            return this;
        }

        public QueryConditionBundle build() {
            return new QueryConditionBundle(this);
        }
    }
}
