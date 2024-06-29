package io.github.dynamixon.test.logic

import groovy.util.logging.Slf4j
import io.github.dynamixon.flexorm.QueryEntry
import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.flexorm.misc.InterceptorContext
import io.github.dynamixon.flexorm.misc.SqlExecutionInterceptor
import io.github.dynamixon.flexorm.pojo.Config
import io.github.dynamixon.test.CommonInfo
import net.sf.jsqlparser.util.validation.Validation
import net.sf.jsqlparser.util.validation.ValidationError
import net.sf.jsqlparser.util.validation.feature.DatabaseType
import org.junit.Test
import org.reflections.Reflections

import java.util.concurrent.ConcurrentHashMap

import static org.reflections.scanners.Scanners.SubTypes

@Slf4j
class LogicTester {

    private static Map<String,QueryEntry> dialectQEMap = new ConcurrentHashMap<>()

    public static final String DIALECT_KEY = 'DIALECT_KEY'

    static Map<String, DatabaseType> sqlParseDbTypeMap(){
        return [
            (DialectConst.MYSQL):DatabaseType.MYSQL,
            (DialectConst.H2):DatabaseType.H2,
            (DialectConst.PG):DatabaseType.POSTGRESQL,
            (DialectConst.MSSQL):DatabaseType.SQLSERVER,
        ]
    }
    static {
        DialectConst.class.getDeclaredFields().each {
            String dialect = it.get(null)
            dialectQEMap.put(dialect,dialectQE(dialect))
        }
    }

    static testMulti(List<LogicTestBase> tests){
        tests.each {
            log.info("+++++++ testing ${it.class.name} +++++++")
            it.test()
            log.info("------- finish ${it.class.name} -------\n\n")
        }
    }

    @Test
    void testAll(){
        testMulti(allTests())
    }

    static SqlExecutionInterceptor getDelegatedInterceptor(Object delegatedResult, Closure<?> validator,Map<String,Object> extraInfo, boolean spanWithin = true){
        return new SqlExecutionInterceptor() {
            @Override
            boolean spanWithin() {
                return spanWithin
            }
            @Override
            void beforeExecution(InterceptorContext interceptorContext){
                interceptorContext.putAllToExtraContextInfo(extraInfo)
                interceptorContext.setDelegatedResult(delegatedResult)
            }
            @Override
            void afterExecution(InterceptorContext interceptorContext){
                validator.call(interceptorContext)
            }
        }
    }

    static SqlExecutionInterceptor getDelegatedInterceptorForBatchUpdate(Map<String,Object> sqlDelegatedResultMap, Closure<?> validator,Map<String,Object> extraInfo, boolean spanWithin = true){
        return new SqlExecutionInterceptor() {
            @Override
            boolean spanWithin() {
                return spanWithin
            }
            @Override
            void beforeExecution(InterceptorContext interceptorContext){
                interceptorContext.putAllToExtraContextInfo(extraInfo)
                interceptorContext.setDelegatedResult(sqlDelegatedResultMap.get(interceptorContext.getSql()))
            }
            @Override
            void afterExecution(InterceptorContext interceptorContext){
                validator.call(interceptorContext)
            }
        }
    }

    static SqlExecutionInterceptor getDelegatedInterceptor(Object delegatedResult, Closure<?> validator, boolean spanWithin = true){
        return getDelegatedInterceptor(delegatedResult,validator,[:],spanWithin)
    }

    static QueryEntry dialectQE(String dialectType = DialectConst.MYSQL){
        if(!dialectQEMap.contains(dialectType)){
            dialectQEMap.put(dialectType,QueryEntry.initQueryEntry(
                CommonInfo.getDataSource(DialectConst.H2),
                dialectType,
                new Config(logStack: true, logStackPackages: ['io.github.dynamixon.test'])
            ))
        }
        return dialectQEMap.get(dialectType)
    }

    static List<QueryEntry> allQueryEntries(){
        return dialectQEMap.values().toList()
    }

    static List<LogicTestBase> allTests(){
        Set<Class<?>> classes = new Reflections('io.github.dynamixon.test.logic', SubTypes).getSubTypesOf(LogicTestBase)
        return classes.collect {(LogicTestBase)it.newInstance()}
    }
    static String sqlId4Logic(QueryEntry queryEntry,String extra = ''){
        return "LogicTest:[${queryEntry.getDialectType()}]"+ (extra?" ${extra}":'')
    }

    static Closure<?> genValidator(String expectedSql, List<Object> expectedValues) {
        return { InterceptorContext interceptorContext ->
            def sql = interceptorContext.getSql()
            def values = interceptorContext.values
            println "sql="+sql
            println "values="+Arrays.deepToString(values)
            assert sql == expectedSql
            assert values.size() == expectedValues.size()
            values?.eachWithIndex { value, index ->
                assert value == expectedValues[index]
            }
            String dialectType = interceptorContext.getFromExtraContextInfo(DIALECT_KEY)
            def databaseType = sqlParseDbTypeMap().get(dialectType)
            if(databaseType!=null){
                Validation validation = new Validation(Collections.singletonList(databaseType), sql)
                List<ValidationError> errors = validation.validate()
                assert errors.size() == 0
            }else{
                println "dialectType:"+dialectType+" ignored for sql parse validation"
            }
        }
    }

    static Closure<?> genValidatorForBatchUpdateSelective(Map<String,Object[][]> expectedSqlValuesMap){
        return { InterceptorContext interceptorContext ->
            def sql = interceptorContext.getSql()
            def values = interceptorContext.values
            println "sql="+sql
            println "values="+Arrays.deepToString(values)
            Object[] expectedValues = expectedSqlValuesMap.get(sql)
            assert expectedValues!=null
            assert Arrays.deepToString(values) == Arrays.deepToString(expectedValues)
            assert Arrays.deepEquals(values,expectedValues)
            String dialectType = interceptorContext.getFromExtraContextInfo(DIALECT_KEY)
            def databaseType = sqlParseDbTypeMap().get(dialectType)
            if(databaseType!=null){
                Validation validation = new Validation(Collections.singletonList(databaseType), sql)
                List<ValidationError> errors = validation.validate()
                assert errors.size() == 0
            }else{
                println "dialectType:"+dialectType+" ignored for sql parse validation"
            }
        }
    }
}
