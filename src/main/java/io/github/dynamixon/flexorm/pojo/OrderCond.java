package io.github.dynamixon.flexorm.pojo;

public class OrderCond {
    private String orderByColumn;
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

    public String getOrderByColumn() {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn) {
        this.orderByColumn = orderByColumn;
    }

    public String getOrderByType() {
        return orderByType;
    }

    public void setOrderByType(String orderByType) {
        this.orderByType = orderByType;
    }
}
