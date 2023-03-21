package io.github.dynamixon.flexorm.misc;


import io.github.dynamixon.flexorm.pojo.OrderCond;

import java.util.Arrays;
import java.util.List;

/**
 * @author halflife3
 * @date 2019/9/21
 */
public class PagingInjector {

    public static void fillParam(Integer pageNo, Integer pageSize, boolean needCount, OrderCond... orderConds){
        if(pageNo!=null&&pageSize!=null&&pageNo!=0&&pageSize!=0){
            int fromIndex = (pageNo - 1) * pageSize;
            offset(fromIndex,pageSize,needCount,orderConds);
        }
    }

    public static void offset(Integer offset, Integer limit, boolean needCount, OrderCond... orderConds){
        if(offset!=null&&limit!=null){
            GeneralThreadLocal.set(DzConst.OFFSET,offset);
            GeneralThreadLocal.set(DzConst.LIMIT,limit);
            GeneralThreadLocal.set(DzConst.NEED_COUNT,needCount);
        }
        if(orderConds!=null&&orderConds.length>0){
            List<OrderCond> conds = Arrays.asList(orderConds);
            GeneralThreadLocal.set(DzConst.ORDER_CONDS,conds);
        }
    }

    public static Integer getOffset(){
        return GeneralThreadLocal.get(DzConst.OFFSET);
    }

    public static Integer getLimit(){
        return GeneralThreadLocal.get(DzConst.LIMIT);
    }

    public static boolean needCount(){
        Boolean needCount = GeneralThreadLocal.get(DzConst.NEED_COUNT);
        return needCount!=null&&needCount;
    }

    public static List<OrderCond> getOrderConds(){
        return GeneralThreadLocal.get(DzConst.ORDER_CONDS);
    }

    public static Integer getCount(){
        return GeneralThreadLocal.get(DzConst.QUERY_COUNT);
    }

    public static void setCount(int count){
        GeneralThreadLocal.set(DzConst.QUERY_COUNT,count);
    }

    public static void dropResult(){
        GeneralThreadLocal.unset(DzConst.QUERY_COUNT);
    }

    public static void unSet(){
        GeneralThreadLocal.unset(DzConst.OFFSET);
        GeneralThreadLocal.unset(DzConst.LIMIT);
        GeneralThreadLocal.unset(DzConst.ORDER_CONDS);
        GeneralThreadLocal.unset(DzConst.NEED_COUNT);
    }
}
