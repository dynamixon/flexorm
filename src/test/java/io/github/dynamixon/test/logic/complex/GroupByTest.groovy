package io.github.dynamixon.test.logic.complex

import io.github.dynamixon.flexorm.pojo.Cond
import io.github.dynamixon.flexorm.pojo.CountInfo
import io.github.dynamixon.moredata.LogicTableA
import io.github.dynamixon.test.logic.LogicTestBase
import io.github.dynamixon.test.logic.LogicTester
import org.junit.Test

import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.*
import static io.github.dynamixon.test.logic.LogicTester.DIALECT_KEY
import static io.github.dynamixon.test.logic.LogicTester.genValidator

class GroupByTest implements LogicTestBase{

    @Test
    @Override
    void test() {
        groupByWithCount()
    }

    static void groupByWithCount(){
        Closure<?> validator = genValidator(
            'select count(*) as count from (select varchar_f, sum(int_f) sum from logic_table_A  where id != ? group by varchar_f having sum > ?) count_tmp_tbl',
            [0,1]
        )

        LogicTester.allQueryEntries().each {
            it.prep(
                sqlId(LogicTester.sqlId4Logic(it,'groupByWithCount-basic')),
                intercept(LogicTester.getDelegatedInterceptor([new CountInfo()],validator,[(DIALECT_KEY):it.getDialectType()])),
                selectColumns('varchar_f','sum(int_f) sum'),
                groupBy('varchar_f'),
                having(new Cond('sum', '>', 1))
            ).count(LogicTableA, new Cond('id','!=',0))
        }
    }
}
