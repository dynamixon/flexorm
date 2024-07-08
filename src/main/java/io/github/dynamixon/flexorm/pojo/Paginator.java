package io.github.dynamixon.flexorm.pojo;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jianfeng.Mao2
 * @date 24-4-11
 */
public class Paginator {
    private Integer offset;
    private Integer limit;
    private Boolean needCount;
    private List<OrderCond> orderConds;
    private Integer totalCount;

    public static Paginator paging(Integer pageNo, Integer pageSize, Boolean needCount, OrderCond ... orderConds){
        Integer offset = null;
        Integer limit = null;
        if(pageNo!=null&&pageSize!=null&&pageNo>0&&pageSize>0){
            offset = (pageNo - 1) * pageSize;
            limit = pageSize;
        }
        return offset(offset, limit,needCount,orderConds);
    }

    public static Paginator offset(Integer offset, Integer limit, Boolean needCount, OrderCond ... orderConds){
        List<OrderCond> orderCondList = null;
        if(orderConds!=null&&orderConds.length>0){
            orderCondList = Arrays.asList(orderConds);
        }
        return new Paginator(offset, limit,needCount,orderCondList);
    }

    public Paginator(Integer offset, Integer limit, Boolean needCount, List<OrderCond> orderConds) {
        this.offset = offset;
        this.limit = limit;
        this.needCount = needCount;
        this.orderConds = orderConds;
    }

    public Paginator() {}

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

    public Boolean getNeedCount() {
        return needCount;
    }

    public void setNeedCount(Boolean needCount) {
        this.needCount = needCount;
    }

    public List<OrderCond> getOrderConds() {
        return orderConds;
    }

    public void setOrderConds(List<OrderCond> orderConds) {
        this.orderConds = orderConds;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
