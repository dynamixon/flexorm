package io.github.dynamixon.test.logic.join


import io.github.dynamixon.flexorm.pojo.*
import io.github.dynamixon.moredata.JoinTableA
import io.github.dynamixon.test.logic.LogicTestBase
import io.github.dynamixon.test.logic.LogicTester
import org.junit.Test

import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.*
import static io.github.dynamixon.test.logic.LogicTester.genValidator
import static io.github.dynamixon.test.logic.LogicTester.DIALECT_KEY

class SqlJoinTest implements LogicTestBase{

    @Test
    @Override
    void test() {
        basicTest()
        multiConditionTest()
    }

    static void basicTest() {
        Closure<?> validator = genValidator(
            'select TBL_A.id, TBL_B.int_f, TBL_C.varchar_f from join_table_A TBL_A  left join join_table_B TBL_B on TBL_A.id  = TBL_B.id  right join join_table_C TBL_C on TBL_A.id  = TBL_C.id  where TBL_A.id = ? and TBL_B.int_f = ? and TBL_C.varchar_f = ?',
            [123L,456,'abc']
        )

        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'join-basic')),
                intercept(LogicTester.getDelegatedInterceptor([new JoinTableA()],validator,[(DIALECT_KEY):it.getDialectType()])),
                selectColumns('TBL_A.id','TBL_B.int_f','TBL_C.varchar_f'),
                joinTable('TBL_A',[
                    new LeftJoin('join_table_B','TBL_B','id','id'),
                    new RightJoin('join_table_C','TBL_C','id','id')
                ])
            ).findObjects(JoinTableA, new Cond('id',123L),new Cond('TBL_B.int_f',456),new Cond('TBL_C.varchar_f','abc'))
        }
        validator = genValidator(
            'select * from join_table_A TBL_A  left join join_table_B TBL_B on TBL_A.id  = TBL_B.id  right join join_table_C TBL_C on TBL_A.id  = TBL_C.id  where TBL_A.id = ? and TBL_B.int_f = ? and TBL_C.varchar_f = ? order by TBL_A.id desc',
            [123L,456,'abc']
        )

        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'join-basic-all-col')),
                intercept(LogicTester.getDelegatedInterceptor([new JoinTableA()],validator,[(DIALECT_KEY):it.getDialectType()])),
                order(new OrderCond('id','desc')),
                joinTable('TBL_A',[
                    new LeftJoin('join_table_B','TBL_B','id','id'),
                    new RightJoin('join_table_C','TBL_C','id','id')
                ])
            ).findObjects(JoinTableA, new Cond('id',123L),new Cond('TBL_B.int_f',456),new Cond('TBL_C.varchar_f','abc'))
        }
    }

    static void multiConditionTest() {
        Closure<?> validator = genValidator(
            'select TBL_A.id, TBL_B.int_f from join_table_A TBL_A  inner join join_table_B TBL_B on TBL_A.id = TBL_B.id  and TBL_A.varchar_f = ? where TBL_A.id = ? and TBL_B.int_f = ?',
            ['qwe',123L,456]
        )

        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'join-multi-condition')),
                intercept(LogicTester.getDelegatedInterceptor([new JoinTableA()],validator,[(DIALECT_KEY):it.getDialectType()])),
                selectColumns('TBL_A.id','TBL_B.int_f'),
                joinTable('TBL_A',[
                    new Join('inner join','join_table_B','TBL_B',
                        Cond.noValueCompare('TBL_A.id','= TBL_B.id'),
                        new Cond('TBL_A.varchar_f','qwe'),
                    )
                ])
            ).findObjects(JoinTableA, new Cond('id',123L),new Cond('TBL_B.int_f',456))
        }
    }
}
