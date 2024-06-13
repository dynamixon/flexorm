package io.github.dynamixon.flexorm;

import com.google.common.collect.Lists;
import io.github.dynamixon.flexorm.enums.SqlExecutionInterceptorChainMode;
import io.github.dynamixon.flexorm.logic.TableLoc;
import io.github.dynamixon.flexorm.logic.TableObjectMetaCache;
import io.github.dynamixon.flexorm.logic.namedsql.NamedParamSqlUtil;
import io.github.dynamixon.flexorm.misc.*;
import io.github.dynamixon.flexorm.pojo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QueryEntry {
    private final CoreRunner coreRunner;

    public QueryEntry(DataSource ds) {
        this(ds,null);
    }

    public QueryEntry(DataSource ds, String dbType) {
        this(ds,dbType,null);
    }

    public QueryEntry(DataSource ds, String dbType,Config config) {
        this.coreRunner = new CoreRunner(new QueryRunner(ds), dbType,config);
    }

    public QueryEntry(QueryRunner qr) {
        this(qr,null);
    }

    public QueryEntry(QueryRunner qr, String dbType) {
        this.coreRunner = new CoreRunner(qr, dbType);
    }

    public QueryEntry(CoreRunner coreRunner) {
        this.coreRunner = coreRunner;
    }

    public CoreRunner getCoreRunner() {
        return coreRunner;
    }

    public QueryRunner getQueryRunner() {
        return coreRunner.getQueryRunner();
    }

    public DataSource getDataSource() {
        return coreRunner.getDataSource();
    }

    public String getDbType() {
        return coreRunner.getDialectType();
    }

    public String getDialectType() {
        return coreRunner.getDialectType();
    }

    public void setConfig(Config config){
        if(coreRunner==null){
            throw new IllegalStateException("CoreRunner hasn't been initialized!");
        }
        coreRunner.setConfig(config);
    }

    public static QueryEntry initQueryEntry(DataSource ds, Config config){
        return initQueryEntry(ds,null,config);
    }

    public static QueryEntry initQueryEntry(DataSource ds, String dbType, Config config){
        return new QueryEntry(ds,dbType,config);
    }

    public QueryEntry prep(ExtraParamInjector.ParamPrep ... prep){
        return this;
    }

    public List<String> getColNames(Class<?> clazz) {
        return getColNames(TableLoc.findTableName(clazz,getDataSource()));
    }

    public List<String> getColNames(String table) {
        return coreRunner.getColNames(table);
    }

    public Map<String, String> getTableMetas() {
        return coreRunner.getTableMetas();
    }

    public void refreshTableMetaCache(Class<?> clazz){
        TableObjectMetaCache.refreshCache(clazz,this);
    }

    public void refreshTableMetaCacheMulti(Class<?> ... classes){
        if(classes==null){
            return;
        }
        Arrays.asList(classes).forEach(this::refreshTableMetaCache);
    }

    public void refreshTableMetaCacheForPackage(String packageName, Class<?> ... excludedClasses){
        Set<Class<?>> classes = TableLoc.tableClasses(packageName,getDataSource());
        if(CollectionUtils.isEmpty(classes)){
            return;
        }
        if(excludedClasses!=null){
            classes.removeIf(clazz->Arrays.asList(excludedClasses).contains(clazz));
        }
        classes.forEach(this::refreshTableMetaCache);
    }

    public void removeCache(Class<?> tableClass){
        TableObjectMetaCache.removeCache(tableClass,getDataSource());
    }

    public void removeCacheAll(){
        TableObjectMetaCache.removeCacheAll(getDataSource());
    }

    public void registerTableObjectMeta(Class<?> clazz, String tableName){
        registerTableObjectMeta(false, clazz, tableName);
    }

    public void registerTableObjectMeta(boolean overwrite, Class<?> clazz, String tableName){
        TableObjectMetaCache.registerTableObjectMeta(overwrite, clazz, coreRunner, tableName);
    }

    public <T> List<T> genericQry(String sql, Class<T> clazz, Object... values) {
        return coreRunner.genericQry(sql, clazz, values);
    }

    public <T> List<T> genericNamedParamQry(String namedParamSql, Class<T> clazz, Map<String,?> paramMap){
        SqlPreparedBundle sqlPreparedBundle = NamedParamSqlUtil.fromNamed(namedParamSql, paramMap);
        return genericQry(sqlPreparedBundle.getSql(),clazz,sqlPreparedBundle.getValues());
    }

    public List<Map<String, Object>> genericQry(String sql, Object... values) {
        return coreRunner.genericQry(sql, values);
    }

    public List<Map<String, Object>> genericNamedParamQry(String namedParamSql, Map<String,?> paramMap){
        SqlPreparedBundle sqlPreparedBundle = NamedParamSqlUtil.fromNamed(namedParamSql, paramMap);
        return genericQry(sqlPreparedBundle.getSql(),sqlPreparedBundle.getValues());
    }

    public <T> T genericQry(String sql, ResultSetHandler<T> resultSetHandler, Object... values) {
        return coreRunner.genericQry(sql, resultSetHandler, values);
    }

    public <T> T genericNamedParamQry(String namedParamSql, ResultSetHandler<T> resultSetHandler, Map<String,?> paramMap){
        SqlPreparedBundle sqlPreparedBundle = NamedParamSqlUtil.fromNamed(namedParamSql, paramMap);
        return genericQry(sqlPreparedBundle.getSql(),resultSetHandler,sqlPreparedBundle.getValues());
    }

    public int genericUpdate(String sql, Object... values) {
        return coreRunner.genericUpdate(sql, values);
    }

    public int genericNamedParamUpdate(String namedParamSql, Map<String,?> paramMap){
        SqlPreparedBundle sqlPreparedBundle = NamedParamSqlUtil.fromNamed(namedParamSql, paramMap);
        return genericUpdate(sqlPreparedBundle.getSql(),sqlPreparedBundle.getValues());
    }

    public int delObjects(String table, List<Cond> conds) {
        try {
            ConditionBundle delCond = new ConditionBundle.Builder()
                .targetTable(table)
                .conditionAndList(combineConds(conds, ExtraParamInjector.getExtraConds()))
                .conditionOrList(ExtraParamInjector.getExtraOrConds())
                .build();
            SqlPreparedBundle sqlPreparedBundle = coreRunner.getSqlBuilder().composeDelete(delCond);
            if(!sqlPreparedBundle.isWithCondition()&&!ExtraParamInjector.emptyUpdateCondAllowed()){
                throw new IllegalArgumentException("Delete without condition! sql:"+sqlPreparedBundle.getSql()+", This restriction can be suppressed by ExtraParamInjector.allowEmptyUpdateCond()");
            }
            return coreRunner.genericUpdate(sqlPreparedBundle.getSql(), sqlPreparedBundle.getValues());
        } finally {
            ExtraParamInjector.unsetForDel();
        }
    }

    public <E> int delObjects(Class<?> clazz, CondCrafter<E> condCrafter, E primalCond) {
        return delObjects(TableLoc.findTableName(clazz,getDataSource()), condCrafter.craft(primalCond));
    }

    public int delObjects(Object obj) {
        if(obj instanceof Class){
            return delObjects((Class<?>) obj,new Cond[0]);
        }
        return delObjects(obj.getClass(), this::fromTableDomain, obj);
    }

    public int delObjectsByTable(String table, Cond... conds) {
        return delObjects(table, Arrays.asList(conds));
    }

    public int delObjects(Class<?> clazz, final Cond... conds) {
        return delObjects(clazz, Arrays::asList, conds);
    }

    @SuppressWarnings({"unchecked"})
    public <T> List<T> genericQry(QueryConditionBundle qryCondition) {
        Class<?> resultClass = qryCondition.getResultClass();
        SqlPreparedBundle sqlPreparedBundle = coreRunner.getSqlBuilder().composeSelect(qryCondition);
        String sql = sqlPreparedBundle.getSql();
        Object[] values = sqlPreparedBundle.getValues();
        if (resultClass.equals(Map.class)) {
            return (List<T>) genericQry(sql, values);
        } else {
            return genericQry(sql, (Class<T>)resultClass, values);
        }
    }

    public <T> List<T> findObjectsT(String table, List<Cond> conds, Class<T> clazz) {
        return findObjects(table,conds,clazz);
    }
    public <T> List<T> findObjects(String table, List<Cond> conds, Class<?> clazz) {
        List<T> rtList;
        try {
            String sqlId = ExtraParamInjector.getSqlId();
            SqlExecutionInterceptor sqlExecutionInterceptor = ExtraParamInjector.getSqlInterceptor();
            SqlExecutionInterceptorChainMode sqlInterceptorChainMode = ExtraParamInjector.getSqlInterceptorChainMode();
            boolean interceptorSpan = sqlExecutionInterceptor !=null&& sqlExecutionInterceptor.spanWithin();
            PagingInjector.dropResult();
            Class<?> resultClass = ExtraParamInjector.getResultClass();
            QueryConditionBundle qryCondition = new QueryConditionBundle.Builder()
                .tableClass(clazz)
                .resultClass(resultClass==null?clazz:resultClass)
                .targetTable(table)
                .conditionAndList(combineConds(conds, ExtraParamInjector.getExtraConds()))
                .conditionOrList(ExtraParamInjector.getExtraOrConds())
                .selectColumns(ExtraParamInjector.getSelectColumns())
                .excludedColumns(ExtraParamInjector.getExcludedColumns())
                .tableAliasForJoin(ExtraParamInjector.getMainTableAlias())
                .joins(ExtraParamInjector.getJoins())
                .groupByColumns(ExtraParamInjector.getGroupByColumns())
                .havingConds(ExtraParamInjector.getHavingConds())
                .offset(PagingInjector.getOffset())
                .limit(PagingInjector.getLimit())
                .orderConds(PagingInjector.getOrderConds())
                .build();
            rtList = genericQry(qryCondition);
            rtList = coreRunner.getOfflinePagination().paginate(rtList, qryCondition.getOffset(), qryCondition.getLimit());
            if (PagingInjector.needCount()) {
                ExtraParamInjector.sqlId(sqlId);
                if(interceptorSpan){
                    ExtraParamInjector.interceptWithChainMode(sqlExecutionInterceptor,sqlInterceptorChainMode!=null?sqlInterceptorChainMode:SqlExecutionInterceptorChainMode.CHAIN_AFTER_GLOBAL);
                }
//                if(CollectionUtils.isNotEmpty(groupByColumns)){
//                    qryCondition.setOffset(null);
//                    qryCondition.setLimit(null);
//                    qryCondition.setOrderConds(null);
//                    SqlPreparedBundle sqlPreparedBundle = coreRunner.getSqlBuilder().composeSelect(qryCondition);
//                    PagingInjector.setCount(coreRunner.genericCount(sqlPreparedBundle.getSql(),sqlPreparedBundle.getValues()));
//                }else {
//                    QueryConditionBundle qcCount = new QueryConditionBundle.Builder()
//                        .targetTable(qryCondition.getTargetTable())
//                        .onlyCount(true)
//                        .tableClass(qryCondition.getTableClass())
//                        .tableAliasForJoin(qryCondition.getTableAliasForJoin())
//                        .joins(qryCondition.getJoins())
//                        .resultClass(CountInfo.class)
//                        .conditionAndList(qryCondition.getConditionAndList())
//                        .conditionOrList(qryCondition.getConditionOrList())
//                        .build();
//                    List<CountInfo> counts = genericQry(qcCount);
//                    PagingInjector.setCount(counts.get(0).getCount());
//                }
                PagingInjector.setCount(countInner(table,conds,clazz,false));
            }
        } finally {
            ExtraParamInjector.unsetForQuery();
        }
        return rtList;
    }

    public <T, E> List<T> findObjects(Class<?> clazz, CondCrafter<E> condCrafter, E primalCond) {
        return findObjects(TableLoc.findTableName(clazz,getDataSource()), condCrafter.craft(primalCond), clazz);
    }

    public <T> List<T> findObjectsT(List<Cond> conds, Class<T> clazz) {
        return findObjects(conds,clazz);
    }
    public <T> List<T> findObjects(List<Cond> conds, Class<?> clazz) {
        return findObjects(clazz, conds);
    }

    public <T> List<T> findObjectsT(Class<T> clazz, List<Cond> conds) {
        return findObjects(clazz, conds);
    }
    public <T> List<T> findObjects(Class<?> clazz, List<Cond> conds) {
        return findObjects(clazz, (primalCond) -> primalCond, conds);
    }

    public <T> List<T> findObjectsT(Class<T> clazz, Cond... conds) {
        return findObjects(clazz,conds);
    }
    public <T> List<T> findObjects(Class<?> clazz, Cond... conds) {
        return findObjects(clazz, Arrays::asList, conds);
    }

    public <T> T findObjectT(List<Cond> conds, Class<T> clazz) {
        return findObject(conds,clazz);
    }
    public <T> T findObject(List<Cond> conds, Class<?> clazz) {
        return findObject(clazz, conds);
    }

    public <T> T findObjectT(Class<T> clazz, List<Cond> conds) {
        return findObject(clazz, conds);
    }
    public <T> T findObject(Class<?> clazz, List<Cond> conds) {
        tryLimitOne();
        return MiscUtil.getFirst(findObjects(clazz, (primalCond) -> primalCond, conds));
    }

    public <T> T findObjectT(String table, List<Cond> conds, Class<T> clazz) {
        return findObject(table,conds,clazz);
    }
    public <T> T findObject(String table, List<Cond> conds, Class<?> clazz) {
        tryLimitOne();
        return MiscUtil.getFirst(findObjects(table, conds, clazz));
    }

    public <T> T findObjectT(Class<T> clazz, Cond... conds) {
        return findObject(clazz,conds);
    }
    public <T> T findObject(Class<?> clazz, Cond... conds) {
        return findObject(TableLoc.findTableName(clazz,getDataSource()), Arrays.asList(conds), clazz);
    }

    public <T> T findOneValue(String table, String columnName, List<Cond> conds){
        Map<String,Object> record = findObject(table,conds,Map.class);
        return (record==null||record.get(columnName)==null)?null:(T)record.get(columnName);
    }

    public <T> T findOneValue(Class<?> clazz, String columnOrFieldName, List<Cond> conds){
        return findOneValue(TableLoc.findTableName(clazz,getDataSource()),determineColumnName(clazz,columnOrFieldName),conds);
    }

    public <T> T findOneValue(Class<?> clazz, String columnOrFieldName, Cond... conds){
        return findOneValue(clazz, columnOrFieldName, Arrays.asList(conds));
    }

    public <T> List<T> searchObjectsT(T obj) {
        return searchObjects(obj);
    }
    public <T> List<T> searchObjects(Object obj) {
        List<Cond> conds = fromTableDomain(obj);
        Class<?> clazz = obj.getClass();
        return findObjects(clazz, conds);
    }

    public <T> T searchObjectT(T obj) {
        return searchObject(obj);
    }
    public <T> T searchObject(Object obj) {
        tryLimitOne();
        return MiscUtil.getFirst(searchObjects(obj));
    }

    public int insert(String table, Map<String, Object> valueMap) {
        return coreRunner.insert(table, valueMap);
    }

    public int insertToTable(String table, Object... records) {
        int num = 0;
        if (records != null) {
            for (Object record : records) {
                if (record != null) {
                    Map<String, Object> valueMap = toColumnValueMap(record);
                    num += insert(table, valueMap);
                }
            }
        }
        return num;
    }

    public int insert(Object... records) {
        int num = 0;
        if (records != null) {
            for (Object record : records) {
                num += insertToTable(TableLoc.findTableName(record.getClass(),getDataSource()), record);
            }
        }
        return num;
    }
    public <T> T insertAndReturnAutoGen(Object record,ResultSetHandler<T> resultSetHandler) {
        return coreRunner.insertWithReturn(TableLoc.findTableName(record.getClass(),getDataSource()),resultSetHandler, toColumnValueMap(record));
    }

    public <T> T insertAndReturnAutoGen(Object record,Integer columnIndex) {
        return coreRunner.insertWithReturn(TableLoc.findTableName(record.getClass(),getDataSource()),columnIndex,null, toColumnValueMap(record));
    }

    public <T> T insertAndReturnAutoGen(Object record,String columnName) {
        return coreRunner.insertWithReturn(TableLoc.findTableName(record.getClass(),getDataSource()),null,columnName, toColumnValueMap(record));
    }

    public <T> T insertAndReturnAutoGen(Object record) {
        return coreRunner.insertWithReturn(TableLoc.findTableName(record.getClass(),getDataSource()),null,null, toColumnValueMap(record));
    }

    public int batchInsertValueMapToTable(String table, int bulkSize, List<Map<String, Object>> valueMapList) {
        int num = 0;
        if (CollectionUtils.isEmpty(valueMapList)) {
            return num;
        }
        Map<String, List<Map<String, Object>>> dataMap = new HashMap<>();
        for (Map<String, Object> valueMap : valueMapList) {
            if (MapUtils.isEmpty(valueMap)) {
                continue;
            }
            List<String> keys = valueMap.keySet().stream().sorted().collect(Collectors.toList());
            String keysStr = keys.toString();
            if (dataMap.containsKey(keysStr)) {
                dataMap.get(keysStr).add(valueMap);
            } else {
                List<Map<String, Object>> listMap = new ArrayList<>();
                listMap.add(valueMap);
                dataMap.put(keysStr, listMap);
            }
        }
        String sqlId = ExtraParamInjector.getSqlId();
        SqlExecutionInterceptor sqlExecutionInterceptor = ExtraParamInjector.getSqlInterceptor();
        SqlExecutionInterceptorChainMode sqlInterceptorChainMode = ExtraParamInjector.getSqlInterceptorChainMode();
        boolean interceptorSpan = sqlExecutionInterceptor !=null&& sqlExecutionInterceptor.spanWithin();
        for (List<Map<String, Object>> list : dataMap.values()) {
            List<List<Map<String, Object>>> partitions = Lists.partition(list, bulkSize);
            for (List<Map<String, Object>> partition : partitions) {
                ExtraParamInjector.sqlId(sqlId);
                if(interceptorSpan){
                    ExtraParamInjector.interceptWithChainMode(sqlExecutionInterceptor,sqlInterceptorChainMode!=null?sqlInterceptorChainMode:SqlExecutionInterceptorChainMode.CHAIN_AFTER_GLOBAL);
                }
                num += coreRunner.batchInsert(table, partition);
            }
        }
        return num;
    }

    public int batchInsertToTable(String table, int bulkSize, Object... records) {
        int num = 0;
        if (records == null || records.length==0) {
            return num;
        }
        List<Map<String, Object>> valueMapList = new ArrayList<>();
        for (Object record : records) {
            if (record != null) {
                valueMapList.add(toColumnValueMap(record));
            }
        }
        return batchInsertValueMapToTable(table,bulkSize,valueMapList);
    }

    public int batchInsertWithSize(int bulkSize, Object... records) {
        if (records != null && records.length > 0) {
            Object first = records[0];
            if (records.length == 1 && first instanceof List) {
                List<?> list = (List<?>) first;
                if (CollectionUtils.isNotEmpty(list)) {
                    String tableName = TableLoc.findTableName(list.get(0).getClass(),getDataSource());
                    return batchInsertToTable(tableName, bulkSize, list.toArray());
                }
            } else {
                String tableName = TableLoc.findTableName(first.getClass(),getDataSource());
                return batchInsertToTable(tableName, bulkSize, records);
            }
        }
        return 0;
    }

    public int batchInsert(Object... records) {
        return batchInsertWithSize(100, records);
    }

    public int update(String table, Map<String, Object> updateValueMap, List<Cond> conds) {
        try {
            List<ColumnValuePair4Update> pairs = toFullColumnValuePair(updateValueMap);
            if(ExtraParamInjector.columnsFromCondIgnoredForUpdate()){
                if(CollectionUtils.isNotEmpty(conds)){
                    List<String> colNames = conds.stream().map(Cond::getColumnName).collect(Collectors.toList());
                    pairs.removeIf(columnValuePair4Update -> colNames.contains(columnValuePair4Update.getColumn()));
                }
            }
            UpdateConditionBundle upCond = new UpdateConditionBundle.Builder()
                .targetTable(table)
                .values2Update(MiscUtil.combineList(pairs,ExtraParamInjector.getExtraColumnValuePairs4Update()))
                .conditionAndList(combineConds(conds, ExtraParamInjector.getExtraConds()))
                .conditionOrList(ExtraParamInjector.getExtraOrConds())
                .build();
            if(CollectionUtils.isEmpty(upCond.getValues2Update())){
                throw new IllegalArgumentException("There are no values to be updated, table="+table);
            }
            SqlPreparedBundle sqlPreparedBundle = coreRunner.getSqlBuilder().composeUpdate(upCond);
            if(!sqlPreparedBundle.isWithCondition()&&!ExtraParamInjector.emptyUpdateCondAllowed()){
                throw new IllegalArgumentException("Update without condition! sql:"+sqlPreparedBundle.getSql()+", This restriction can be suppressed by ExtraParamInjector.allowEmptyUpdateCond()");
            }
            return coreRunner.genericUpdate(sqlPreparedBundle.getSql(), sqlPreparedBundle.getValues());
        } finally {
            ExtraParamInjector.unsetForUpdate();
        }
    }

    public int[] batchUpdate(List<Triple<String,Map<String, Object>, List<Cond>>> batchUpdateTriples) {
        try {
            if(CollectionUtils.isEmpty(batchUpdateTriples)){
                return new int[]{-1};
            }
            String sqlId = ExtraParamInjector.getSqlId();
            SqlExecutionInterceptor sqlExecutionInterceptor = ExtraParamInjector.getSqlInterceptor();
            SqlExecutionInterceptorChainMode sqlInterceptorChainMode = ExtraParamInjector.getSqlInterceptorChainMode();
            Map<String,List<SqlPreparedBundle>> sqlPreparedBundleMap = new LinkedHashMap<>();
            int size = batchUpdateTriples.size();
            // for each item in LinkedHashMap:sqlPreparedBundleMap, the key is the index to which the sql corresponds, the value is the sql.
            Map<Integer,String> posSqlMap = new HashMap<>();
            // for each item of updateValueMapAndConds, the key is the item index, the value is the sql built from the item.
            Map<Integer,String> idxSqlMap = new HashMap<>();
            // for each item of updateValueMapAndConds, the key is the item index, the value is the index of sqlPreparedBundle from List<SqlPreparedBundle> which grouped by sql .
            Map<Integer, Integer> innerPosMap = new HashMap<>();
            // the key is the index based on the return value of batchUpdate, the value is the index corresponds to the item of updateValueMapAndConds.
            Map<Integer, Integer> resultPosMap = new HashMap<>();
            for (int i = 0; i < size; i++){
                Triple<String,Map<String, Object>, List<Cond>> triple = batchUpdateTriples.get(i);
                String table = triple.getLeft();
                Map<String, Object> updateValueMap = triple.getMiddle();
                List<Cond> conds = triple.getRight();
                List<ColumnValuePair4Update> columnValuepairs = toFullColumnValuePair(updateValueMap);
                if(ExtraParamInjector.columnsFromCondIgnoredForUpdate()){
                    if(CollectionUtils.isNotEmpty(conds)){
                        List<String> colNames = conds.stream().map(Cond::getColumnName).collect(Collectors.toList());
                        columnValuepairs.removeIf(columnValuePair4Update -> colNames.contains(columnValuePair4Update.getColumn()));
                    }
                }
                UpdateConditionBundle upCond = new UpdateConditionBundle.Builder()
                        .targetTable(table)
                        .values2Update(MiscUtil.combineList(columnValuepairs,ExtraParamInjector.getExtraColumnValuePairs4Update()))
                        .conditionAndList(combineConds(conds, ExtraParamInjector.getExtraConds()))
                        .conditionOrList(ExtraParamInjector.getExtraOrConds())
                        .build();
                if(CollectionUtils.isEmpty(upCond.getValues2Update())){
                    throw new IllegalArgumentException("There are no values to be updated, table="+table);
                }
                SqlPreparedBundle sqlPreparedBundle = coreRunner.getSqlBuilder().composeUpdate(upCond);
                String sql = sqlPreparedBundle.getSql();
                if(!sqlPreparedBundle.isWithCondition()&&!ExtraParamInjector.emptyUpdateCondAllowed()){
                    throw new IllegalArgumentException("Update without condition! sql:"+ sql +", This restriction can be suppressed by ExtraParamInjector.allowEmptyUpdateCond()");
                }
                idxSqlMap.put(i,sql);
                List<SqlPreparedBundle> sqlPreparedBundles = sqlPreparedBundleMap.get(sql);
                if(sqlPreparedBundles==null){
                    sqlPreparedBundles = new ArrayList<>();
                    sqlPreparedBundles.add(sqlPreparedBundle);
                    sqlPreparedBundleMap.put(sql,sqlPreparedBundles);
                    posSqlMap.put(posSqlMap.size(),sql);
                }else {
                    sqlPreparedBundles.add(sqlPreparedBundle);
                }
                innerPosMap.put(i,sqlPreparedBundles.size()-1);
            }
            Map<String,Integer> sqlStartIdxMap = new HashMap<>();
            AtomicInteger posCur = new AtomicInteger(0);
            posSqlMap.keySet().stream().sorted().forEach(pos->{
                String sql = posSqlMap.get(pos);
                int bundleSize = sqlPreparedBundleMap.get(sql).size();
                int idx = posCur.get();
                sqlStartIdxMap.put(sql, idx);
                posCur.set(idx+bundleSize);
            });
            for (int i = 0; i < size; i++){
                String sql = idxSqlMap.get(i);
                Integer sqlStartIdx = sqlStartIdxMap.get(sql);
                Integer innerIdx = innerPosMap.get(i);
                resultPosMap.put((sqlStartIdx+innerIdx),i);
            }
            List<Integer> batchEffected = new ArrayList<>();
            sqlPreparedBundleMap.forEach((sql, sqlPreparedBundles)-> {
                ExtraParamInjector.sqlId(sqlId);
                ExtraParamInjector.interceptWithChainMode(sqlExecutionInterceptor,sqlInterceptorChainMode!=null?sqlInterceptorChainMode:SqlExecutionInterceptorChainMode.CHAIN_AFTER_GLOBAL);
                Object[][] batchValues = toBatchValues(sqlPreparedBundles);
                int[] nums = coreRunner.genericBatchUpdate(sql, batchValues);
                batchEffected.addAll(Arrays.stream(nums).boxed().collect(Collectors.toList()));
                });
            int[] rt = new int[size];
            for (int i = 0; i < size; i++){
                rt[i] = batchEffected.get(resultPosMap.get(i));
            }
            return rt;
        } finally {
            ExtraParamInjector.unsetForUpdate();
        }
    }

    public int updateSelective(String table, Object record, List<Cond> conds) {
        return update(table, toColumnValueMap(record), conds);
    }

    public int[] batchUpdateSelectiveByTable(List<Triple<String,Object,List<Cond>>> tableRecordCondsTriples) {
        if(CollectionUtils.isEmpty(tableRecordCondsTriples)){
            return new int[]{-1};
        }
        return batchUpdate(tableRecordCondsTriples.stream()
                        .map(triple -> Triple.of(triple.getLeft(),toColumnValueMap(triple.getMiddle()), triple.getRight()))
                        .collect(Collectors.toList()));
    }

    public int updateSelective(Object record, List<Cond> conds) {
        return updateSelective(TableLoc.findTableName(record.getClass(),getDataSource()), record, conds);
    }

    public int[] batchUpdateSelectiveByRecord(List<Pair<Object,List<Cond>>> recordCondsPairs){
        if(CollectionUtils.isEmpty(recordCondsPairs)){
            return new int[]{-1};
        }
        return batchUpdateSelectiveByTable(recordCondsPairs.stream()
                .map(pair -> Triple.of(TableLoc.findTableName(pair.getLeft().getClass(),getDataSource()),pair.getLeft(), pair.getRight()))
                .collect(Collectors.toList()));
    }

    public <E> int updateSelective(Object record, CondCrafter<E> condCrafter, E primalCond) {
        return updateSelective(record, condCrafter.craft(primalCond));
    }

    public <E> int[] batchUpdateSelectiveWithCondCrafter(List<Triple<Object,CondCrafter<E>,E>> recordCondCrafterPrimalCondTriples){
        if(CollectionUtils.isEmpty(recordCondCrafterPrimalCondTriples)){
            return new int[]{-1};
        }
        return batchUpdateSelectiveByRecord(recordCondCrafterPrimalCondTriples.stream()
                .map(triple -> Pair.of(triple.getLeft(),triple.getMiddle().craft(triple.getRight())))
                .collect(Collectors.toList()));
    }

    public int updateSelective(Object record, Cond... conds) {
        return updateSelective(record, Arrays::asList, conds);
    }

    @SafeVarargs
    public final int[] batchUpdateSelectiveByRecordVarargs(Pair<Object, List<Cond>>... recordCondsPairs){
        return batchUpdateSelectiveByRecord(Arrays.asList(recordCondsPairs));
    }

    @SafeVarargs
    public final int[] batchUpdateSelectiveByTableVarargs(Triple<String,Object,List<Cond>>... tableRecordCondsTriples){
        return batchUpdateSelectiveByTable(Arrays.asList(tableRecordCondsTriples));
    }

    public int updateSelectiveConcise(Object record, String... fieldsOrColumns) {
        ExtraParamInjector.ignoreColumnsFromCondForUpdate();
        return updateSelective(record, initCondsByFields(record, fieldsOrColumns));
    }

    public int[] batchUpdateSelectiveConcise(List<Pair<Object,List<String>>> recordFieldsOrColumnsPairs){
        ExtraParamInjector.ignoreColumnsFromCondForUpdate();
        return batchUpdateSelectiveByRecord(recordFieldsOrColumnsPairs.stream()
                .map(pair -> Pair.of(
                        // record
                        pair.getLeft(),
                        // conds
                        initCondsByFields(
                            // record
                            pair.getLeft(),
                            // fieldsOrColumns
                            pair.getRight().toArray(new String[0])
                        )
                    )
                )
                .collect(Collectors.toList()));
    }

    @SafeVarargs
    public final int[] batchUpdateSelectiveConciseVarargs(Pair<Object, List<String>>... recordFieldsOrColumnsPairs){
        return batchUpdateSelectiveConcise(Arrays.asList(recordFieldsOrColumnsPairs));
    }

    public <T> int updateSelectiveAutoCond(T record, T condObj) {
        return updateSelective(record, this::fromTableDomain, condObj);
    }

    public int[] batchUpdateSelectiveAutoCond(List<Pair<Object,Object>> recordCondObjPairs){
        if(CollectionUtils.isEmpty(recordCondObjPairs)){
            return new int[]{-1};
        }
        return batchUpdateSelectiveByRecord(recordCondObjPairs.stream()
                .map(pair -> Pair.of(pair.getLeft(),fromTableDomain(pair.getRight())))
                .collect(Collectors.toList()));
    }

    @SafeVarargs
    public final int[] batchUpdateSelectiveAutoCondVarargs(Pair<Object, Object>... recordCondObjPairs){
        return batchUpdateSelectiveAutoCond(Arrays.asList(recordCondObjPairs));
    }

    public int updateSelectiveByPrimary(Object record){
        return update(TableLoc.findTableName(record.getClass(),getDataSource()), toColumnValueMap(record,false),getPrimaryConds(record));
    }

    public int[] batchUpdateSelectiveByPrimary(List<Object> records){
        return batchUpdate(records.stream()
                .map(record -> Triple.of(TableLoc.findTableName(record.getClass(),getDataSource()),toColumnValueMap(record,false),getPrimaryConds(record)))
                .collect(Collectors.toList()));
    }

    public int[] batchUpdateSelectiveByPrimaryVarargs(Object ... records){
        return batchUpdateSelectiveByPrimary(Arrays.asList(records));
    }

    public int persist(Object record, List<Cond> conds) {
        if (CollectionUtils.isEmpty(conds)) {
            throw new IllegalArgumentException("conditions can't be empty for persist");
        }
        String sqlId = ExtraParamInjector.getSqlId();
        SqlExecutionInterceptor sqlExecutionInterceptor = ExtraParamInjector.getSqlInterceptor();
        SqlExecutionInterceptorChainMode sqlInterceptorChainMode = ExtraParamInjector.getSqlInterceptorChainMode();
        boolean interceptorSpan = sqlExecutionInterceptor !=null&& sqlExecutionInterceptor.spanWithin();
        int num;
        num = updateSelective(record, conds);
        if (num == 0) {
            if(interceptorSpan){
                ExtraParamInjector.interceptWithChainMode(sqlExecutionInterceptor,sqlInterceptorChainMode!=null?sqlInterceptorChainMode:SqlExecutionInterceptorChainMode.CHAIN_AFTER_GLOBAL);
            }
            ExtraParamInjector.sqlId(sqlId);
            num = insert(record);
        }
        return num;
    }

    public <E> int persist(Object record, CondCrafter<E> condCrafter, E primalCond) {
        return persist(record, condCrafter.craft(primalCond));
    }

    public int persist(Object record, Cond... conds) {
        return persist(record, Arrays::asList, conds);
    }

    public <T> int persistAutoCond(T record, T condObj) {
        ExtraParamInjector.ignoreColumnsFromCondForUpdate();
        return persist(record, this::fromTableDomain, condObj);
    }

    public int persistByPrimary(Object record){
        ExtraParamInjector.ignoreColumnsFromCondForUpdate();
        return persist(record, this::getPrimaryConds, record);
    }

    public int increment(String table, String columnName, long incr, List<Cond> conds){
        ExtraParamInjector.addColumnValuePair4Update(new ColumnValuePair4Update(columnName,columnName+(incr>=0?" + ":" - ")+Math.abs(incr), true));
        return update(table,null,conds);
    }

    public int increment(Class<?> tableClass, String columnOrFieldName, long incr, List<Cond> conds){
        return increment(TableLoc.findTableName(tableClass,getDataSource()),determineColumnName(tableClass,columnOrFieldName),incr,conds);
    }

    public int increment(Class<?> tableClass, String columnOrFieldName, long incr, Cond... conds){
        return increment(tableClass,columnOrFieldName,incr,Arrays.asList(conds));
    }

    public int updateFull(String table, Object record, List<Cond> conds, List<String> excludeColumns, boolean includePrimary) {
        Map<String, Object> map = toFullColumnValueMap(record,includePrimary,excludeColumns);
        return update(table, map, conds);
    }

    public int[] batchUpdateFull(List<Pair<Triple<String,Object,List<Cond>>,Pair<List<String>,Boolean>>> batchUpdateFullComplex){
        return batchUpdate(batchUpdateFullComplex.stream()
                .map(complex -> Triple.of(
                        //table
                        complex.getLeft().getLeft(),
                        //updateValueMap
                        toFullColumnValueMap(
                                //record
                                complex.getLeft().getMiddle(),
                                //includePrimary
                                complex.getRight().getRight(),
                                //excludeColumns
                                complex.getRight().getLeft()
                        ),
                        //conds
                        complex.getLeft().getRight()))
                .collect(Collectors.toList()));
    }

    public int updateFull(Object record, List<Cond> conds, String ... excludeColumns) {
        return updateFull(TableLoc.findTableName(record.getClass(),getDataSource()), record, conds, Arrays.asList(excludeColumns),true);
    }

    public int[] batchUpdateFullByRecord(List<Triple<Object,List<Cond>,List<String>>> recordCondsExcludeColumnsTriples){
        if(CollectionUtils.isEmpty(recordCondsExcludeColumnsTriples)){
            return new int[]{-1};
        }
        return batchUpdateFull(recordCondsExcludeColumnsTriples.stream()
                .map(triple->Pair.of(Triple.of(
                        //table
                        TableLoc.findTableName(triple.getLeft().getClass(),getDataSource()),
                        //record
                        triple.getLeft(),
                        //conds
                        triple.getMiddle()
                ),Pair.of(
                        //excludeColumns
                        triple.getRight(),
                        //includePrimary
                        true)
                ))
                .collect(Collectors.toList()));
    }

    public <E> int updateFull(Object record, CondCrafter<E> condCrafter, E primalCond, String ... excludeColumns) {
        return updateFull(record, condCrafter.craft(primalCond), excludeColumns);
    }

    public <E> int[] batchUpdateFullWithCondCrafter(List<Pair<Triple<Object,CondCrafter<E>, E>, List<String>>> batchUpdateFullComplex){
        return batchUpdateFullByRecord(batchUpdateFullComplex.stream()
                .map(pair->Triple.of(
                        //record
                        pair.getLeft().getLeft(),
                        //conds
                        pair.getLeft().getMiddle().craft(pair.getLeft().getRight()),
                        //excludeColumns
                        pair.getRight()))
                .collect(Collectors.toList()));
    }

    public <T> int updateFull(T record, T condObj, String ... excludeColumns) {
        return updateFull(record, this::fromTableDomain, condObj, excludeColumns);
    }

    public int[] batchUpdateFullAutoCond(List<Triple<Object,Object,List<String>>> recordCondObjExcludeColumnsTriples){
        return batchUpdateFullByRecord(recordCondObjExcludeColumnsTriples.stream()
                .map(triple -> Triple.of(
                        //record
                        triple.getLeft(),
                        //conds
                        fromTableDomain(triple.getMiddle()),
                        //excludeColumns
                        triple.getRight()))
                .collect(Collectors.toList()));
    }

    public int updateFullByPrimary(Object record, String ... excludeColumns){
        return updateFull(TableLoc.findTableName(record.getClass(),getDataSource()),record,getPrimaryConds(record),Arrays.asList(excludeColumns),false);
    }

    public int[] batchUpdateFullByPrimary(List<Pair<Object,List<String>>> recordExcludeColumnsPairs){
        return batchUpdateFull(recordExcludeColumnsPairs.stream()
                .map(pair -> Pair.of(Triple.of(
                        //table
                        TableLoc.findTableName(pair.getLeft().getClass(),getDataSource()),
                        //record
                        pair.getLeft(),
                        //conds
                        getPrimaryConds(pair.getLeft())
                ),Pair.of(
                        //excludeColumns
                        pair.getRight(),
                        //includePrimary
                        true)
                ))
                .collect(Collectors.toList()));
    }

    public boolean exist(Class<?> clazz, List<Cond> conds) {
        trySelectOneForExist();
        return findObject(conds, clazz) != null;
    }

    public <E> boolean exist(Class<?> clazz, CondCrafter<E> condCrafter, E primalCond) {
        return exist(clazz, condCrafter.craft(primalCond));
    }

    public boolean exist(Class<?> clazz, Cond... conds) {
        return exist(clazz, Arrays::asList, conds);
    }

    public <T> boolean exist(T obj) {
        if(obj instanceof Class){
            return exist((Class<?>) obj,new Cond[0]);
        }
        return exist(obj.getClass(), this::fromTableDomain, obj);
    }

    private int countInner(String table, List<Cond> conds, Class<?> clazz, boolean includePagingCondition){
        List<String> groupByColumns = ExtraParamInjector.getGroupByColumns();
        QueryConditionBundle qcCount = new QueryConditionBundle.Builder()
            .targetTable(table)
            .tableClass(clazz)
            .conditionAndList(combineConds(conds, ExtraParamInjector.getExtraConds()))
            .conditionOrList(ExtraParamInjector.getExtraOrConds())
            .selectColumns(ExtraParamInjector.getSelectColumns())
            .excludedColumns(ExtraParamInjector.getExcludedColumns())
            .tableAliasForJoin(ExtraParamInjector.getMainTableAlias())
            .joins(ExtraParamInjector.getJoins())
            .groupByColumns(groupByColumns)
            .havingConds(ExtraParamInjector.getHavingConds())
            .build();
        if(includePagingCondition){
            qcCount.setOffset(PagingInjector.getOffset());
            qcCount.setLimit(PagingInjector.getLimit());
        }
        if(CollectionUtils.isNotEmpty(groupByColumns)){
            SqlPreparedBundle sqlPreparedBundle = coreRunner.getSqlBuilder().composeSelect(qcCount);
            return coreRunner.genericCount(sqlPreparedBundle.getSql(),sqlPreparedBundle.getValues());
        }else {
            qcCount.setOnlyCount(true);
            qcCount.setResultClass(CountInfo.class);
            List<CountInfo> counts = genericQry(qcCount);
            return counts.get(0).getCount();
        }
    }

    public int count(String table, List<Cond> conds, Class<?> clazz) {
        try {
            return countInner(table,conds,clazz,true);
        } finally {
            ExtraParamInjector.unsetForQuery();
        }
    }

    public int count(String table, List<Cond> conds) {
        return count(table,conds,null);
    }

    public int count(Class<?> clazz, List<Cond> conds) {
        return count(TableLoc.findTableName(clazz,getDataSource()),conds,clazz);
    }

    public <E> int count(Class<?> clazz, CondCrafter<E> condCrafter, E primalCond) {
        return count(clazz, condCrafter.craft(primalCond));
    }

    public int count(Class<?> clazz, Cond... conds) {
        return count(clazz, Arrays::asList, conds);
    }

    public <T> int count(T obj) {
        if(obj instanceof Class){
            return count((Class<?>) obj,new ArrayList<>());
        }
        return count(obj.getClass(), this::fromTableDomain, obj);
    }

    public List<Cond> fromTableDomain(Object obj) {
        List<Cond> conds = new ArrayList<>();
        Map<String, Object> condMap = toColumnValueMap(obj);
        condMap.forEach((fieldName, value) -> conds.add(new Cond(fieldName, value)));
        return conds;
    }

    public List<Cond> buildConds(Object obj) {
        return coreRunner.getSqlBuilder().buildConds(obj);
    }

    private List<Cond> initCondsByFields(Object obj, String[] fieldOrColumnArr) {
        List<Cond> conds = new ArrayList<>();
        try {
            if (fieldOrColumnArr == null || fieldOrColumnArr.length == 0) {
                return conds;
            }
            Class<?> tableClass = obj.getClass();
            TableObjectMetaCache.initTableObjectMeta(tableClass, this);
            Map<String, String> fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(tableClass,getDataSource());
            Map<String, String> columnToFieldMap = TableObjectMetaCache.getColumnToFieldMap(tableClass,getDataSource());
            Map<String, Field> fieldMap = MiscUtil.mapFieldFromObj(obj);
            for (String fieldOrColumn : fieldOrColumnArr) {
                boolean match = false;
                String colName = null;
                Field field = null;
                if (fieldToColumnMap.containsKey(fieldOrColumn)) {
                    field = fieldMap.get(fieldOrColumn);
                    colName = fieldToColumnMap.get(fieldOrColumn);
                    match = true;
                } else if (columnToFieldMap.containsKey(fieldOrColumn.toLowerCase())) {
                    String fieldName = columnToFieldMap.get(fieldOrColumn);
                    field = fieldMap.get(fieldName);
                    colName = fieldOrColumn;
                    match = true;
                }
                if (!match) {
                    throw new IllegalArgumentException("fieldOrColumn:" + fieldOrColumn + " can't be recognized!");
                }
                field.setAccessible(true);
                conds.add(new Cond(colName, field.get(obj)));
            }
        } catch (Exception e) {
            throw new DBException(e);
        }
        return conds;
    }

    private String determineColumnName(Class<?> tableClass, String fieldOrColumn){
        TableObjectMetaCache.initTableObjectMeta(tableClass, this);
        Map<String, String> fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(tableClass,getDataSource());
        Map<String, String> columnToFieldMap = TableObjectMetaCache.getColumnToFieldMap(tableClass,getDataSource());
        if (fieldToColumnMap.containsKey(fieldOrColumn)) {
            return fieldToColumnMap.get(fieldOrColumn);
        }
        if (columnToFieldMap.containsKey(fieldOrColumn)) {
            return fieldOrColumn;
        }
        throw new IllegalArgumentException("fieldOrColumn:" + fieldOrColumn + " can't be recognized!");
    }

    private static List<Cond> combineConds(List<Cond> conds1, List<Cond> conds2) {
        return MiscUtil.combineList(conds1,conds2);
    }

    private List<ColumnValuePair4Update> toFullColumnValuePair(Map<String, Object> map) {
        List<ColumnValuePair4Update> pairs = new ArrayList<>();
        if (MapUtils.isNotEmpty(map)) {
            map.forEach((key, value) -> {
                pairs.add(new ColumnValuePair4Update(key, value));
            });
        }
        return pairs;
    }

    private Map<String, Object> toColumnValueMap(Object obj) {
        return toColumnValueMap(obj,true);
    }

    private Map<String, Object> toColumnValueMap(Object obj, boolean includePrimary) {
        Map<String, Object> columnValueMap = toFullColumnValueMap(obj,includePrimary);
        columnValueMap.entrySet().removeIf(entry -> entry.getValue() == null);
        return columnValueMap;
    }

    private Map<String, Object> toFullColumnValueMap(Object obj) {
        return toFullColumnValueMap(obj,true);
    }

    private Map<String, Object> toFullColumnValueMap(Object obj, boolean includePrimary) {
        return toFullColumnValueMap(obj,includePrimary,null);
    }

    private Map<String, Object> toFullColumnValueMap(Object obj, boolean includePrimary,List<String> excludeColumns) {
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            Class<?> tableClass = obj.getClass();
            TableObjectMetaCache.initTableObjectMeta(tableClass, this);
            List<Field> fields = MiscUtil.getAllFields(tableClass);
            Map<String, String> fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(tableClass,getDataSource());
            for (Field field : fields) {
                String fieldName = field.getName();
                if (field.isSynthetic()||!fieldToColumnMap.containsKey(fieldName)) {
                    continue;
                }
                if(!includePrimary){
                    boolean isPrimary = TableObjectMetaCache.isPrimaryField(tableClass,fieldName,this);
                    if (isPrimary){
                        continue;
                    }
                }
                field.setAccessible(true);
                Object value = field.get(obj);
                map.put(fieldToColumnMap.get(fieldName), value);
            }
            if (CollectionUtils.isNotEmpty(excludeColumns)) {
                List<String> lowercaseColNames = excludeColumns.stream().map(String::toLowerCase).collect(Collectors.toList());
                List<String> keys2Remove = new ArrayList<>();
                map.forEach((key, value) -> {
                    if (lowercaseColNames.contains(key.toLowerCase())) {
                        keys2Remove.add(key);
                    }
                });
                keys2Remove.forEach(map::remove);
            }
        } catch (Exception e) {
            throw new DBException(e);
        }
        return map;
    }

    private List<Cond> getPrimaryConds(Object obj){
        List<Cond> conds = new ArrayList<>();
        try {
            Class<?> tableClass = obj.getClass();
            TableObjectMetaCache.initTableObjectMeta(tableClass, this);
            List<String> primaryFields = TableObjectMetaCache.getPrimaryFields(tableClass,getDataSource());
            if(CollectionUtils.isEmpty(primaryFields)){
                return conds;
            }
            Map<String, String> fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(tableClass,getDataSource());
            List<Field> fields = MiscUtil.getAllFields(tableClass);
            for (Field field : fields) {
                String fieldName = field.getName();
                boolean isPrimary = TableObjectMetaCache.isPrimaryField(tableClass,fieldName,this);
                if (field.isSynthetic()||!isPrimary||!primaryFields.contains(fieldName)||!fieldToColumnMap.containsKey(fieldName)) {
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(obj);
                conds.add(new Cond(fieldToColumnMap.get(fieldName),value));
            }
        } catch (Exception e) {
            throw new DBException(e);
        }

        return conds;
    }

    private boolean existOffsetLimit(){
        return PagingInjector.getOffset()!=null&&PagingInjector.getLimit()!=null;
    }

    private void tryLimitOne(){
        if(!existOffsetLimit()){
            ExtraParamInjector.offset(0,1,false);
        }
    }

    private void trySelectOneForExist(){
        if(CollectionUtils.isNotEmpty(ExtraParamInjector.getSelectColumns())){
            return;
        }
        ExtraParamInjector.selectColumns("1 as count");
        ExtraParamInjector.resultClass(CountInfo.class);
    }

    private Object[][] toBatchValues(List<SqlPreparedBundle> bundles){
        if(CollectionUtils.isEmpty(bundles)){
            return null;
        }
        Object[][] bachValues = new Object[bundles.size()][];
        for (int i = 0; i < bundles.size(); i++){
            bachValues[i] = bundles.get(i).getValues();
        }
        return bachValues;
    }
}
