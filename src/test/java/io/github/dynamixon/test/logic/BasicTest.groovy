package io.github.dynamixon.test.logic

import io.github.dynamixon.flexorm.misc.InterceptorContext
import io.github.dynamixon.flexorm.pojo.Cond
import io.github.dynamixon.moredata.LogicTableA
import org.junit.Test

import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.intercept
import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.sqlId

class BasicTest implements LogicTestBase{

    @Test
    @Override
    void test() {
        Closure<?> validator = { InterceptorContext interceptorContext ->
            def sql = interceptorContext.getSql()
            def values = interceptorContext.values
            assert sql.contains('select * from logic_table_A  where id = ?')
            assert values.size() == 1
            assert values[0] == 10
        }
        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it)),
                intercept(LogicTester.getDelegatedInterceptor([new LogicTableA()],validator))
            ).findObjects(LogicTableA, new Cond('id',10))
        }
    }
}
