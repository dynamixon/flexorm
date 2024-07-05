package io.github.dynamixon.flexorm.misc;

import io.github.dynamixon.flexorm.pojo.Paginator;

/**
 * @author maojianfeng
 * @date 2021/6/15
 */
public interface DzConst {

    String WEAK_HASH_MAP_AS_CACHE = "weak.hash.map.as.cache";

    String NAMED_PARAM_SQL_PARSE_CACHE_COUNT = "named.param.sql.parse.cache.count";

    String FIELD_INFO_GETTER_CACHE_COUNT = "field.info.getter.cache.count";

    String IGNORE_LOG = "flexorm_ignoreLog";

    String EXTRA_PARAM_PREFIX = "flexorm_extra_param_";

    /**
     * GeneralThreadLocal key to specify which columns to be returned from a query
     */
    String SELECT_COLUMNS = EXTRA_PARAM_PREFIX +"selectColumns";

    /**
     * GeneralThreadLocal key to specify which columns to be excluded from a query
     */
    String EXCLUDE_COLUMNS = EXTRA_PARAM_PREFIX +"excludeColumns";

    /**
     * GeneralThreadLocal key to denote a sql statement
     */
    String SQL_ID = EXTRA_PARAM_PREFIX +"sqlId";

    /**
     * GeneralThreadLocal key to add extra "and conditions" to a query
     */
    String EXTRA_CONDS = EXTRA_PARAM_PREFIX +"extraConds";

    /**
     * GeneralThreadLocal key to add extra "or conditions" to a query
     */
    String EXTRA_OR_CONDS = EXTRA_PARAM_PREFIX +"extraOrConds";

    String EXTRA_COLUMN_VALUE_PAIRS_4_UPDATE = EXTRA_PARAM_PREFIX +"extraColumnValuePairs4Update";

    /**
     * GeneralThreadLocal key to specify whether empty condition for an update action is allowed
     */
    String ALLOW_EMPTY_UPDATE_COND = EXTRA_PARAM_PREFIX +"allowEmptyUpdateCond";

    /**
     * GeneralThreadLocal key to specify the offset of a query
     * @see Paginator
     */
    @Deprecated
    String OFFSET = EXTRA_PARAM_PREFIX +"offset";

    /**
     * GeneralThreadLocal key to specify the limit of a query
     * @see Paginator
     */
    @Deprecated
    String LIMIT = EXTRA_PARAM_PREFIX +"limit";

    /**
     * GeneralThreadLocal key to specify the limit of a query
     */
    String GROUP_BY_COLUMNS = EXTRA_PARAM_PREFIX +"groupByColumns";

    /**
     * GeneralThreadLocal key to specify the having conditions
     * used with group by
     */
    String HAVING_CONDS = EXTRA_PARAM_PREFIX +"havingConds";

    /**
     * GeneralThreadLocal key to specify whether the total record count is needed from an offset query
     * @see Paginator
     */
    @Deprecated
    String NEED_COUNT = EXTRA_PARAM_PREFIX +"needCount";

    /**
     * GeneralThreadLocal key to specify the total record count of an offset query
     */
    String QUERY_COUNT = EXTRA_PARAM_PREFIX +"queryCount";

    /**
     * GeneralThreadLocal key to specify the paging instruction of an offset query
     */
    String PAGINATOR = EXTRA_PARAM_PREFIX +"paginator";

    /**
     * GeneralThreadLocal key to specify the order arrangement of a query
     * @see Paginator
     */
    @Deprecated
    String ORDER_CONDS = EXTRA_PARAM_PREFIX +"orderConds";

    String IGNORE_COLUMNS_FROM_COND_FOR_UPDATE = EXTRA_PARAM_PREFIX +"ignoreColumnsFromCondForUpdate";

    /**
     * The returned result(or item from the result list) will be of this class
     */
    String RESULT_CLASS = EXTRA_PARAM_PREFIX +"resultClass";

    /**
     * The Interceptor that intercepts SQL executions
     */
    String SQL_EXECUTION_INTERCEPTOR = EXTRA_PARAM_PREFIX +"sqlExecutionInterceptor";

    /**
     * The Interceptor chain mode in which per execution interceptor cooperates with global execution interceptor
     */
    String SQL_EXECUTION_INTERCEPTOR_CHAIN_MODE = EXTRA_PARAM_PREFIX +"sqlExecutionInterceptorChainMode";

    String MAIN_TABLE_ALIAS_FOR_JOIN = EXTRA_PARAM_PREFIX +"mainTableAliasForJoin";

    String JOINS = EXTRA_PARAM_PREFIX +"joins";

    String BATCH_INSERT_MODE = EXTRA_PARAM_PREFIX +"batchInsertMode";

}
