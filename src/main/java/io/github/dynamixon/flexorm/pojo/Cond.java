package io.github.dynamixon.flexorm.pojo;

import io.github.dynamixon.flexorm.enums.CondAndOr;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by maojianfeng on 2/25/17.
 */
public class Cond {
    private String columnName;
    private FieldInfoGetter<?> fieldInfoGetter;
    private String compareOpr;
    private Object value;
    private Boolean ignoreNull;
    private InnerCond innerCond;
    private CondAndOr andOrToInner;

    public Cond() {
    }

    public Cond(String columnName, String compareOpr, Object value, Boolean ignoreNull, InnerCond innerCond,CondAndOr andOrToInner) {
        this.columnName = columnName;
        this.compareOpr = compareOpr;
        this.value = value;
        this.ignoreNull = ignoreNull;
        this.innerCond = innerCond;
        this.andOrToInner = andOrToInner;
    }

    public <T> Cond(FieldInfoGetter<T> fieldInfoGetter, String compareOpr, Object value, Boolean ignoreNull, InnerCond innerCond, CondAndOr andOrToInner) {
        this.fieldInfoGetter = fieldInfoGetter;
        this.compareOpr = compareOpr;
        this.value = value;
        this.ignoreNull = ignoreNull;
        this.innerCond = innerCond;
        this.andOrToInner = andOrToInner;
    }

    public Cond(String columnName, String compareOpr, Object value, Boolean ignoreNull) {
        this(columnName, compareOpr, value, ignoreNull, null, null);
    }

    public <T> Cond(FieldInfoGetter<T> fieldInfoGetter, String compareOpr, Object value, Boolean ignoreNull) {
        this(fieldInfoGetter, compareOpr, value, ignoreNull, null, null);
    }

    public Cond(String columnName, String compareOpr, Object value) {
        this(columnName, compareOpr, value, true);
    }

    public <T> Cond(FieldInfoGetter<T> fieldInfoGetter, String compareOpr, Object value) {
        this(fieldInfoGetter, compareOpr, value, true);
    }

    public Cond(String columnName, Object value) {
        this(columnName, "=", value, true);
    }

    public <T> Cond(FieldInfoGetter<T> fieldInfoGetter, Object value) {
        this(fieldInfoGetter, "=", value, true);
    }

    private Cond(Builder builder) {
        setColumnName(builder.columnName);
        setFieldInfoGetter(builder.fieldInfoGetter);
        setCompareOpr(builder.compareOpr);
        setValue(builder.value);
        setIgnoreNull(builder.ignoreNull);
        setInnerCond(builder.innerCond);
        setAndOrToInner(builder.andOrToInner);
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public FieldInfoGetter<?> getFieldInfoGetter() {
        return fieldInfoGetter;
    }

    public void setFieldInfoGetter(FieldInfoGetter<?> fieldInfoGetter) {
        this.fieldInfoGetter = fieldInfoGetter;
    }

    public String getCompareOpr() {
        return compareOpr;
    }

    public void setCompareOpr(String compareOpr) {
        this.compareOpr = compareOpr;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Boolean getIgnoreNull() {
        return ignoreNull;
    }

    public void setIgnoreNull(Boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
    }

    public InnerCond getInnerCond() {
        return innerCond;
    }

    public void setInnerCond(InnerCond innerCond) {
        this.innerCond = innerCond;
    }

    public CondAndOr getAndOrToInner() {
        return andOrToInner;
    }

    public void setAndOrToInner(CondAndOr andOrToInner) {
        this.andOrToInner = andOrToInner;
    }

    public boolean ignoreNull() {
        return getIgnoreNull() == null || BooleanUtils.isTrue(getIgnoreNull());
    }

    public boolean hasInnerConds() {
        return innerCond!=null&&CollectionUtils.isNotEmpty(innerCond.getInnerCondList());
    }

    public List<Cond> innerCondList(){
        return hasInnerConds()?innerCond.getInnerCondList(): Collections.emptyList();
    }

    public CondAndOr innerCondAndOr(){
        return innerCond==null||innerCond.getInnerCondAndOr()==null?CondAndOr.AND : innerCond.getInnerCondAndOr();
    }

    public CondAndOr andOrToInner(){
        return andOrToInner==null?CondAndOr.AND : andOrToInner;
    }

    public static Cond noValueCompare(String columnName, String compareOpr) {
        return new Cond(columnName, compareOpr, null, false);
    }

    @Override
    public String toString() {
        return "Cond{" +
            "columnName='" + columnName + '\'' +
            ", fieldInfoGetter=" + fieldInfoGetter +
            ", compareOpr='" + compareOpr + '\'' +
            ", value=" + value +
            ", ignoreNull=" + ignoreNull +
            ", innerCond=" + innerCond +
            ", andOrToInner=" + andOrToInner +
            '}';
    }

    public static final class Builder {
        private String columnName;
        private FieldInfoGetter<?> fieldInfoGetter;
        private String compareOpr;
        private Object value;
        private Boolean ignoreNull;
        private InnerCond innerCond;
        private CondAndOr andOrToInner;

        public Builder() {
        }

        public Builder columnName(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public Builder fieldInfoGetter(FieldInfoGetter<?> fieldInfoGetter) {
            this.fieldInfoGetter = fieldInfoGetter;
            return this;
        }

        public Builder compareOpr(String compareOpr) {
            this.compareOpr = compareOpr;
            return this;
        }

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        public Builder ignoreNull(Boolean ignoreNull) {
            this.ignoreNull = ignoreNull;
            return this;
        }

        public Builder innerCond(InnerCond innerCond) {
            this.innerCond = innerCond;
            return this;
        }

        public Builder andOrToInner(CondAndOr andOrToInner) {
            this.andOrToInner = andOrToInner;
            return this;
        }

        public Cond build() {
            Cond cond = new Cond();
            cond.setColumnName(columnName);
            cond.setFieldInfoGetter(fieldInfoGetter);
            cond.setCompareOpr(compareOpr);
            cond.setValue(value);
            cond.setIgnoreNull(ignoreNull);
            cond.setInnerCond(innerCond);
            cond.setAndOrToInner(andOrToInner);
            return cond;
        }
    }
}
