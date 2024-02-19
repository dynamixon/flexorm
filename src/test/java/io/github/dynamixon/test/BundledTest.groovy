package io.github.dynamixon.test

import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.test.dynamictable.DyTableNameTest
import io.github.dynamixon.test.fakedb.FakeDBTest
import io.github.dynamixon.test.h2.H2Test
import io.github.dynamixon.test.hsqldb.HsqlDbTest
import io.github.dynamixon.test.logic.LogicTester
import io.github.dynamixon.test.metacache.MetaCacheTest
import io.github.dynamixon.test.mssql.MsSqlTest
import io.github.dynamixon.test.mysql.MysqlTest
import io.github.dynamixon.test.pg.PgTest
import io.github.dynamixon.test.spi.SPITest
import io.github.dynamixon.test.sqlite.SqliteTest
import io.github.dynamixon.test.table2java.Table2JavaTest
import io.github.dynamixon.test.tableloc.TableLocTest
import org.junit.Test

class BundledTest {
    @Test
    void test(){
        localTest()
        for (CommonTest ct in [
            new MysqlTest(),
            new PgTest(),
            new MsSqlTest(),
            new H2Test(),
            new HsqlDbTest(),
            new SqliteTest(),
            new FakeDBTest()
        ]){
            ct.test()
        }
        for (String dialect in [
            DialectConst.MYSQL,
            DialectConst.PG,
            DialectConst.MSSQL,
            DialectConst.H2,
            DialectConst.HSQLDB,
            DialectConst.SQLITE,
        ]){
            new Table2JavaTest().testDialect(dialect)
        }
        MetaCacheTest.printCurrentCache()
    }

    static void localTest(){
        new TableLocTest().test()
        new SqlBuilderTest().testInnerAndOrBasic()
        new SqlBuilderTest().testNull()
        new LogicTester().testAll()
        def namedSqlTest = new NamedSqlTest()
        namedSqlTest.basicTest()
        namedSqlTest.inParamTest()
        namedSqlTest.cacheTest()
        DyTableNameTest.test()
        FieldInfoGetterJavaTest.extraTest()
        FieldInfoGetterGroovyTest.extraTest()
        new SPITest().test()
        new TableLocTest().test()
    }
}
