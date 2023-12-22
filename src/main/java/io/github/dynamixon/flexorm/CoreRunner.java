package io.github.dynamixon.flexorm;

import io.github.dynamixon.flexorm.annotation.Table;
import io.github.dynamixon.flexorm.dialect.DialectConst;
import io.github.dynamixon.flexorm.dialect.DialectFactory;
import io.github.dynamixon.flexorm.dialect.batch.BatchInserter;
import io.github.dynamixon.flexorm.dialect.batch.DefaultBatchInserter;
import io.github.dynamixon.flexorm.dialect.pagination.*;
import io.github.dynamixon.flexorm.enums.LoggerLevel;
import io.github.dynamixon.flexorm.enums.SqlExecutionInterceptorChainMode;
import io.github.dynamixon.flexorm.logic.SqlBuilder;
import io.github.dynamixon.flexorm.logic.TableObjectMetaCache;
import io.github.dynamixon.flexorm.misc.*;
import io.github.dynamixon.flexorm.pojo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class CoreRunner {
    private static final Logger logger = LoggerFactory.getLogger(CoreRunner.class);
    private final QueryRunner queryRunner;
    private final SqlBuilder sqlBuilder;
    private String dialectType;
    private BatchInserter batchInserter;
    private Pagination pagination;
    private OfflinePagination offlinePagination;
    private Config config;

    public CoreRunner(QueryRunner queryRunner) {
        this(queryRunner, null, null);
    }

    public CoreRunner(QueryRunner queryRunner, String dialectType) {
        this(queryRunner, dialectType, null);
    }

    public CoreRunner(QueryRunner queryRunner, Config config) {
        this(queryRunner, null, config);
    }

    public CoreRunner(QueryRunner queryRunner, String dialectType, Config config) {
        this.queryRunner = queryRunner;
        this.config = config==null?Config.defaultConfig():config;
        if (dialectType == null) {
            this.dialectType = new PlatformUtils().determineDatabaseType(queryRunner.getDataSource());
            if (DialectFactory.SUPPORTED_DB.stream().noneMatch(this.dialectType::equalsIgnoreCase)) {
                this.dialectType = DialectConst.DEFAULT;
            }
        } else {
            this.dialectType = dialectType;
        }
        initDialect();
        sqlBuilder = new SqlBuilder(this);
    }

    private void initDialect() {
        boolean batchInserterMatch = false;
        ServiceLoader<BatchInserter> batchInserters = ServiceLoader.load(BatchInserter.class);
        for (BatchInserter batchInserter : batchInserters) {
            if (batchInserter.match(dialectType)) {
                this.batchInserter = batchInserter;
                batchInserterMatch = true;
                break;
            }
        }
        if (!batchInserterMatch) {
            this.batchInserter = new DefaultBatchInserter();
        }

        boolean paginationMatch = false;
        ServiceLoader<Pagination> paginations = ServiceLoader.load(Pagination.class);
        for (Pagination pagination : paginations) {
            if (pagination.match(dialectType)) {
                this.pagination = pagination;
                paginationMatch = true;
                break;
            }
        }
        if (!paginationMatch) {
            this.pagination = new DefaultPagination();
        }

        boolean offlinePaginationMatch = false;
        ServiceLoader<OfflinePagination> offlinePaginations = ServiceLoader.load(OfflinePagination.class);
        for (OfflinePagination offlinePagination : offlinePaginations) {
            if (offlinePagination.match(dialectType)) {
                this.offlinePagination = offlinePagination;
                offlinePaginationMatch = true;
                break;
            }
        }
        if (!offlinePaginationMatch) {
            if (!(this.pagination instanceof DefaultPagination)) {
                this.offlinePagination = new DummyOfflinePagination();
            } else {
                this.offlinePagination = new DefaultOfflinePagination();
            }
        }
        logger.info("batchInserterMatch[{}],batchInserter[{}];paginationMatch[{}],pagination[{}];offlinePaginationMatch[{}],offlinePagination[{}]", batchInserterMatch, batchInserter, paginationMatch, pagination, offlinePaginationMatch, offlinePagination);
    }

    public String getDialectType() {
        return dialectType;
    }

    public DataSource getDataSource() {
        return queryRunner.getDataSource();
    }

    public QueryRunner getQueryRunner() {
        return queryRunner;
    }

    public SqlBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    public BatchInserter getBatchInserter() {
        return batchInserter;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public OfflinePagination getOfflinePagination() {
        return offlinePagination;
    }

    public Config getConfig() {
        return config==null?Config.defaultConfig():config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public <T> List<T> genericQry(String sql, Class<T> clazz, Object[] values) {
        if (clazz.isAnnotationPresent(Table.class)) {
            TableObjectMetaCache.initTableObjectMeta(clazz, this);
        }
        return genericQry(sql, new BeanListHandler<>(clazz, new BasicRowProcessor(MoreGenerousBeanProcessorFactory.populateBeanProcessor(clazz,getDataSource()))), values);
    }

    public List<Map<String, Object>> genericQry(String sql, Object[] values) {
        return genericQry(sql, new MapListHandler(), values);
    }

    public int genericCount(String sql,Object[] values){
        try {
            String countSql = "select count(*) as count from ("+sql+") count_tmp_tbl";
            List<CountInfo> countInfos = genericQry(countSql,CountInfo.class,values);
            return countInfos.get(0).getCount();
        } catch (Exception e) {
            throw new DBException(e);
        }
    }

    public <T> T genericQry(String sql, ResultSetHandler<T> resultSetHandler, Object[] values) {
        T result;
        InterceptorContext interceptorContext = null;
        try {
            interceptorContext = initInterceptorContext(sql,values,config.getGlobalSqlExecutionInterceptor());
            sql = getIdSql(interceptorContext.getSql());
            long start = System.currentTimeMillis();
            if(interceptorContext.isResultDelegate()){
                result = interceptorContext.getGenericDelegateResult();
            }else {
                result = queryRunner.query(sql, resultSetHandler, interceptorContext.getValues());
                interceptorContext.setRealResult(result);
            }
            long end = System.currentTimeMillis();
            String outputDenote = "";
            if (result != null) {
                if (result instanceof List) {
                    outputDenote = "RESULT-SIZE";
                } else {
                    outputDenote = "OUTPUT";
                }
            }
            long timeCost = end - start;
            interceptorContext.setTimeCost(timeCost);
            log(sql, interceptorContext, result, outputDenote, timeCost);
        } catch (SQLException e) {
            throw new DBException(e);
        }finally {
            postIntercept(interceptorContext,config.getGlobalSqlExecutionInterceptor());
        }
        return result;
    }

    public int genericUpdate(String sql, Object[] values) {
        int affected = 0;
        InterceptorContext interceptorContext = null;
        try {
            interceptorContext = initInterceptorContext(sql,values,config.getGlobalSqlExecutionInterceptor());
            sql = getIdSql(interceptorContext.getSql());
            long start = System.currentTimeMillis();
            if(interceptorContext.isResultDelegate()){
                affected = interceptorContext.getGenericDelegateResult();
            }else {
                affected = queryRunner.update(sql, interceptorContext.getValues());
                interceptorContext.setRealResult(affected);
            }
            long end = System.currentTimeMillis();
            long timeCost = end - start;
            interceptorContext.setTimeCost(timeCost);
            log(sql, interceptorContext, affected, "AFFECTED", timeCost);
        } catch (SQLException e) {
            throw new DBException(e);
        }finally {
            postIntercept(interceptorContext,config.getGlobalSqlExecutionInterceptor());
        }
        return affected;
    }

    private SqlValuePart composeInsertSqlValuePart(String table, Map<String, Object> valueMap){
        StringBuilder sql = new StringBuilder("insert into " + table + " (");
        StringBuilder valueSql = new StringBuilder(" values( ");
        List<Object> values = new ArrayList<>();
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                sql.append(field).append(",");
                valueSql.append("?,");
                values.add(value);
            }
        }
        sql = new StringBuilder(StringUtils.stripEnd(sql.toString(), ",") + ") ");
        valueSql = new StringBuilder(StringUtils.stripEnd(valueSql.toString(), ",") + ") ");
        sql.append(valueSql);
        return new SqlValuePart(sql.toString(),values);
    }

    public int insert(String table, Map<String, Object> valueMap) {
        SqlValuePart sqlValuePart = composeInsertSqlValuePart(table, valueMap);
        return genericUpdate(sqlValuePart.getSqlPart(), sqlValuePart.getValueParts().toArray());
    }

    public <T> T insertWithReturn(String table,Integer columnIndex, String columnName, Map<String, Object> valueMap) {
        long start = System.currentTimeMillis();
        T rt = null;
        InterceptorContext interceptorContext = null;
        try {
            SqlValuePart sqlValuePart = composeInsertSqlValuePart(table, valueMap);
            Object[] valueArr = sqlValuePart.getValueParts().toArray();
            interceptorContext = initInterceptorContext(sqlValuePart.getSqlPart(),valueArr,config.getGlobalSqlExecutionInterceptor());
            StringBuilder sql = new StringBuilder(getIdSql(interceptorContext.getSql()));
            if(interceptorContext.isResultDelegate()){
                rt = interceptorContext.getGenericDelegateResult();
            }else {
                ScalarHandler<T> scalarHandler;
                if(StringUtils.isBlank(columnName)){
                    scalarHandler = new ScalarHandler<>(columnIndex==null?1:columnIndex);
                }else {
                    scalarHandler = new ScalarHandler<>(columnName);
                }
                rt = queryRunner.insert(sql.toString(), scalarHandler, interceptorContext.getValues());
                interceptorContext.setRealResult(rt);
            }
            long end = System.currentTimeMillis();
            long timeCost = end - start;
            interceptorContext.setTimeCost(timeCost);
            log(sql.toString(), interceptorContext, rt, "OUTPUT", timeCost);
        } catch (SQLException e) {
            throw new DBException(e);
        }finally {
            postIntercept(interceptorContext,config.getGlobalSqlExecutionInterceptor());
        }
        return rt;
    }

    public int batchInsert(String table, List<Map<String, Object>> listMap) {
        return batchInserter.batchInsert(this, table, listMap);
    }

    public List<String> getColNames(String table) {
        List<String> cols = null;
        try {
            long start = System.currentTimeMillis();
            String sql = "select * from " + table + " where 1=2";
            cols = queryRunner.query(sql, resultSet -> {
                List<String> cols1 = new ArrayList<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    if (null == columnName || 0 == columnName.length()) {
                        columnName = metaData.getColumnName(i);
                    }
                    cols1.add(columnName);
                }
                return cols1;
            });
            long end = System.currentTimeMillis();
            log(sql, null, cols, "RESULT-SIZE", (end - start));
        } catch (SQLException e) {
            throw new DBException(e);
        }
        return cols;
    }

    public Map<String, String> getTableMetas() {
        Map<String, String> map = new HashMap<>();
        DataSource dataSource = queryRunner.getDataSource();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet tablesRs = md.getTables(null, null, "%", null);
            while (tablesRs.next()) {
                String tableName = tablesRs.getString("TABLE_NAME");
                //make sure jdbc url contains parameter: useInformationSchema=true (MySQL)
                String comment = tablesRs.getString("REMARKS");
                map.put(tableName, comment);
            }
        } catch (Exception e) {
            throw new DBException(e);
        }
        return map;
    }

    private void log(String sql, InterceptorContext interceptorContext, Object output, String outputDenote, long elapsed) {
        try {
            Object[] values = interceptorContext ==null?null: interceptorContext.getValues();
            boolean resultDelegate = interceptorContext != null && interceptorContext.isResultDelegate();
            Boolean ignoreLog = GeneralThreadLocal.get(DzConst.IGNORE_LOG);
            if(ignoreLog!=null&&ignoreLog){
                return;
            }
            String delegateFlag = resultDelegate?"[DELEGATED]":"";
            String log = delegateFlag+"===> SQL: " + sql;
            if (values != null) {
                log += "  VALUES: " + new ArrayList<>(Arrays.asList(values));
            }
            if (output != null) {
                if ("RESULT-SIZE".equalsIgnoreCase(outputDenote)) {
                    log += "\n"+delegateFlag+"<=== " + outputDenote + ": " + ((List) output).size();
                } else {
                    log += "\n"+delegateFlag+"<=== " + outputDenote + ": " + output;
                }
            }
            log += "\n"+delegateFlag+"<==> ELAPSED: " + elapsed + " ms.";

            boolean logStack = getConfig().isLogStack();
            if (logStack) {
                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                log += "\n<==< INVOKE-CHAIN:"+conciseStack(stack);
            }
            LoggerLevel loggerLevel = getConfig().getLoggerLevel();
            if(loggerLevel==null){
                logger.debug(log);
                return;
            }
            switch (loggerLevel) {
                case INFO:
                    logger.info(log);
                    break;
                case WARN:
                    logger.warn(log);
                    break;
                default:
                    logger.debug(log);
                    break;
            }
        } catch (Exception e) {
            logger.warn("log error", e);
        }
    }

    private String getIdSql(String sql) {
        String idSql = sql;
        String sqlId = ExtraParamInjector.getSqlId();
        if (StringUtils.isNotBlank(sqlId)) {
            try {
                idSql = "/* " + sqlId + " */ " + sql;
            } finally {
                ExtraParamInjector.unsetSqlId();
            }
        }
        return idSql;
    }

    private List<SqlExecutionInterceptor> composeSqlExecutionInterceptors(SqlExecutionInterceptor globalSqlExecutionInterceptor){
        SqlExecutionInterceptor sqlExecutionInterceptor = ExtraParamInjector.getSqlInterceptor();
        if(sqlExecutionInterceptor==null&&globalSqlExecutionInterceptor==null){
            return null;
        }
        if(sqlExecutionInterceptor==null){
            return Collections.singletonList(globalSqlExecutionInterceptor);
        }
        SqlExecutionInterceptorChainMode sqlExecutionInterceptorChainMode = ExtraParamInjector.getSqlInterceptorChainMode();
        if(sqlExecutionInterceptorChainMode==null){
            sqlExecutionInterceptorChainMode = SqlExecutionInterceptorChainMode.CHAIN_AFTER_GLOBAL;
        }
        List<SqlExecutionInterceptor> interceptors = new ArrayList<>();
        interceptors.add(globalSqlExecutionInterceptor);
        interceptors.add(sqlExecutionInterceptor);
        if(sqlExecutionInterceptorChainMode == SqlExecutionInterceptorChainMode.CHAIN_BEFORE_GLOBAL){
            Collections.reverse(interceptors);
        }else if(sqlExecutionInterceptorChainMode == SqlExecutionInterceptorChainMode.OVERWRITE_GLOBAL){
            interceptors.remove(0);
        }
        interceptors.removeIf(Objects::isNull);
        return interceptors;
    }

    private InterceptorContext initInterceptorContext(String sql, Object[] values, SqlExecutionInterceptor globalSqlExecutionInterceptor){

        List<SqlExecutionInterceptor> interceptors = composeSqlExecutionInterceptors(globalSqlExecutionInterceptor);

        if(CollectionUtils.isNotEmpty(interceptors)){
            InterceptorContext interceptorContext = new InterceptorContext(sql,values);
            interceptors.forEach(interceptor -> interceptor.beforeExecution(interceptorContext));
            return interceptorContext;
        }
        return new InterceptorContext(sql,values);
    }

    private void postIntercept(InterceptorContext interceptorContext, SqlExecutionInterceptor globalSqlExecutionInterceptor){
        try {
            if(interceptorContext==null){
                return;
            }
            List<SqlExecutionInterceptor> interceptors = composeSqlExecutionInterceptors(globalSqlExecutionInterceptor);
            if(CollectionUtils.isEmpty(interceptors)){
                return;
            }
            interceptors.forEach(interceptor -> interceptor.afterExecution(interceptorContext));
        } finally {
            ExtraParamInjector.unsetInterceptor();
            ExtraParamInjector.unsetInterceptorChainMode();
        }
    }

    private String conciseStack(StackTraceElement[] stack) {
        try {
            StringBuilder conciseInfo = new StringBuilder();
            if (stack != null) {
                List<String> logStackPackages = config.getLogStackPackages();
                List<StackTraceElement> elements = new ArrayList<>(Arrays.asList(stack));
                for (StackTraceElement element : elements) {
                    String className = element.getClassName();
                    if (CollectionUtils.isNotEmpty(logStackPackages)) {
                        boolean match = logStackPackages.stream().anyMatch(p -> StringUtils.trimToEmpty(className).startsWith(p));
                        if (!match) {
                            continue;
                        }
                    }
                    String fileName = element.getFileName();
                    int lineNumber = element.getLineNumber();
                    String infoPart = (fileName != null && lineNumber >= 0 ?
                        "(" + fileName + ":" + lineNumber + ")" :
                        (fileName != null ? "(" + fileName + ")" : "(Unknown Source)"));
                    conciseInfo.append(infoPart).append("<-");
                }
            }
            String info = conciseInfo.toString();
            info = StringUtils.stripEnd(info, "<-");
            return info;
        } catch (Throwable e) {
            logger.warn("conciseStack error", e);
            return "Unknown Stack";
        }
    }
}
