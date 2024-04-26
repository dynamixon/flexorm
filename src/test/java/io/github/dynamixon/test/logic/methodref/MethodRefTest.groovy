package io.github.dynamixon.test.logic.methodref

import io.github.dynamixon.flexorm.misc.InterceptorContext
import io.github.dynamixon.moredata.LogicTableA
import io.github.dynamixon.test.logic.LogicTester
import io.github.dynamixon.test.logic.LogicTestBase
import org.junit.Test

import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.addColumnValuePair4Update
import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.intercept
import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.order
import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.sqlId
/**
 * @author Jianfeng.Mao2
 * @date 23-12-29
 */
class MethodRefTest implements LogicTestBase{

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

        Closure<?> validator4OrderCond = { InterceptorContext interceptorContext ->
            def sql = interceptorContext.getSql()
            def values = interceptorContext.values
            assert sql.contains('order by all_lower_getter desc')
            assert values.size() == 1
            assert values[0] == 'abc'
        }
        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'orderCond-java')),
                order(JavaMethodRefCond.allLowerGetterOrderCond()),
                intercept(LogicTester.getDelegatedInterceptor([new LogicTableA()],validator4OrderCond))
            ).findObjects(LogicTableA, JavaMethodRefCond.allLowerGetterCond())
        }
        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'orderCond-groovy')),
                order(GroovyMethodRefCond.allLowerGetterOrderCond()),
                intercept(LogicTester.getDelegatedInterceptor([new LogicTableA()],validator4OrderCond))
            ).findObjects(LogicTableA, GroovyMethodRefCond.allLowerGetterCond())
        }

        Closure<?> validator4ColumnValuePair4Update = { InterceptorContext interceptorContext ->
            def sql = interceptorContext.getSql()
            def values = interceptorContext.values
            assert sql.contains('set all_lower_getter = ?')
            assert values.size() == 2
            assert values[0] == 'xxx'
            assert values[1] == 10
        }
        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'columnValuePair4Update-java')),
                addColumnValuePair4Update(JavaMethodRefCond.allLowerGetterColumnValuePair4Update()),
                intercept(LogicTester.getDelegatedInterceptor(1,validator4ColumnValuePair4Update))
            ).updateSelective(new LogicTableA(), JavaMethodRefCond.idCond())
        }
        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'columnValuePair4Update-groovy')),
                addColumnValuePair4Update(GroovyMethodRefCond.allLowerGetterColumnValuePair4Update()),
                intercept(LogicTester.getDelegatedInterceptor(1,validator4ColumnValuePair4Update))
            ).updateSelective(new LogicTableA(), GroovyMethodRefCond.idCond())
        }
    }
}
