package io.github.dynamixon.test.logic.complex

import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.flexorm.misc.DelegatedResultGenerator
import io.github.dynamixon.flexorm.misc.InterceptorContext
import io.github.dynamixon.flexorm.pojo.Cond
import io.github.dynamixon.flexorm.pojo.CountInfo
import io.github.dynamixon.flexorm.pojo.OrderCond
import io.github.dynamixon.flexorm.pojo.Paginator
import io.github.dynamixon.moredata.LogicTableA
import io.github.dynamixon.test.logic.LogicTestBase
import io.github.dynamixon.test.logic.LogicTester
import org.junit.Test

import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.*
import static io.github.dynamixon.test.logic.LogicTester.DIALECT_KEY

class pagingTest implements LogicTestBase{

    @Test
    @Override
    void test() {
        pagingWithSelectColumns()
    }

    static void pagingWithSelectColumns(){
        List<Tuple2<String,List<Object>>> sqlValues = []
        Closure<?> preValidator = { InterceptorContext interceptorContext ->
            def sql = interceptorContext.getSql()
            def values = interceptorContext.values
            sqlValues.add(new Tuple2(sql,values))
        }

        DelegatedResultGenerator<List<?>> delegatedResultGenerator = new DelegatedResultGenerator<List<?>>() {
            @Override
            List<?> generate(InterceptorContext context) {
                def sql = context.sql
                if(sql.contains('count(*) as count')){
                    return [new CountInfo(count: 1)]
                }
                return [LogicTableA]
            }
        }

        Map<String,String> dialectValidateMap = [
                (DialectConst.DEFAULT):'select varchar_f, int_f from logic_table_A  where id != ? order by varchar_f desc',
                (DialectConst.MYSQL):'select varchar_f, int_f from logic_table_A  where id != ? order by varchar_f desc limit ?,?',
                (DialectConst.PG):'select varchar_f, int_f from logic_table_A  where id != ? order by varchar_f desc limit ? offset ?',
                (DialectConst.H2):'select varchar_f, int_f from logic_table_A  where id != ? order by varchar_f desc limit ?,?',
                (DialectConst.SQLITE):'select varchar_f, int_f from logic_table_A  where id != ? order by varchar_f desc limit ?,?',
                (DialectConst.HSQLDB):'select varchar_f, int_f from logic_table_A  where id != ? order by varchar_f desc limit ?,?',
                (DialectConst.MSSQL):'select varchar_f, int_f from logic_table_A  where id != ? order by varchar_f desc offset ? rows fetch next ? rows only',
        ]

        LogicTester.allQueryEntries().each {
            it.prep(
                    sqlId(LogicTester.sqlId4Logic(it,'pagingWithSelectColumns-basic')),
                    intercept(LogicTester.getDelegatedInterceptor(delegatedResultGenerator,preValidator,[(DIALECT_KEY):it.getDialectType()])),
                    selectColumns('varchar_f','int_f'),
                    paging(Paginator.paging(1,10,true,new OrderCond('varchar_f','desc')))
            ).findObject(LogicTableA, new Cond('id','!=',0))

            assert sqlValues.size() == 2
            def genSql = sqlValues[0].getV1().trim()
            if(dialectValidateMap.get(it.getDialectType())==null){
                println "!!!" + it.getDialectType() + LogicTester.allQueryEntries().size()
            }
            def validateSql = dialectValidateMap.get(it.getDialectType())
            assert genSql == validateSql

            sqlValues.clear()
        }
    }
}
