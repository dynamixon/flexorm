package io.github.dynamixon.test.logic.batch

import io.github.dynamixon.flexorm.misc.ExtraParamInjector
import io.github.dynamixon.flexorm.pojo.Cond
import io.github.dynamixon.flexorm.pojo.CondCrafter
import io.github.dynamixon.moredata.LogicTableA
import io.github.dynamixon.test.logic.LogicTestBase
import io.github.dynamixon.test.logic.LogicTester
import org.apache.commons.lang3.tuple.Pair
import org.apache.commons.lang3.tuple.Triple
import org.junit.Test

import static io.github.dynamixon.test.logic.LogicTester.genValidatorForBatchUpdateSelective

class BatchUpdateSelectiveTest implements LogicTestBase{
    @Test
    @Override
    void test() {
        batchUpdateSelectiveByPrimaryVarargs()
        batchUpdateSelectiveAutoCondVarargs()
        batchUpdateSelectiveConciseVarargs()
        batchUpdateSelectiveByTableVarargs()
        batchUpdateSelectiveWithCondCrafter()
    }

    static void batchUpdateSelectiveByPrimaryVarargs(){
        Closure<?> validator = genValidatorForBatchUpdateSelective(
                'update logic_table_A  set int_f = ? where id = ?':[[999,1L].toArray(),[999,2L].toArray()]
        )
        Map<String,?> sqlDelegatedResultMap = ['update logic_table_A  set int_f = ? where id = ?':[1,2] as int[]]
        LogicTester.allQueryEntries().each {
            def rt = it.prep(
                    ExtraParamInjector.sqlId(LogicTester.sqlId4Logic(it, 'batchUpdateSelectiveByPrimaryVarargs-basic')),
                    ExtraParamInjector.intercept(LogicTester.getDelegatedInterceptorForBatchUpdate(sqlDelegatedResultMap, validator, [(LogicTester.DIALECT_KEY): it.getDialectType()])),
            ).batchUpdateSelectiveByPrimaryVarargs(new LogicTableA(id: 1L, intF: 999), new LogicTableA(id: 2L, intF: 999))
            assert Arrays.deepToString(rt) == Arrays.deepToString([1,2].toArray())
        }

        validator = genValidatorForBatchUpdateSelective(
                'update logic_table_A  set int_f = ? where id = ?':[[999,1L].toArray(),[999,3L].toArray()],
                'update logic_table_A  set boolean_f = ? where id = ?':[[true,2L].toArray(),[false,4L].toArray()]
        )
        sqlDelegatedResultMap = [
                'update logic_table_A  set int_f = ? where id = ?':[1,2] as int[],
                'update logic_table_A  set boolean_f = ? where id = ?':[3,4] as int[]]
        LogicTester.allQueryEntries().each {
            def rt = it.prep(
                    ExtraParamInjector.sqlId(LogicTester.sqlId4Logic(it, 'batchUpdateSelectiveByPrimaryVarargs-separate-sql')),
                    ExtraParamInjector.intercept(LogicTester.getDelegatedInterceptorForBatchUpdate(sqlDelegatedResultMap, validator, [(LogicTester.DIALECT_KEY): it.getDialectType()])),
            ).batchUpdateSelectiveByPrimaryVarargs(new LogicTableA(id: 1L, intF: 999), new LogicTableA(id: 2L, booleanF: true), new LogicTableA(id: 3L, intF: 999), new LogicTableA(id: 4L, booleanF: false))
            assert Arrays.deepToString(rt) == Arrays.deepToString([1,3,2,4].toArray())
        }
    }

    static void batchUpdateSelectiveAutoCondVarargs(){
        Closure<?> validator = genValidatorForBatchUpdateSelective(
                'update logic_table_A  set int_f = ? where id = ?':[[999,1L].toArray(),[999,2L].toArray()]
        )
        Map<String,?> sqlDelegatedResultMap = ['update logic_table_A  set int_f = ? where id = ?':[1,2] as int[]]
        LogicTester.allQueryEntries().each {
            def rt = it.prep(
                    ExtraParamInjector.sqlId(LogicTester.sqlId4Logic(it, 'batchUpdateSelectiveAutoCondVarargs-basic')),
                    ExtraParamInjector.intercept(LogicTester.getDelegatedInterceptorForBatchUpdate(sqlDelegatedResultMap, validator, [(LogicTester.DIALECT_KEY): it.getDialectType()])),
            ).batchUpdateSelectiveAutoCondVarargs(Pair.of(new LogicTableA(intF: 999), new LogicTableA(id: 1L)),Pair.of(new LogicTableA(intF: 999), new LogicTableA(id: 2L)))
            assert Arrays.deepToString(rt) == Arrays.deepToString([1,2].toArray())
        }

        validator = genValidatorForBatchUpdateSelective(
                'update logic_table_A  set int_f = ? where id = ?':[[999,1L].toArray(),[999,3L].toArray()],
                'update logic_table_A  set boolean_f = ? where id = ?':[[true,2L].toArray(),[false,4L].toArray()]
        )
        sqlDelegatedResultMap = [
                'update logic_table_A  set int_f = ? where id = ?':[1,2] as int[],
                'update logic_table_A  set boolean_f = ? where id = ?':[3,4] as int[]]
        LogicTester.allQueryEntries().each {
            def rt = it.prep(
                    ExtraParamInjector.sqlId(LogicTester.sqlId4Logic(it, 'batchUpdateSelectiveAutoCondVarargs-separate-sql')),
                    ExtraParamInjector.intercept(LogicTester.getDelegatedInterceptorForBatchUpdate(sqlDelegatedResultMap, validator, [(LogicTester.DIALECT_KEY): it.getDialectType()])),
            ).batchUpdateSelectiveAutoCondVarargs(
                    Pair.of(new LogicTableA(intF: 999), new LogicTableA(id: 1L)),
                    Pair.of(new LogicTableA(booleanF: true), new LogicTableA(id: 2L)),
                    Pair.of(new LogicTableA(intF: 999), new LogicTableA(id: 3L)),
                    Pair.of(new LogicTableA(booleanF: false), new LogicTableA(id: 4L))
            )
            assert Arrays.deepToString(rt) == Arrays.deepToString([1,3,2,4].toArray())
        }
    }

    static void batchUpdateSelectiveConciseVarargs(){
        Closure<?> validator = genValidatorForBatchUpdateSelective(
                'update logic_table_A  set int_f = ? where id = ?':[[999,1L].toArray(),[999,2L].toArray()]
        )
        Map<String,?> sqlDelegatedResultMap = ['update logic_table_A  set int_f = ? where id = ?':[1,2] as int[]]
        LogicTester.allQueryEntries().each {
            def rt = it.prep(
                    ExtraParamInjector.sqlId(LogicTester.sqlId4Logic(it, 'batchUpdateSelectiveConciseVarargs-basic')),
                    ExtraParamInjector.intercept(LogicTester.getDelegatedInterceptorForBatchUpdate(sqlDelegatedResultMap, validator, [(LogicTester.DIALECT_KEY): it.getDialectType()])),
            ).batchUpdateSelectiveConciseVarargs(Pair.of(new LogicTableA(id: 1L, intF: 999), ['id']),Pair.of(new LogicTableA(id: 2L, intF: 999), ['id']))
            assert Arrays.deepToString(rt) == Arrays.deepToString([1,2].toArray())
        }

        validator = genValidatorForBatchUpdateSelective(
                'update logic_table_A  set int_f = ? where id = ?':[[999,1L].toArray(),[999,3L].toArray()],
                'update logic_table_A  set boolean_f = ? where id = ?':[[true,2L].toArray(),[false,4L].toArray()]
        )
        sqlDelegatedResultMap = [
                'update logic_table_A  set int_f = ? where id = ?':[1,2] as int[],
                'update logic_table_A  set boolean_f = ? where id = ?':[3,4] as int[]]
        LogicTester.allQueryEntries().each {
            def rt = it.prep(
                    ExtraParamInjector.sqlId(LogicTester.sqlId4Logic(it, 'batchUpdateSelectiveConciseVarargs-separate-sql')),
                    ExtraParamInjector.intercept(LogicTester.getDelegatedInterceptorForBatchUpdate(sqlDelegatedResultMap, validator, [(LogicTester.DIALECT_KEY): it.getDialectType()])),
            ).batchUpdateSelectiveConciseVarargs(
                    Pair.of(new LogicTableA(id: 1L, intF: 999), ['id']),
                    Pair.of(new LogicTableA(id: 2L, booleanF: true), ['id']),
                    Pair.of(new LogicTableA(id: 3L, intF: 999), ['id']),
                    Pair.of(new LogicTableA(id: 4L, booleanF: false), ['id'])
            )
            assert Arrays.deepToString(rt) == Arrays.deepToString([1,3,2,4].toArray())
        }
    }

    static void batchUpdateSelectiveByTableVarargs(){
        Closure<?> validator = genValidatorForBatchUpdateSelective(
                'update logic_table_A  set int_f = ? where id = ?':[[999,1L].toArray(),[999,2L].toArray()]
        )
        Map<String,?> sqlDelegatedResultMap = ['update logic_table_A  set int_f = ? where id = ?':[1,2] as int[]]
        LogicTester.allQueryEntries().each {
            def rt = it.prep(
                    ExtraParamInjector.sqlId(LogicTester.sqlId4Logic(it, 'batchUpdateSelectiveByTableVarargs-basic')),
                    ExtraParamInjector.intercept(LogicTester.getDelegatedInterceptorForBatchUpdate(sqlDelegatedResultMap, validator, [(LogicTester.DIALECT_KEY): it.getDialectType()])),
            ).batchUpdateSelectiveByTableVarargs(Triple.of('logic_table_A',new LogicTableA(intF: 999), [new Cond('id',1L)]),Triple.of('logic_table_A',new LogicTableA(intF: 999), [new Cond('id',2L)]))
            assert Arrays.deepToString(rt) == Arrays.deepToString([1,2].toArray())
        }

        validator = genValidatorForBatchUpdateSelective(
                'update logic_table_A  set int_f = ? where id = ?':[[999,1L].toArray(),[999,3L].toArray()],
                'update logic_table_A  set boolean_f = ? where id = ?':[[true,2L].toArray(),[false,4L].toArray()]
        )
        sqlDelegatedResultMap = [
                'update logic_table_A  set int_f = ? where id = ?':[1,2] as int[],
                'update logic_table_A  set boolean_f = ? where id = ?':[3,4] as int[]]
        LogicTester.allQueryEntries().each {
            def rt = it.prep(
                    ExtraParamInjector.sqlId(LogicTester.sqlId4Logic(it, 'batchUpdateSelectiveByTableVarargs-separate-sql')),
                    ExtraParamInjector.intercept(LogicTester.getDelegatedInterceptorForBatchUpdate(sqlDelegatedResultMap, validator, [(LogicTester.DIALECT_KEY): it.getDialectType()])),
            ).batchUpdateSelectiveByTableVarargs(
                    Triple.of('logic_table_A',new LogicTableA(intF: 999), [new Cond('id',1L)]),
                    Triple.of('logic_table_A',new LogicTableA(booleanF: true), [new Cond('id',2L)]),
                    Triple.of('logic_table_A',new LogicTableA(intF: 999), [new Cond('id',3L)]),
                    Triple.of('logic_table_A',new LogicTableA(booleanF: false), [new Cond('id',4L)])
            )
            assert Arrays.deepToString(rt) == Arrays.deepToString([1,3,2,4].toArray())
        }
    }

    static void batchUpdateSelectiveWithCondCrafter(){
        CondCrafter<List<Cond>> crafter = c -> c
        Closure<?> validator = genValidatorForBatchUpdateSelective(
                'update logic_table_A  set int_f = ? where id = ?':[[999,1L].toArray(),[999,2L].toArray()]
        )
        Map<String,?> sqlDelegatedResultMap = ['update logic_table_A  set int_f = ? where id = ?':[1,2] as int[]]
        LogicTester.allQueryEntries().each {
            def rt = it.prep(
                    ExtraParamInjector.sqlId(LogicTester.sqlId4Logic(it, 'batchUpdateSelectiveWithCondCrafter-basic')),
                    ExtraParamInjector.intercept(LogicTester.getDelegatedInterceptorForBatchUpdate(sqlDelegatedResultMap, validator, [(LogicTester.DIALECT_KEY): it.getDialectType()])),
            ).batchUpdateSelectiveWithCondCrafter([Triple.of(new LogicTableA(intF: 999), crafter, [new Cond('id',1L)]),Triple.of(new LogicTableA(intF: 999), crafter, [new Cond('id',2L)])])
            assert Arrays.deepToString(rt) == Arrays.deepToString([1,2].toArray())
        }

        validator = genValidatorForBatchUpdateSelective(
                'update logic_table_A  set int_f = ? where id = ?':[[999,1L].toArray(),[999,3L].toArray()],
                'update logic_table_A  set boolean_f = ? where id = ?':[[true,2L].toArray(),[false,4L].toArray()]
        )
        sqlDelegatedResultMap = [
                'update logic_table_A  set int_f = ? where id = ?':[1,2] as int[],
                'update logic_table_A  set boolean_f = ? where id = ?':[3,4] as int[]]
        LogicTester.allQueryEntries().each {
            def rt = it.prep(
                    ExtraParamInjector.sqlId(LogicTester.sqlId4Logic(it, 'batchUpdateSelectiveWithCondCrafter-separate-sql')),
                    ExtraParamInjector.intercept(LogicTester.getDelegatedInterceptorForBatchUpdate(sqlDelegatedResultMap, validator, [(LogicTester.DIALECT_KEY): it.getDialectType()])),
            ).batchUpdateSelectiveWithCondCrafter([
                    Triple.of(new LogicTableA(intF: 999),crafter,  [new Cond('id',1L)]),
                    Triple.of(new LogicTableA(booleanF: true),crafter,  [new Cond('id',2L)]),
                    Triple.of(new LogicTableA(intF: 999),crafter,  [new Cond('id',3L)]),
                    Triple.of(new LogicTableA(booleanF: false),crafter,  [new Cond('id',4L)])]
            )
            assert Arrays.deepToString(rt) == Arrays.deepToString([1,3,2,4].toArray())
        }
    }
}
