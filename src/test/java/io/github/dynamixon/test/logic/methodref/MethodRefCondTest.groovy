package io.github.dynamixon.test.logic.methodref

import io.github.dynamixon.flexorm.misc.InterceptorContext
import io.github.dynamixon.moredata.LogicTableA
import io.github.dynamixon.test.logic.LogicTester
import io.github.dynamixon.test.logic.LogicTestBase
import org.junit.Test

import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.intercept
import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.sqlId
/**
 * @author Jianfeng.Mao2
 * @date 23-12-29
 */
class MethodRefCondTest implements LogicTestBase{

    @Test
    @Override
    void test() {
        Closure<?> validator4Id = { InterceptorContext interceptorContext ->
            def sql = interceptorContext.getSql()
            def values = interceptorContext.values
            assert sql.contains('where id = ?')
            assert values.size() == 1
            assert values[0] == 10
        }
        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'id-java')),
                intercept(LogicTester.getDelegatedInterceptor([new LogicTableA()],validator4Id))
            ).findObjects(LogicTableA, JavaMethodRefCond.idCond())

            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'id-java-del')),
                intercept(LogicTester.getDelegatedInterceptor(1,validator4Id))
            ).delObjects(LogicTableA, JavaMethodRefCond.idCond())
        }
        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'id-groovy')),
                intercept(LogicTester.getDelegatedInterceptor([new LogicTableA()],validator4Id))
            ).findObjects(LogicTableA, GroovyMethodRefCond.idCond())

            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'id-groovy-del')),
                intercept(LogicTester.getDelegatedInterceptor(1,validator4Id))
            ).delObjects(LogicTableA, GroovyMethodRefCond.idCond())
        }

        Closure<?> validator4AllLowerGetter = { InterceptorContext interceptorContext ->
            def sql = interceptorContext.getSql()
            def values = interceptorContext.values
            assert sql.contains('where all_lower_getter = ?')
            assert values.size() == 1
            assert values[0] == 'abc'
        }
        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'allLowerGetter-java')),
                intercept(LogicTester.getDelegatedInterceptor([new LogicTableA()],validator4AllLowerGetter))
            ).findObjects(LogicTableA, JavaMethodRefCond.allLowerGetterCond())
        }
        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'allLowerGetter-groovy')),
                intercept(LogicTester.getDelegatedInterceptor([new LogicTableA()],validator4AllLowerGetter))
            ).findObjects(LogicTableA, GroovyMethodRefCond.allLowerGetterCond())
        }
    }
}
