package io.github.dynamixon.flexorm.pojo;

import java.util.Arrays;

public class SqlPreparedBundle {
    private String sql;
    private Object[] values;
    private boolean withCondition;

    public SqlPreparedBundle() {
    }

    public SqlPreparedBundle(String sql, Object[] values) {
        this.sql = sql;
        this.values = values;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public boolean isWithCondition() {
        return withCondition;
    }

    public void setWithCondition(boolean withCondition) {
        this.withCondition = withCondition;
    }

    @Override
    public String toString() {
        return "SqlPreparedBundle{" +
            "sql='" + sql + '\'' +
            ", values=" + Arrays.toString(values) +
            ", withCondition=" + withCondition +
            '}';
    }
}
