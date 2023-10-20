package io.github.dynamixon.test.dynamictable

import io.github.dynamixon.flexorm.QueryEntry
import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.flexorm.logic.TableObjectMetaCache
import io.github.dynamixon.flexorm.misc.ExtraParamInjector
import io.github.dynamixon.flexorm.misc.GeneralThreadLocal
import io.github.dynamixon.flexorm.misc.InterceptorContext
import io.github.dynamixon.flexorm.misc.SqlExecutionInterceptor
import io.github.dynamixon.flexorm.pojo.Config
import io.github.dynamixon.test.CommonInfo


class DyTableNameTest {
    static void test(){
        [DialectConst.H2, DialectConst.HSQLDB, DialectConst.HSQLDB, DialectConst.MYSQL, DialectConst.PG, DialectConst.MSSQL].each { dbType ->
            String dyPart = UUID.randomUUID().toString().replace('-','')
            try {
                GeneralThreadLocal.set('tbl_dynamic_part',dyPart)
                def queryEntry = QueryEntry.initQueryEntry(
                    CommonInfo.getDataSource(dbType),
                    new Config(logStack: true, logStackPackages: ['com.github.haflife3.test'])
                )
                TableObjectMetaCache.registerTableObjectMeta(false,false,DyTableNameBean.class,queryEntry.getCoreRunner(),'ttt',['id':'id'],['id':'id'],[])
                String sql = ''
                queryEntry.prep(ExtraParamInjector.intercept(new SqlExecutionInterceptor() {
                    @Override
                    void beforeExecution(InterceptorContext interceptorContext) {
                        interceptorContext.setDelegatedResult([new DyTableNameBean(id: 'fake_id')])
                        sql = interceptorContext.getSql()
                    }
                })).findObjects(DyTableNameBean.class)
                assert sql.contains(dyPart)
            } finally {
                GeneralThreadLocal.unset('tbl_dynamic_part')
            }
        }
    }

    static void main(String[] args) {
        test()
    }
}
