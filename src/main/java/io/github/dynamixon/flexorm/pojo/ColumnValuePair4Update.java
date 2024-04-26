package io.github.dynamixon.flexorm.pojo;

/**
 * Created by maojianfeng on 2/17/17.
 */
public class ColumnValuePair4Update {
    private String column;
    private FieldInfoGetter<?> fieldInfoGetter;
    private Object value;
    private Boolean valueAsSqlPart;

    public ColumnValuePair4Update() {
    }

    public ColumnValuePair4Update(String column, Object value) {
        this.column = column;
        this.value = value;
        this.valueAsSqlPart = false;
    }

    public <T> ColumnValuePair4Update(FieldInfoGetter<T> fieldInfoGetter, Object value) {
        this.fieldInfoGetter = fieldInfoGetter;
        this.value = value;
    }

    public ColumnValuePair4Update(String column, Object value, Boolean valueAsSqlPart) {
        this.column = column;
        this.value = value;
        this.valueAsSqlPart = valueAsSqlPart;
    }

    public <T> ColumnValuePair4Update(FieldInfoGetter<T> fieldInfoGetter, Object value, Boolean valueAsSqlPart) {
        this.fieldInfoGetter = fieldInfoGetter;
        this.value = value;
        this.valueAsSqlPart = valueAsSqlPart;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public FieldInfoGetter<?> getFieldInfoGetter() {
        return fieldInfoGetter;
    }

    public void setFieldInfoGetter(FieldInfoGetter<?> fieldInfoGetter) {
        this.fieldInfoGetter = fieldInfoGetter;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Boolean getValueAsSqlPart() {
        return valueAsSqlPart;
    }

    public void setValueAsSqlPart(Boolean valueAsSqlPart) {
        this.valueAsSqlPart = valueAsSqlPart;
    }

    public boolean isSqlPartValue(){
        return valueAsSqlPart!=null && valueAsSqlPart;
    }
}
