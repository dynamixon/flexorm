package io.github.dynamixon.test.logic.join

import io.github.dynamixon.flexorm.misc.InterceptorContext
import io.github.dynamixon.flexorm.pojo.Cond
import io.github.dynamixon.flexorm.pojo.JoinInstruction
import io.github.dynamixon.moredata.JoinTableA
import io.github.dynamixon.test.logic.LogicTestBase
import io.github.dynamixon.test.logic.LogicTester
import org.junit.Test

import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.*

class SqlJoinTest implements LogicTestBase{

    @Test
    @Override
    void test() {
        basicTest()
    }

    static void basicTest() {
        Closure<?> validator = { InterceptorContext interceptorContext ->
            def sql = interceptorContext.getSql()
            def values = interceptorContext.values
            assert sql == 'select TBL_A.id, TBL_B.int_f, TBL_C.varchar_f from join_table_A TBL_A  left join join_table_B TBL_B on TBL_A.id  = TBL_B.id  left join join_table_C TBL_C on TBL_A.id  = TBL_C.id  where TBL_A.id = ? and TBL_B.int_f = ? and TBL_C.varchar_f = ?'
            assert values.size()==3
            assert values[0]==123L
            assert values[1]==456
            assert values[2]=='abc'
        }

        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'join-basic')),
                intercept(LogicTester.getDelegatedInterceptor([new JoinTableA()],validator)),
                selectColumns('TBL_A.id','TBL_B.int_f','TBL_C.varchar_f'),
                joinTable('TBL_A',[
                    new JoinInstruction('join_table_B','TBL_B','id','id'),
                    new JoinInstruction('join_table_C','TBL_C','id','id')
                ])
            ).findObjects(JoinTableA, new Cond('id',123L),new Cond('TBL_B.int_f',456),new Cond('TBL_C.varchar_f','abc'))

        }
    }
}
