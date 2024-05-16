package io.github.dynamixon.flexorm.misc;


import io.github.dynamixon.flexorm.pojo.OrderCond;
import io.github.dynamixon.flexorm.pojo.Paginator;

import java.util.List;

/**
 * @author halflife3
 * @date 2019/9/21
 */
public class PagingInjector {

    public static void fillParam(Integer pageNo, Integer pageSize, boolean needCount, OrderCond... orderConds){
        ExtraParamInjector.paging(Paginator.paging(pageNo,pageSize,needCount,orderConds));
    }

    public static void offset(Integer offset, Integer limit, boolean needCount, OrderCond... orderConds){
        ExtraParamInjector.paging(Paginator.offset(offset,limit,needCount,orderConds));
    }

    public static Integer getOffset(){
        Paginator paginator = ExtraParamInjector.getPaginator();
        return paginator==null?null:paginator.getOffset();
    }

    public static Integer getLimit(){
        Paginator paginator = ExtraParamInjector.getPaginator();
        return paginator==null?null:paginator.getLimit();
    }

    public static boolean needCount(){
        Paginator paginator = ExtraParamInjector.getPaginator();
        return paginator!=null&&paginator.getNeedCount()!=null&&paginator.getNeedCount();
    }

    public static List<OrderCond> getOrderConds(){
        Paginator paginator = ExtraParamInjector.getPaginator();
        return paginator==null?null:paginator.getOrderConds();
    }

    public static Integer getCount(){
        return GeneralThreadLocal.get(DzConst.QUERY_COUNT);
    }

    public static void setCount(int count){
        Paginator paginator = ExtraParamInjector.getPaginator();
        if(paginator!=null){
            paginator.setTotalCount(count);
        }
        GeneralThreadLocal.set(DzConst.QUERY_COUNT,count);
    }

    public static void dropResult(){
        GeneralThreadLocal.unset(DzConst.QUERY_COUNT);
    }

    public static void unset(){
        GeneralThreadLocal.unset(DzConst.PAGINATOR);
    }
}
