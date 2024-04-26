package io.github.dynamixon.flexorm.pojo;

public class OrderCond {
    private String orderByColumn;
    private FieldInfoGetter<?> fieldInfoGetter;
    private String orderByType;

    public OrderCond() {}

    public OrderCond(String orderByColumn) {
        this.orderByColumn = orderByColumn;
        this.orderByType = "asc";
    }

    public OrderCond(String orderByColumn, String orderByType) {
        this.orderByColumn = orderByColumn;
        this.orderByType = orderByType;
    }

    public <T> OrderCond(FieldInfoGetter<T> fieldInfoGetter, String orderByType) {
        this.fieldInfoGetter = fieldInfoGetter;
        this.orderByType = orderByType;
    }

    public String getOrderByColumn() {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn) {
        this.orderByColumn = orderByColumn;
    }

    public FieldInfoGetter<?> getFieldInfoGetter() {
        return fieldInfoGetter;
    }

    public void setFieldInfoGetter(FieldInfoGetter<?> fieldInfoGetter) {
        this.fieldInfoGetter = fieldInfoGetter;
    }

    public String getOrderByType() {
        return orderByType;
    }

    public void setOrderByType(String orderByType) {
        this.orderByType = orderByType;
    }
}
