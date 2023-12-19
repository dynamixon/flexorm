package io.github.dynamixon.test

import com.google.common.cache.LoadingCache
import io.github.dynamixon.flexorm.logic.namedsql.NamedParamSqlUtil
import io.github.dynamixon.flexorm.logic.namedsql.ParsedSql
import io.github.dynamixon.test.parallel.FunctionWrapper
import io.github.dynamixon.test.parallel.ParallelTaskHub
import org.junit.Test

import java.util.function.Function

/**
 * @author Jianfeng.Mao2
 * @date 23-8-30
 */
class NamedSqlTest {

    @Test
    void basicTest(){
        Map<String, ?> paramMap = [ttt:'444',rrr:'455']
        String sql = 'select * from abc where qwe = :ttt and frt= :{rrr} and \\:rrr=555'
        def preparedBundle = NamedParamSqlUtil.fromNamed(sql, paramMap)

        String sqlToUse = preparedBundle.getSql()
        Object[] params = preparedBundle.getValues()

        println sqlToUse
        println params.toList()
        assert sqlToUse == 'select * from abc where qwe = ? and frt= ? and :rrr=555'
        assert params.length == 2
        assert params[0] == '444'
        assert params[1] == '455'
    }

    @Test
    void inParamTest(){
        Map<String, ?> paramMap = [ttt:'444',rrr:['455','678']]
        String sql = 'select * from abc where qwe = :ttt and frt in (:rrr)'
        def preparedBundle = NamedParamSqlUtil.fromNamed(sql, paramMap)
        String sqlToUse = preparedBundle.getSql()
        Object[] params = preparedBundle.getValues()

        println sqlToUse
        println params.toList()
        assert sqlToUse == 'select * from abc where qwe = ? and frt in (?, ?)'
        assert params.length == 3
        assert params[0] == '444'
        assert params[1] == '455'
        assert params[2] == '678'
    }

    @Test
    void cacheTest(){
        def clazz = NamedParamSqlUtil.class
        def field = clazz.getDeclaredField('NAMED_PARAM_SQL_PARSE_CACHE')
        field.setAccessible(true)
        LoadingCache<String, ParsedSql> cache =  (LoadingCache<String, ParsedSql>)field.get(null)
        cache.invalidateAll()
        assert cache.size() == 0

        def cacheCount = NamedParamSqlUtil.cacheCount()
        long halfCount = (cacheCount / 2).toLong()
        String sqlTmp = 'select * from TABLE_NAME where qwe = :ttt'
        halfCount.times {
            String sql = sqlTmp.replace('TABLE_NAME', UUID.randomUUID().toString())
            NamedParamSqlUtil.fromNamed(sql, [ttt:'444'+it])
        }
        assert cache.size() == halfCount

        cacheCount.times {
            String sql = sqlTmp.replace('TABLE_NAME', UUID.randomUUID().toString())
            NamedParamSqlUtil.fromNamed(sql, [ttt:'444'+it])
        }

        List<FunctionWrapper> tasks = []
        100.times {
            tasks.add(new FunctionWrapper(new Function<Void, Void>() {
                @Override
                Void apply(Void param) {
                    String sql = sqlTmp.replace('TABLE_NAME', UUID.randomUUID().toString())
                    NamedParamSqlUtil.fromNamed(sql, [ttt:'444'+it])
                    return null
                }
            },null))
        }
        ParallelTaskHub.exeTasksWithParam(tasks)

        10.times {
            NamedParamSqlUtil.fromNamed(sqlTmp, [ttt:'444'+it])
        }
        sleep(1000L)
        assert cache.size() == cacheCount
    }
}
