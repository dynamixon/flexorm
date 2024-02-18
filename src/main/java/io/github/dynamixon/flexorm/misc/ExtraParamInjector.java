package io.github.dynamixon.flexorm.misc;

import io.github.dynamixon.flexorm.enums.SqlExecutionInterceptorChainMode;
import io.github.dynamixon.flexorm.pojo.Cond;
import io.github.dynamixon.flexorm.pojo.JoinInstruction;
import io.github.dynamixon.flexorm.pojo.OrderCond;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author mjf
 * @date 2020/6/28
 */
public class ExtraParamInjector {

    public static class ParamPrep{}

    public static final ParamPrep paramPrep = new ParamPrep();

    public static ParamPrep paging(Integer pageNo, Integer pageSize, boolean needCount, OrderCond... orderConds){
        PagingInjector.fillParam(pageNo,pageSize,needCount,orderConds);
        return paramPrep;
    }

    public static ParamPrep offset(Integer offset, Integer limit, boolean needCount, OrderCond... orderConds){
        PagingInjector.offset(offset,limit,needCount,orderConds);
        return paramPrep;
    }

    public static ParamPrep order(OrderCond... orderConds){
        PagingInjector.offset(null,null,false,orderConds);
        return paramPrep;
    }

    public static ParamPrep groupBy(String ... groupByColumns){
        if(groupByColumns!=null&&groupByColumns.length>0){
            GeneralThreadLocal.set(DzConst.GROUP_BY_COLUMNS, Arrays.asList(groupByColumns));
        }
        return paramPrep;
    }

    public static ParamPrep having(Cond ... conds){
        if(conds!=null&&conds.length>0){
            GeneralThreadLocal.set(DzConst.HAVING_CONDS, Arrays.asList(conds));
        }
        return paramPrep;
    }

    public static ParamPrep selectColumns(String ... selectColumns){
        if(selectColumns!=null&&selectColumns.length>0){
            GeneralThreadLocal.set(DzConst.SELECT_COLUMNS, Arrays.asList(selectColumns));
        }
        return paramPrep;
    }

    public static ParamPrep sqlId(String sqlId){
        if(StringUtils.isNotBlank(sqlId)){
            GeneralThreadLocal.set(DzConst.SQL_ID, sqlId);
        }
        return paramPrep;
    }

    public static ParamPrep addCond(List<Cond> conds){
        if(CollectionUtils.isNotEmpty(conds)){
            conds = Stream.of(getExtraConds(), conds)
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
            GeneralThreadLocal.set(DzConst.EXTRA_CONDS, conds);
        }
        return paramPrep;
    }

    public static ParamPrep addOrCond(List<Cond> conds){
        if(CollectionUtils.isNotEmpty(conds)){
            conds = Stream.of(getExtraOrConds(), conds)
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
            GeneralThreadLocal.set(DzConst.EXTRA_OR_CONDS, conds);
        }
        return paramPrep;
    }

    public static ParamPrep resultClass(Class<?> resultClass){
        if(resultClass!=null){
            GeneralThreadLocal.set(DzConst.RESULT_CLASS, resultClass);
        }
        return paramPrep;
    }

    public static ParamPrep allowEmptyUpdateCond(){
        GeneralThreadLocal.set(DzConst.ALLOW_EMPTY_UPDATE_COND, true);
        return paramPrep;
    }

    public static ParamPrep ignoreColumnsFromCondForUpdate(){
        GeneralThreadLocal.set(DzConst.IGNORE_COLUMNS_FROM_COND_FOR_UPDATE, true);
        return paramPrep;
    }

    public static ParamPrep intercept(SqlExecutionInterceptor sqlExecutionInterceptor){
        return interceptWithChainMode(sqlExecutionInterceptor,SqlExecutionInterceptorChainMode.CHAIN_AFTER_GLOBAL);
    }

    public static ParamPrep interceptWithChainMode(SqlExecutionInterceptor sqlExecutionInterceptor, SqlExecutionInterceptorChainMode chainMode){
        GeneralThreadLocal.set(DzConst.SQL_EXECUTION_INTERCEPTOR, sqlExecutionInterceptor);
        GeneralThreadLocal.set(DzConst.SQL_EXECUTION_INTERCEPTOR_CHAIN_MODE, chainMode);
        return paramPrep;
    }

    public static ParamPrep joinTable(String mainTableAlias, List<JoinInstruction> joinInstructions){
        GeneralThreadLocal.set(DzConst.MAIN_TABLE_ALIAS_FOR_JOIN, mainTableAlias);
        if(CollectionUtils.isNotEmpty(joinInstructions)){
            //in corporate with JoinInstruction(String tableName,String tableAlias, String mainTableCol, String joinTableCol)
            joinInstructions.forEach(joinInstruction -> {
                if(joinInstruction!=null){
                    List<Cond> joinConds = joinInstruction.getJoinConds();
                    if(CollectionUtils.isNotEmpty(joinConds)&&joinConds.size()==1){
                        Cond cond = joinConds.get(0);
                        String columnName = cond.getColumnName();
                        cond.setColumnName(columnName.replace(JoinInstruction.MAIN_TABLE_ALIAS_PLACEHOLDER,mainTableAlias));
                    }
                }
            });
        }
        GeneralThreadLocal.set(DzConst.JOIN_INSTRUCTIONS, joinInstructions);
        return paramPrep;
    }

    public static Integer getTotalCount(){
        return PagingInjector.getCount();
    }

    public static String getSqlId(){
        return GeneralThreadLocal.get(DzConst.SQL_ID);
    }

    public static List<String> getSelectColumns(){
        return GeneralThreadLocal.get(DzConst.SELECT_COLUMNS);
    }

    public static List<String> getGroupByColumns(){
        return GeneralThreadLocal.get(DzConst.GROUP_BY_COLUMNS);
    }

    public static List<Cond> getHavingConds(){
        return GeneralThreadLocal.get(DzConst.HAVING_CONDS);
    }

    public static List<Cond> getExtraConds(){
        return GeneralThreadLocal.get(DzConst.EXTRA_CONDS);
    }

    public static List<Cond> getExtraOrConds(){
        return GeneralThreadLocal.get(DzConst.EXTRA_OR_CONDS);
    }

    public static boolean emptyUpdateCondAllowed(){
        Boolean allowEmptyUpdateCond = GeneralThreadLocal.get(DzConst.ALLOW_EMPTY_UPDATE_COND);
        return allowEmptyUpdateCond!=null&&allowEmptyUpdateCond;
    }

    public static boolean columnsFromCondIgnoredForUpdate(){
        Boolean ignoreColumnFromCondForUpdate = GeneralThreadLocal.get(DzConst.IGNORE_COLUMNS_FROM_COND_FOR_UPDATE);
        return ignoreColumnFromCondForUpdate!=null&&ignoreColumnFromCondForUpdate;
    }

    public static Class<?> getResultClass(){
        return GeneralThreadLocal.get(DzConst.RESULT_CLASS);
    }

    public static SqlExecutionInterceptor getSqlInterceptor(){
        return GeneralThreadLocal.get(DzConst.SQL_EXECUTION_INTERCEPTOR);
    }

    public static SqlExecutionInterceptorChainMode getSqlInterceptorChainMode(){
        return GeneralThreadLocal.get(DzConst.SQL_EXECUTION_INTERCEPTOR_CHAIN_MODE);
    }

    public static String getMainTableAlias(){
        return GeneralThreadLocal.get(DzConst.MAIN_TABLE_ALIAS_FOR_JOIN);
    }

    public static List<JoinInstruction> getJoinInstructions(){
        return GeneralThreadLocal.get(DzConst.JOIN_INSTRUCTIONS);
    }

    public static void unSetForQuery(){
        PagingInjector.unSet();
        GeneralThreadLocal.unset(DzConst.SELECT_COLUMNS);
        GeneralThreadLocal.unset(DzConst.GROUP_BY_COLUMNS);
        GeneralThreadLocal.unset(DzConst.HAVING_CONDS);
        GeneralThreadLocal.unset(DzConst.RESULT_CLASS);
        GeneralThreadLocal.unset(DzConst.MAIN_TABLE_ALIAS_FOR_JOIN);
        GeneralThreadLocal.unset(DzConst.JOIN_INSTRUCTIONS);
        unsetExtraConds();
        unsetExtraOrConds();
    }

    public static void unsetSqlId(){
        GeneralThreadLocal.unset(DzConst.SQL_ID);
    }

    public static void unsetInterceptor(){
        GeneralThreadLocal.unset(DzConst.SQL_EXECUTION_INTERCEPTOR);
    }

    public static void unsetInterceptorChainMode(){
        GeneralThreadLocal.unset(DzConst.SQL_EXECUTION_INTERCEPTOR_CHAIN_MODE);
    }

    public static void unsetExtraConds(){
        GeneralThreadLocal.unset(DzConst.EXTRA_CONDS);
    }

    public static void unsetExtraOrConds(){
        GeneralThreadLocal.unset(DzConst.EXTRA_OR_CONDS);
    }

    public static void unsetEmptyUpdateCondRestriction(){
        GeneralThreadLocal.unset(DzConst.ALLOW_EMPTY_UPDATE_COND);
    }

    public static void unsetIgnoreColumnsFromCondForUpdate(){
        GeneralThreadLocal.unset(DzConst.IGNORE_COLUMNS_FROM_COND_FOR_UPDATE);
    }
}
