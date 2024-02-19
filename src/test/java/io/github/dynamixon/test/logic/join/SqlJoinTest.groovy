package io.github.dynamixon.test.logic.join

import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.flexorm.misc.InterceptorContext
import io.github.dynamixon.flexorm.pojo.Cond
import io.github.dynamixon.flexorm.pojo.Join
import io.github.dynamixon.flexorm.pojo.LeftJoin
import io.github.dynamixon.flexorm.pojo.RightJoin
import io.github.dynamixon.moredata.JoinTableA
import io.github.dynamixon.test.logic.LogicTestBase
import io.github.dynamixon.test.logic.LogicTester
import net.sf.jsqlparser.util.validation.Validation
import net.sf.jsqlparser.util.validation.ValidationError
import net.sf.jsqlparser.util.validation.feature.DatabaseType
import org.junit.Test

import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.*

class SqlJoinTest implements LogicTestBase{

    private static final String DIALECT_KEY = 'DIALECT_KEY'

    static Map<String, DatabaseType> sqlParseDbTypeMap(){
        return [
            (DialectConst.MYSQL):DatabaseType.MYSQL,
            (DialectConst.H2):DatabaseType.H2,
            (DialectConst.PG):DatabaseType.POSTGRESQL,
            (DialectConst.MSSQL):DatabaseType.SQLSERVER,
        ]
    }

    @Test
    @Override
    void test() {
        basicTest()
        multiConditionTest()
    }

    static Closure<?> genValidator(String expectedSql, List<Object> expectedValues) {
        return { InterceptorContext interceptorContext ->
            def sql = interceptorContext.getSql()
            def values = interceptorContext.values
            println "sql="+sql
            println "values="+values
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
