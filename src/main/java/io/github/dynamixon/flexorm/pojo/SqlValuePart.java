package io.github.dynamixon.flexorm.pojo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SqlValuePart {
    private String sqlPart;

    private List<Object> valueParts;

    public SqlValuePart() {
    }

    public SqlValuePart(String sqlPart, List<Object> valueParts) {
        this.sqlPart = sqlPart;
        this.valueParts = valueParts;
    }

    public String getSqlPart() {
        return sqlPart;
    }

    public void setSqlPart(String sqlPart) {
        this.sqlPart = sqlPart;
    }

    public List<Object> getValueParts() {
        return valueParts;
    }

    public void setValueParts(List<Object> valueParts) {
        this.valueParts = valueParts;
    }

    public boolean valid(){
        return StringUtils.isNotBlank(sqlPart) && CollectionUtils.isNotEmpty(valueParts);
    }

    @Override
    public String toString() {
        return "SqlValuePart{" +
            "sqlPart='" + sqlPart + '\'' +
            ", valueParts=" + valueParts +
            '}';
    }
}
