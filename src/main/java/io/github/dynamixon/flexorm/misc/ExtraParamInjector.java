package io.github.dynamixon.flexorm.misc;

import io.github.dynamixon.flexorm.enums.BatchInsertMode;
import io.github.dynamixon.flexorm.enums.SqlExecutionInterceptorChainMode;
import io.github.dynamixon.flexorm.pojo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author mjf
 * @date 2020/6/28
 */
public class ExtraParamInjector {

    public static class ParamPrep{}

    public static final ParamPrep paramPrep = new ParamPrep();

    public static void purgeExtraParam(){
        Map<String, Object> tlMap = GeneralThreadLocal.get();
        if(tlMap==null){
            return;
        }
        if(CollectionUtils.isEmpty(tlMap.keySet())){
            return;
        }
        Set<String> tlKeys = new HashSet<>(tlMap.keySet());
        tlKeys.forEach(tlKey->{
            if(tlKey.startsWith(DzConst.EXTRA_PARAM_PREFIX)){
                GeneralThreadLocal.unset(tlKey);
            }
        });
    }

    public static void turnOffLogging(){
        GeneralThreadLocal.set(DzConst.IGNORE_LOG, true);
    }
    public static void turnOnLogging(){
        GeneralThreadLocal.unset(DzConst.IGNORE_LOG);
    }

    public static ParamPrep paging(Integer pageNo, Integer pageSize, boolean needCount, OrderCond... orderConds){
        try {
            PagingInjector.fillParam(pageNo,pageSize,needCount,orderConds);
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep offset(Integer offset, Integer limit, boolean needCount, OrderCond... orderConds){
        try {
            PagingInjector.offset(offset,limit,needCount,orderConds);
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep order(OrderCond... orderConds){
        try {
            PagingInjector.offset(null,null,false,orderConds);
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep paging(Paginator paginator){
        try {
            GeneralThreadLocal.set(DzConst.PAGINATOR, paginator);
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep groupBy(String ... groupByColumns){
        try {
            if(groupByColumns!=null&&groupByColumns.length>0){
                GeneralThreadLocal.set(DzConst.GROUP_BY_COLUMNS, Arrays.asList(groupByColumns));
            }
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep having(Cond ... conds){
        try {
            if(conds!=null&&conds.length>0){
                GeneralThreadLocal.set(DzConst.HAVING_CONDS, Arrays.asList(conds));
            }
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep selectColumns(String ... selectColumns){
        try {
            if(selectColumns!=null&&selectColumns.length>0){
                GeneralThreadLocal.set(DzConst.SELECT_COLUMNS, Arrays.asList(selectColumns));
            }
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep excludeColumns(String ... excludedColumns){
        try {
            if(excludedColumns!=null&&excludedColumns.length>0){
                GeneralThreadLocal.set(DzConst.EXCLUDE_COLUMNS, Arrays.asList(excludedColumns));
            }
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep sqlId(String sqlId){
        try {
            if(StringUtils.isNotBlank(sqlId)){
                GeneralThreadLocal.set(DzConst.SQL_ID, sqlId);
            }
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep addCond(List<Cond> conds){
        try {
            if(CollectionUtils.isNotEmpty(conds)){
                GeneralThreadLocal.set(DzConst.EXTRA_CONDS, MiscUtil.combineList(getExtraConds(),conds));
            }
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep addCond(Cond ... conds){
        try {
            if(conds!=null&&conds.length>0){
                return addCond(Arrays.asList(conds));
            }
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep addOrCond(List<Cond> conds){
        try {
            if(CollectionUtils.isNotEmpty(conds)){
                GeneralThreadLocal.set(DzConst.EXTRA_OR_CONDS, MiscUtil.combineList(getExtraOrConds(), conds));
            }
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep addOrCond(Cond ... conds){
        try {
            if(conds!=null&&conds.length>0){
                return addOrCond(Arrays.asList(conds));
            }
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep addColumnValuePair4Update(List<ColumnValuePair4Update> columnValuePairs4Update){
        try {
            if(CollectionUtils.isNotEmpty(columnValuePairs4Update)){
                GeneralThreadLocal.set(DzConst.EXTRA_COLUMN_VALUE_PAIRS_4_UPDATE, MiscUtil.combineList(getExtraColumnValuePairs4Update(),columnValuePairs4Update));
            }
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep addColumnValuePair4Update(ColumnValuePair4Update ... columnValuePairs4Update){
        try {
            if(columnValuePairs4Update!=null&&columnValuePairs4Update.length>0){
                return addColumnValuePair4Update(Arrays.asList(columnValuePairs4Update));
            }
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep resultClass(Class<?> resultClass){
        try {
            if(resultClass!=null){
                GeneralThreadLocal.set(DzConst.RESULT_CLASS, resultClass);
            }
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep allowEmptyUpdateCond(){
        try {
            GeneralThreadLocal.set(DzConst.ALLOW_EMPTY_UPDATE_COND, true);
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep ignoreColumnsFromCondForUpdate(){
        try {
            GeneralThreadLocal.set(DzConst.IGNORE_COLUMNS_FROM_COND_FOR_UPDATE, true);
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep intercept(SqlExecutionInterceptor sqlExecutionInterceptor){
        return interceptWithChainMode(sqlExecutionInterceptor,SqlExecutionInterceptorChainMode.CHAIN_AFTER_GLOBAL);
    }

    public static ParamPrep interceptWithChainMode(SqlExecutionInterceptor sqlExecutionInterceptor, SqlExecutionInterceptorChainMode chainMode){
        try {
            GeneralThreadLocal.set(DzConst.SQL_EXECUTION_INTERCEPTOR, sqlExecutionInterceptor);
            GeneralThreadLocal.set(DzConst.SQL_EXECUTION_INTERCEPTOR_CHAIN_MODE, chainMode);
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep joinTable(String mainTableAlias, List<Join> joins){
        try {
            GeneralThreadLocal.set(DzConst.MAIN_TABLE_ALIAS_FOR_JOIN, mainTableAlias);
            if(CollectionUtils.isNotEmpty(joins)){
                //in corporate with Join(String joinMethod, String tableName, String tableAlias, String mainTableCol, String joinTableCol)
                joins.forEach(joinInstruction -> {
                    if(joinInstruction!=null){
                        List<Cond> joinConds = joinInstruction.getJoinConds();
                        if(CollectionUtils.isNotEmpty(joinConds)&&joinConds.size()==1){
                            Cond cond = joinConds.get(0);
                            String columnName = cond.getColumnName();
                            cond.setColumnName(columnName.replace(Join.MAIN_TABLE_ALIAS_PLACEHOLDER,mainTableAlias));
                        }
                    }
                });
            }
            GeneralThreadLocal.set(DzConst.JOINS, joins);
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep batchInsertMode(BatchInsertMode batchInsertMode){
        try {
            if(batchInsertMode!=null){
                GeneralThreadLocal.set(DzConst.BATCH_INSERT_MODE, batchInsertMode);
            }
        } catch (Throwable e) {
            purgeExtraParam();
            throw e;
        }
        return paramPrep;
    }

    public static ParamPrep jdbcBatchInsertMode(){
        return batchInsertMode(BatchInsertMode.JDBC_BATCH);
    }

    public static Paginator getPaginator(){
        return GeneralThreadLocal.get((DzConst.PAGINATOR));
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

    public static List<String> getExcludedColumns(){
        return GeneralThreadLocal.get(DzConst.EXCLUDE_COLUMNS);
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

    public static List<ColumnValuePair4Update> getExtraColumnValuePairs4Update(){
        return GeneralThreadLocal.get(DzConst.EXTRA_COLUMN_VALUE_PAIRS_4_UPDATE);
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

    public static List<Join> getJoins(){
        return GeneralThreadLocal.get(DzConst.JOINS);
    }

    public static BatchInsertMode getBatchInsertMode(){
        return GeneralThreadLocal.get(DzConst.BATCH_INSERT_MODE);
    }

    public static void unsetForQuery(){
        PagingInjector.unset();
        GeneralThreadLocal.unset(DzConst.SELECT_COLUMNS);
        GeneralThreadLocal.unset(DzConst.EXCLUDE_COLUMNS);
        GeneralThreadLocal.unset(DzConst.GROUP_BY_COLUMNS);
        GeneralThreadLocal.unset(DzConst.HAVING_CONDS);
        GeneralThreadLocal.unset(DzConst.RESULT_CLASS);
        GeneralThreadLocal.unset(DzConst.MAIN_TABLE_ALIAS_FOR_JOIN);
        GeneralThreadLocal.unset(DzConst.JOINS);
        unsetExtraConds();
        unsetExtraOrConds();
    }

    public static void unsetForUpdate(){
        unsetExtraConds();
        unsetExtraOrConds();
        unsetExtraColumnValuePairs4Update();
        unsetEmptyUpdateCondRestriction();
        unsetIgnoreColumnsFromCondForUpdate();

        unsetSqlId();
        unsetInterceptor();
        unsetInterceptorChainMode();
    }

    public static void unsetForDel(){
        unsetExtraConds();
        unsetExtraOrConds();
        unsetEmptyUpdateCondRestriction();

        unsetSqlId();
        unsetInterceptor();
        unsetInterceptorChainMode();
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

    public static void unsetExtraColumnValuePairs4Update(){
        GeneralThreadLocal.unset(DzConst.EXTRA_COLUMN_VALUE_PAIRS_4_UPDATE);
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

    public static void unsetBatchInsertMode(){
        GeneralThreadLocal.unset(DzConst.BATCH_INSERT_MODE);
    }
}
