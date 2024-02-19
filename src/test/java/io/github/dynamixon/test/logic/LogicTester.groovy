package io.github.dynamixon.test.logic

import groovy.util.logging.Slf4j
import io.github.dynamixon.flexorm.QueryEntry
import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.flexorm.misc.InterceptorContext
import io.github.dynamixon.flexorm.misc.SqlExecutionInterceptor
import io.github.dynamixon.flexorm.pojo.Config
import io.github.dynamixon.test.CommonInfo
import org.junit.Test
import org.reflections.Reflections

import java.util.concurrent.ConcurrentHashMap

import static org.reflections.scanners.Scanners.SubTypes

@Slf4j
class LogicTester {

    private static Map<String,QueryEntry> dialectQEMap = new ConcurrentHashMap<>()
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
}
