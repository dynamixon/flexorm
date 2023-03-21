package io.github.dynamixon.flexorm.pojo;

public class OrderCond {
    private String orderByField;
    private String orderByType;

    public OrderCond() {}

    public OrderCond(String orderByField) {
        this.orderByField = orderByField;
        this.orderByType = "asc";
    }

    public OrderCond(String orderByField, String orderByType) {
        this.orderByField = orderByField;
        this.orderByType = orderByType;
    }

    public String getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(String orderByField) {
        this.orderByField = orderByField;
    }

    public String getOrderByType() {
        return orderByType;
    }

    public void setOrderByType(String orderByType) {
        this.orderByType = orderByType;
    }
}
