package io.github.dynamixon.flexorm.misc;

/**
 * @author maojianfeng
 * @date 2021/6/15
 */
public interface DzConst {

    String WEAK_HASH_MAP_AS_CACHE = "weak.hash.map.as.cache";

    String NAMED_PARAM_SQL_PARSE_CACHE_COUNT = "named.param.sql.parse.cache.count";

    String FIELD_INFO_GETTER_CACHE_COUNT = "field.info.getter.cache.count";

    String PREFIX = "flexorm_";

    String IGNORE_LOG = PREFIX+"ignoreLog";

    /**
     * GeneralThreadLocal key to specify which columns to be returned from a query
     */
    String SELECT_COLUMNS = PREFIX+"selectColumns";

    /**
     * GeneralThreadLocal key to denote a sql statement
     */
    String SQL_ID = PREFIX+"sqlId";

    /**
     * GeneralThreadLocal key to add extra "and conditions" to a query
     */
    String EXTRA_CONDS = PREFIX+"extraConds";

    /**
     * GeneralThreadLocal key to add extra "or conditions" to a query
     */
    String EXTRA_OR_CONDS = PREFIX+"extraOrConds";

    /**
     * GeneralThreadLocal key to specify whether empty condition for an update action is allowed
     */
    String ALLOW_EMPTY_UPDATE_COND = PREFIX+"allowEmptyUpdateCond";

    /**
     * GeneralThreadLocal key to specify the offset of a query
     */
    String OFFSET = PREFIX+"offset";

    /**
     * GeneralThreadLocal key to specify the limit of a query
     */
    String LIMIT = PREFIX+"limit";

    /**
     * GeneralThreadLocal key to specify the limit of a query
     */
    String GROUP_BY_COLUMNS = PREFIX+"groupByColumns";

    /**
     * GeneralThreadLocal key to specify the having conditions
     * used with group by
     */
    String HAVING_CONDS = PREFIX+"havingConds";

    /**
     * GeneralThreadLocal key to specify whether the total record count is needed from an offset query
     */
    String NEED_COUNT = PREFIX+"needCount";

    /**
     * GeneralThreadLocal key to specify the total record count of an offset query
     */
    String QUERY_COUNT = PREFIX+"queryCount";

    /**
     * GeneralThreadLocal key to specify the order arrangement of a query
     */
    String ORDER_CONDS = PREFIX+"orderConds";

    String IGNORE_COLUMNS_FROM_COND_FOR_UPDATE = PREFIX+"ignoreColumnsFromCondForUpdate";

    /**
     * The returned result(or item from the result list) will be of this class
     */
    String RESULT_CLASS = PREFIX+"resultClass";

    /**
     * The Interceptor that intercepts SQL executions
     */
    String SQL_EXECUTION_INTERCEPTOR = PREFIX+"sqlExecutionInterceptor";

    /**
     * The Interceptor chain mode in which per execution interceptor cooperates with global execution interceptor
     */
    String SQL_EXECUTION_INTERCEPTOR_CHAIN_MODE = PREFIX+"sqlExecutionInterceptorChainMode";

    String MAIN_TABLE_ALIAS_FOR_JOIN = PREFIX+"mainTableAliasForJoin";

    String JOIN_INSTRUCTIONS = PREFIX+"joinInstructions";

}
