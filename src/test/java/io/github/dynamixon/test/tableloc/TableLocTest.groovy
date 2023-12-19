package io.github.dynamixon.test.tableloc


import io.github.dynamixon.dataobject.NoAnnoTable
import io.github.dynamixon.dataobject.NoAnnoTableGv
import io.github.dynamixon.dataobject.NoSuchTable
import io.github.dynamixon.dataobject.NoSuchTableGv
import io.github.dynamixon.dataobject.sub.SubNoAnnoTable
import io.github.dynamixon.dataobject.sub.SubNoAnnoTableGv
import io.github.dynamixon.dataobject.sub.SubNoSuchTable
import io.github.dynamixon.dataobject.sub.SubNoSuchTableGv
import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.flexorm.logic.TableLoc
import io.github.dynamixon.flexorm.logic.TableObjectMetaCache
import io.github.dynamixon.moredata.DecoyTable
import io.github.dynamixon.test.CommonInfo
import org.junit.Test

class TableLocTest {

    @Test
    void test(){
        basicTest()
        tableClassesTest()
        tableClassesTestMore()
    }

    static void basicTest(){
        String packageName = "io.github.dynamixon.dataobject"
        String tableName = "dummy_table"
        def dataSource = CommonInfo.getDataSource(DialectConst.HSQLDB)
        def classes = TableLoc.findClasses(tableName, packageName)
        assert classes.size() > 0
        classes.each {
            def tableName1 = TableLoc.findTableName(it,dataSource)
            if(tableName1){
                assert tableName1.toLowerCase() == tableName
            }
        }
    }

    static void tableClassesTest(){
        List<Class<?>> noSuchTableClasses = [NoSuchTable, NoSuchTableGv, SubNoSuchTable, SubNoSuchTableGv]
        String packageName = "io.github.dynamixon.dataobject"
        def tableClasses = TableLoc.tableClasses(packageName)
        noSuchTableClasses.each {
            assert tableClasses.contains(it)
        }
        assert tableClasses.size() > noSuchTableClasses.size()
    }

    static void tableClassesTestMore(){
        String packageName = "io.github.dynamixon.dataobject"
        List<Class<?>> noAnnoTableClasses = [NoAnnoTable, NoAnnoTableGv, SubNoAnnoTable, SubNoAnnoTableGv]
        def tableNameMap = TableObjectMetaCache.getTableNameMap(CommonInfo.getDataSource(DialectConst.H2))
        noAnnoTableClasses.each {
            tableNameMap.put(it,it.simpleName)
        }
        tableNameMap.put(DecoyTable,'decoy_tbl')
        def tableClasses = TableLoc.tableClasses(packageName)
        noAnnoTableClasses.each {
            assert tableClasses.contains(it)
        }
        assert !tableClasses.contains(DecoyTable)
        assert tableClasses.size() > noAnnoTableClasses.size()
    }
}
