package io.github.dynamixon.test

import io.github.dynamixon.dataobject.*
import io.github.dynamixon.flexorm.CoreRunner
import io.github.dynamixon.flexorm.QueryEntry
import io.github.dynamixon.flexorm.annotation.Column
import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.flexorm.enums.LoggerLevel
import io.github.dynamixon.flexorm.enums.SqlExecutionInterceptorChainMode
import io.github.dynamixon.flexorm.logic.TableLoc
import io.github.dynamixon.flexorm.logic.TableObjectMetaCache
import io.github.dynamixon.flexorm.misc.*
import io.github.dynamixon.flexorm.pojo.Cond
import io.github.dynamixon.flexorm.pojo.Config
import io.github.dynamixon.flexorm.pojo.Null
import io.github.dynamixon.flexorm.pojo.OrderCond
import io.github.dynamixon.test.transaction.TransactionTest
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.collections.MapUtils
import org.apache.commons.dbutils.QueryRunner
import org.apache.commons.dbutils.ResultSetHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.ResultSet
import java.sql.SQLException

import static io.github.dynamixon.flexorm.misc.ExtraParamInjector.*

class CommonTest {
    protected static final Logger logger = LoggerFactory.getLogger(CommonTest.class)

    protected QueryEntry qe

    protected List<Class<? extends DummyTable>> getRecordClass(){
        return configMap().get("recordClass") as List<Class<? extends DummyTable>>
    }

    protected String getDbType(){
        return configMap().get("dbType")
    }

    protected String getSetupDbType(){
        return configMap().get("setupDbType")
    }

    protected String getQueryEntryDbType(){
        return qe.getDbType()
    }

    protected String tableName(){
        return configMap().get("tableName")
    }

    protected String cleanUpSql(){
        return configMap().get("cleanUpSql")
    }

    protected String dropTableSql(){
        return configMap().get("dropTableSql")
    }

    protected boolean idInt(){
        return configMap().get("idInt")
    }

    protected List<String> nullField4Test(){
        return configMap().get("nullField4Test") as List<String>
    }

    protected Map<String,String> fieldToColumnCustomMap(){
        return configMap().get("fieldToColumnCustomMap") as Map<String,String>
    }

    protected Map<String,String> columnToFieldCustomMap(){
        return configMap().get("columnToFieldCustomMap") as Map<String,String>
    }

    protected List<String> primaryFields(){
        return configMap().get("primaryFields") as List<String>
    }

    protected boolean classNeedRegMeta(){
        return configMap().get("classNeedRegMeta") == getCurrentClass()
    }

    protected String sumTestField(){
        return configMap().get("sumTestField")
    }

    protected String autoGenColName(){
        return configMap().get("autoGenColName")
    }

    protected Class<?> otherResultClass(){
        return configMap().get("otherResultClass") as Class<?>
    }

    protected Map<String,Object> configMap(){
        return [
            "recordClass":[],
            "dbType":"",
            "setupDbType":null,
            "tableName":"dummy_table",
            "cleanUpSql":"truncate table TABLE_PLACEHOLDER",
            "dropTableSql":"drop table TABLE_PLACEHOLDER",
            "idInt":false,
            "nullField4Test":["varcharF","varchar_f"],
            "classNeedRegMeta":null,
            "fieldToColumnCustomMap":["mismatchedName":"name_mismatch_f"],
            "columnToFieldCustomMap":["name_mismatch_f":"mismatchedName"],
            "primaryFields":["id"],
            "sumTestField":null,
            "autoGenColName":"id",
            "otherResultClass":null,
        ] as Map<String, Object>
    }

    protected static Class<? extends DummyTable> getCurrentClass(){
        return GeneralThreadLocal.get("CurrentClass")
    }

    protected static void setCurrentClass(Class<? extends DummyTable> clazz){
        GeneralThreadLocal.set("CurrentClass",clazz)
    }

//    @Before
    void setup(String dbType){
        logger.info('>>setup<<')
        qe = QueryEntry.initQueryEntry(
            CommonInfo.getDataSource(getDbType()),
            dbType,
            new Config(logStack: true,logStackPackages: ['io.github.dynamixon.test'])
        )
        String createTableTemplate = CommonInfo.createTableMap.get(getDbType())
        String createTableSql = createTableTemplate.replace("TABLE_PLACEHOLDER",tableName())
        qe.prep(sqlId(verboseSqlId("setup create table")))
            .genericUpdate(createTableSql)
        String cleanUpSql = cleanUpSql().replace("TABLE_PLACEHOLDER",tableName())
        qe.prep(sqlId(verboseSqlId("cleanUpSql"))).genericUpdate(cleanUpSql)
        if(classNeedRegMeta()){
            TableObjectMetaCache.registerTableObjectMeta(false,getCurrentClass(),qe.coreRunner,tableName(),fieldToColumnCustomMap(),columnToFieldCustomMap(),primaryFields())
        }
        logger.info '>>setup finish<<'
    }

//    @After
    void cleanup(){
        logger.info '>>cleanup<<'
        String sql = dropTableSql().replace("TABLE_PLACEHOLDER",tableName())
        qe.prep(sqlId(verboseSqlId("cleanup drop table")))
            .genericUpdate(sql)
        GeneralThreadLocal.unset()
        logger.info '>>cleanup finish<<'
    }

    protected void test(){
        logger.info ' -- test -- '
        new LocalLogicTest().test()
        for(String testDbType in [DialectConst.DEFAULT, getSetupDbType()]){
            getRecordClass().each {
                try {
                    setCurrentClass(it)
                    setup(testDbType)
                    condBuild()
                    queryEntryInit()
                    logger.info("************ ${getCurrentClass()} *************")
                    testDbType?:dbType()
                    tableMetas()
                    colNames()
                    batchInsert()
                    typeMapping()
                    queryAll()
                    count()
                    exist()
                    extraCondCount()
                    genericQry4Map()
                    genericNamedParamQry()
                    querySingleAndExist()
                    selectColumnsTest()
                    orderTest()
                    pagingTest()
                    paging4Map()
                    offsetTest()
                    groupByHaving()
                    nameMismatch()
                    extraCondQuery()
                    orCondQuery()
                    nullCondQuery()
                    moreQuery()
                    genericT()
                    otherResultClassTest()
                    colHandleTest()
                    interceptTest()
                    interceptWithGlobalTest()
                    loggerLevelTest()
                    testRegisterTableObjectMeta()
                    testRefreshTableMetaCache()
                    testRefreshTableMetaCacheMulti()
                    testRefreshTableMetaCacheForPackage()
                    testRemoveAllTableMetaCache()
                    testFieldInfoGetter()
                    insertOne()
                    nullCond()
                    genericNamedParamUpdate()
                    updateSelective()
                    extraCondUpdateSelective()
                    updateSelectiveByFieldOrColumn()
                    updateFull()
                    extraCondUpdateFull()
                    persist()
                    persistNoCondFail()
                    insertAndReturnAutoGen()
                    delOne()
                    extraCondDel()
                    delNoCondFail()
                    batchInsertVarArg()
                    emptyCondBlock()
                    delAll()
                    tx()
                }catch(Throwable e){
                    logger.error(e.getMessage(),e)
                    throw e
                } finally {
                    cleanup()
                }
            }
        }
        logger.info ' -- test finish -- '
    }

    void condBuild(){
        logger.info ' -- condBuild -- '
        CondBuildObj obj = new CondBuildObj(
            val1: "v1",
            val2: "v2",
            val3: "v3",
            val4: [1234L,4567L],
            val5: []
        )
        def conds = qe.buildConds(obj)
        assert conds.size() == 4
        assert conds[0].columnName == "val1" && conds[0].compareOpr == "=" && conds[0].value == "v1"
        assert conds[1].columnName == "val_2" && conds[1].compareOpr == "=" && conds[1].value == "v2"
        assert conds[2].columnName == "val3" && conds[2].compareOpr == "like" && conds[2].value == "%v3%"
        assert conds[3].columnName == "val_4" && conds[3].compareOpr == "in" && conds[3].value == [1234L,4567L]
    }

    void queryEntryInit(){
        logger.info ' -- queryEntryInit -- '
        QueryEntry qe1 = new QueryEntry(CommonInfo.getDataSource(getDbType()))
        assert qe1.getColNames(getCurrentClass()).size() > 0
        assert qe1.getCoreRunner() != null
        assert qe1.getQueryRunner() != null
        assert qe1.getDataSource() != null
        QueryEntry qe2 = new QueryEntry(CommonInfo.getDataSource(getDbType()),getDbType())
        assert qe2.getColNames(getCurrentClass()).size() > 0
        assert qe2.getCoreRunner() != null
        assert qe2.getQueryRunner() != null
        assert qe2.getDataSource() != null
        QueryEntry qe3 = new QueryEntry(CommonInfo.getDataSource(getDbType()),getDbType(),new Config(logStack: true,logStackPackages: ['io.github.dynamixon']))
        assert qe3.getColNames(getCurrentClass()).size() > 0
        assert qe3.getCoreRunner() != null
        assert qe3.getQueryRunner() != null
        assert qe3.getDataSource() != null
        QueryEntry qe4 = new QueryEntry(new QueryRunner(CommonInfo.getDataSource(getDbType())))
        assert qe4.getColNames(getCurrentClass()).size() > 0
        assert qe4.getCoreRunner() != null
        assert qe4.getQueryRunner() != null
        assert qe4.getDataSource() != null
        QueryEntry qe5 = new QueryEntry(new QueryRunner(CommonInfo.getDataSource(getDbType())),getDbType())
        assert qe5.getColNames(getCurrentClass()).size() > 0
        assert qe5.getCoreRunner() != null
        assert qe5.getQueryRunner() != null
        assert qe5.getDataSource() != null
        QueryEntry qe6 = new QueryEntry(new CoreRunner(new QueryRunner(CommonInfo.getDataSource(getDbType()))))
        assert qe6.getColNames(getCurrentClass()).size() > 0
        assert qe6.getCoreRunner() != null
        assert qe6.getQueryRunner() != null
        assert qe6.getDataSource() != null
        QueryEntry qe7 = QueryEntry.initQueryEntry(CommonInfo.getDataSource(getDbType()),new Config(logStack: true,logStackPackages: ['io.github.dynamixon']))
        assert qe7.getColNames(getCurrentClass()).size() > 0
        assert qe7.getCoreRunner() != null
        assert qe7.getQueryRunner() != null
        assert qe7.getDataSource() != null
        qe7.setConfig(new Config(logStack: true,logStackPackages: ['io.github.dynamixon','io.github.dynamixonX']))
        assert qe7.getCoreRunner().getConfig().logStackPackages[1] == 'io.github.dynamixonX'
        try {
            def clazz = qe7.class
            def field = clazz.getDeclaredField('coreRunner')
            field.setAccessible(true)
            field.set(qe7,null)
            qe7.setConfig(new Config())
            assert false
        } catch (Exception e) {
            assert e instanceof DBException
            assert e.getMessage().contains("CoreRunner hasn't been initialized!")
        }
    }

    void dbType(){
        logger.info ' -- dbType -- '
        assert getDbType() == qe.getDbType()
    }

    void tableMetas(){
        logger.info ' -- tableMetas -- '
        def metas = qe.tableMetas
        def tableName = TableLoc.findTableName(getCurrentClass(),qe.dataSource).toLowerCase()
        assert metas.any {it.key.toLowerCase() == tableName }
    }

    void colNames(){
        logger.info ' -- colNames -- '
        Class<? extends DummyTable> clazz = getCurrentClass()
        List<String> colNames = qe.prep(sqlId(verboseSqlId("colNames")))
            .getColNames(clazz)
        List<String> lowerColNames = new ArrayList<>()
        colNames.each {lowerColNames << it.toLowerCase()}
        Collections.sort(lowerColNames)
        List<String> compareColNames = new ArrayList<>()
        TableObjectMetaCache.initTableObjectMeta(clazz,qe)
        def columnToFieldMap = TableObjectMetaCache.getColumnToFieldMap(clazz,qe.dataSource)
        columnToFieldMap.each {
            compareColNames << it.key
        }
        Collections.sort(compareColNames)
        lowerColNames.eachWithIndex { String entry, int i ->
            assert entry == compareColNames.get(i)
        }
    }

    void batchInsert(){
        logger.info ' -- batchInsert -- '
        List<? extends DummyTable> list = CommonTool.generateDummyRecords(getCurrentClass(),200)
        def insertNum = qe.prep(sqlId(verboseSqlId("batchInsert")))
            .batchInsert(list)
        assert insertNum == 200
    }

    void typeMapping(){
        logger.info ' -- typeMapping -- '
        qe.prep(sqlId(verboseSqlId("typeMapping")))
            .genericQry("select * from ${tableName()}",new ResultSetHandler<List<Void>>() {
                @Override
                List<Void> handle(ResultSet rs) throws SQLException {
                    def metaData = rs.getMetaData()
                    def count = metaData.getColumnCount()
                    while (rs.next()){
                        for (i in 1 .. count){
                            def object = rs.getObject(i)
                            if(object!=null){
                                logger.info ("${object.getClass().name} # ${metaData.getColumnTypeName(i)} # ${object}")
                            }
                        }
                        break
                    }
                    return null
                }
            })
    }

    void queryAll(){
        logger.info ' -- queryAll -- '
        List<? extends DummyTable> list = qe.prep(
            sqlId(verboseSqlId("queryAll")),
            offset(null,null,false,new OrderCond("id"))
        ).searchObjects(getCurrentClass().newInstance())
        assert list.size() == 200
        GeneralThreadLocal.set("allRecords",list)
    }

    void count(){
        logger.info ' -- count -- '
        assert qe.prep(sqlId(verboseSqlId("count step1")))
            .count(getCurrentClass().newInstance()) == 200

        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def id2Query = MiscUtil.extractFieldValueFromObj(list.get(0),"id")
        assert qe.prep(sqlId(verboseSqlId("count step2")))
            .count(getCurrentClass(),new Cond("id",id2Query)) == 1
    }

    void exist(){
        logger.info ' -- exist -- '
        assert qe.prep(sqlId(verboseSqlId("exist step1")))
            .exist(getCurrentClass().newInstance())
        assert qe.prep(sqlId(verboseSqlId("exist step2")))
            .exist(getCurrentClass())
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def id2Query = MiscUtil.extractFieldValueFromObj(list.get(0),"id")
        assert qe.prep(sqlId(verboseSqlId("exist step3")))
            .exist(getCurrentClass(),new Cond("id",id2Query))
        def maxId = qe.prep(sqlId(verboseSqlId("exist step4")))
            .genericQry("select max(id) id from ${tableName()}").get(0).get('id')
        def nonExistId = maxId+1
        assert !qe.prep(sqlId(verboseSqlId("exist step5")))
            .exist(getCurrentClass(),new Cond("id",nonExistId))

    }

    void extraCondCount(){
        logger.info ' -- extraCondCount -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def id2Query = MiscUtil.extractFieldValueFromObj(list.get(0),"id")
        assert qe.prep(
            sqlId(verboseSqlId("extraCondCount")),
            addCond([new Cond("id",id2Query)])
        ).count(getCurrentClass().newInstance()) == 1
    }

    void genericQry4Map(){
        logger.info ' -- genericQry4Map -- '
        def clazz = getCurrentClass()
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def id2Query = MiscUtil.extractFieldValueFromObj(list.get(0),"id")
        String sql = "select * from ${tableName()} where id = ?"
        List<Map<String,Object>> result = qe
            .prep(sqlId(verboseSqlId("genericQry4Map step1")))
            .genericQry(sql,id2Query)
        assert result.size() == 1
        def search = clazz.newInstance()
        MiscUtil.setValue(search,"id",id2Query)
        def objRecord = qe.prep(sqlId(verboseSqlId("genericQry4Map step2")))
            .searchObject(search)
        def mapRecord = result.get(0)
        def fields = MiscUtil.getAllFields(clazz)

        TableObjectMetaCache.initTableObjectMeta(clazz,qe)
        def fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(clazz,qe.dataSource)
        fields.each {
            String colName = fieldToColumnMap.get(it.getName())
            def column = it.getAnnotation(Column)
            //if customValue presents, the real column should be column.value() or field name
            if(column&&column.customValue()){
                colName = (column?.value())?:it.name
            }
            it.setAccessible(true)
            def value = it.get(objRecord)
            if(value!=null){
                assert mapRecord.get(colName)!=null
            }
        }
    }

    void genericNamedParamQry(){
        logger.info ' -- genericNamedParamQry -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def record = list.get(0)
        def id = MiscUtil.extractFieldValueFromObj(record,"id")
        String sql = "select * from ${tableName()} where id = :id"
        List<Map<String,Object>> result = qe
            .prep(sqlId(verboseSqlId("genericNamedParamQry step1")))
            .genericNamedParamQry(sql, [id:id])
        assert result.size() == 1

        List<? extends DummyTable> listRt = qe.prep(
            sqlId(verboseSqlId("genericNamedParamQry step2"))
        ).genericNamedParamQry(sql,getCurrentClass(), [id:id])
        assert listRt.size() == 1

        List<String> listRt2 = qe.prep(
            sqlId(verboseSqlId("genericNamedParamQry step3"))
        ).genericNamedParamQry(sql, new ResultSetHandler<List<String>>(){
            @Override
            List<String> handle(ResultSet rs) throws SQLException {
                List<String> tmpList = []
                while (rs.next()){
                    tmpList.add(rs.getObject(1)?.toString())
                }
                return tmpList
            }
        }, [id:id])
        assert listRt2.size() == 1

        String inSql = "select * from ${tableName()} where id = :id and id in (:idList)"
        List<Map<String,Object>> inResult = qe
            .prep(sqlId(verboseSqlId("genericNamedParamQry step4")))
            .genericNamedParamQry(inSql, [id:id,idList:[id,id]])
        assert inResult.size() == 1
    }

    void querySingleAndExist(){
        logger.info ' -- querySingleAndExist -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def record = list.get(0)
        def search = getCurrentClass().newInstance()
        def id = MiscUtil.extractFieldValueFromObj(record,"id")
        search.setId(id)
        def resultRecord = qe.prep(sqlId(verboseSqlId("querySingleAndExist step1")))
            .searchObject(search)
        assert MiscUtil.extractFieldValueFromObj(resultRecord, "id") == id

        def exist = qe.prep(sqlId(verboseSqlId("querySingleAndExist step2")))
            .exist(search)
        assert exist

        search.setId(-1)
        def notExist = !qe.prep(sqlId(verboseSqlId("querySingleAndExist step3")))
            .exist(search)
        assert notExist

        assert qe.prep(sqlId(verboseSqlId("querySingleAndExist step4")))
            .exist(getCurrentClass())
    }

    void selectColumnsTest(){
        logger.info ' -- selectColumnsTest -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def record = list.get(0)
        def search = getCurrentClass().newInstance()
        def id = MiscUtil.extractFieldValueFromObj(record,"id")
        MiscUtil.setValue(search,"id",id)
        def resultRecord = qe.prep(
            selectColumns("id"),
            sqlId(verboseSqlId("selectColumns"))
        ).searchObject(search)
        def mapObject = MiscUtil.mapObject(resultRecord)
        mapObject.each {
            if(it.key=="id"){
                assert it.value!=null
            }else {
                assert it.value==null
            }
        }
    }

    void orderTest(){
        logger.info ' -- orderTest -- '
        List<? extends DummyTable> list = qe.prep(
            order(new OrderCond("id","desc")),
            sqlId(verboseSqlId("order"))
        ).searchObjects(getCurrentClass().newInstance())
        assert list.size() == 200
        def lastId = Integer.MAX_VALUE
        list.each {
            def id = MiscUtil.extractFieldValueFromObj(it,"id")
            assert id < lastId
            lastId = id
        }
    }

    void pagingTest(){
        logger.info ' -- pagingTest -- '
        List<? extends DummyTable> list = qe.prep(
            paging(1,10,true,new OrderCond("id","desc")),
            sqlId(verboseSqlId("paging"))
        ).searchObjects(getCurrentClass().newInstance())
        def count = getTotalCount()
        assert list.size() == 10
        assert count == 200
        def lastId = Integer.MAX_VALUE
        list.each {
            def id = MiscUtil.extractFieldValueFromObj(it,"id")
            assert id < lastId
            lastId = id
        }
    }

    void paging4Map(){
        logger.info ' -- paging4Map -- '
        List<Map<String,Object>> list = qe.prep(
            paging(1,10,true,new OrderCond("id","desc")),
            sqlId(verboseSqlId("paging4Map"))
        ).findObjects(tableName(),[],Map.class)
        def count = getTotalCount()
        assert list.size() == 10
        assert count == 200
        def lastId = Integer.MAX_VALUE
        list.each {
            def id = it.get('id')
            assert id < lastId
            lastId = id
        }
    }

    void offsetTest(){
        logger.info ' -- offset -- '
        List<? extends DummyTable> list = qe.prep(
            offset(0,10,false,new OrderCond("id","desc")),
            sqlId(verboseSqlId("offset step1"))
        ).searchObjects(getCurrentClass().newInstance())
        assert list.size() == 10

        List<? extends DummyTable> otherList = qe.prep(
            offset(3,5,false,new OrderCond("id","desc")),
            sqlId(verboseSqlId("offset step2"))
        ).searchObjects(getCurrentClass().newInstance())
        assert otherList.size() == 5

        def subList = list.subList(3, (5 + 3))
        subList.eachWithIndex { def record, int i ->
            record.getId() == otherList[i].getId()
        }
    }

    void groupByHaving(){
        logger.info ' -- groupByHaving -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def id0 = list.get(0).getId()
        def id1 = list.get(1).getId()
        def id2 = list.get(2).getId()
        List<? extends DummyTable> rtList = qe.prep(
            selectColumns("id","sum(${sumTestField()}) as ${sumTestField()}"),
            groupBy('id'),
            offset(0,2,true,new OrderCond('id','desc')),
            sqlId(verboseSqlId("groupBy step1"))
        ).findObjects(getCurrentClass(),new Cond("id","in",[id0,id1,id2]))
        assert rtList.size() == 2
        assert getTotalCount() == 3

        rtList = qe.prep(
            selectColumns("id","sum(${sumTestField()}) as ${sumTestField()}"),
            groupBy('id'),
            having(new Cond("sum(${sumTestField()})",'!=', -1)),
            offset(0,2,true,new OrderCond('id','desc')),
            sqlId(verboseSqlId("groupBy step2"))
        ).findObjects(getCurrentClass(),new Cond("id","in",[id0,id1,id2]))
        assert rtList.size() == 2
        assert getTotalCount() == 3
    }

    void nameMismatch(){
        logger.info ' -- nameMismatch -- '
        List<? extends DummyTable> list = qe.prep(
            sqlId(verboseSqlId("nameMismatch step1"))
        ).searchObjects(getCurrentClass().newInstance())
        list.each {
            assert it.getMismatchedName()!=null
        }
        def search = getCurrentClass().newInstance()
        MiscUtil.setValue(search,"mismatchedName",list[0].getMismatchedName())
        def result = qe.prep(
            sqlId(verboseSqlId("nameMismatch step2"))
        ).searchObject(search)
        assert result!=null
    }

    void extraCondQuery(){
        logger.info ' -- extraCondQuery -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def record = list.get(0)
        def search = getCurrentClass().newInstance()
        def id = MiscUtil.extractFieldValueFromObj(record,"id")
        def result = qe.prep(
            addCond([new Cond("id",id)]),
            sqlId(verboseSqlId("extraCondQuery"))
        ).searchObject(search)
        assert result.getId() == id
    }

    void orCondQuery(){
        logger.info ' -- orCondQuery -- '
        def search = getCurrentClass().newInstance()
        def result = qe.prep(
            addOrCond([
                new Cond("id", 100),
                new Cond("id","between", [80,120]),
                new Cond("id","in", [121,122]),
                new Cond("id",new Null())
            ]),
            sqlId(verboseSqlId("orCondQuery"))
        ).searchObjects(search)
        result.each {
            def id = it.getId()
            assert id>=80&&id<=122
        }
    }

    void nullCondQuery(){
        logger.info ' -- nullCondQuery -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def record = list.get(0)
        def id = MiscUtil.extractFieldValueFromObj(record,"id")
        def dbRecords = qe.prep(
            sqlId(verboseSqlId("nullCondQuery step1"))
        ).findObjects(getCurrentClass(), new Cond('id', id), new Cond('id', null))
        assert dbRecords.size()==1
        assert dbRecords.get(0).getId() == id


        dbRecords = qe.prep(
            sqlId(verboseSqlId("nullCondQuery step2")),
            addOrCond([new Cond("id", null)])
        ).findObjects(getCurrentClass(), new Cond('id', id))
        assert dbRecords.size()==1
        assert dbRecords.get(0).getId() == id


        dbRecords = qe.prep(
            sqlId(verboseSqlId("nullCondQuery step3"))
        ).findObjects(getCurrentClass(), new Cond('id', id), new Cond('id', 'is not null',null,false))
        assert dbRecords.size()==1
        assert dbRecords.get(0).getId() == id

    }

    void moreQuery(){
        logger.info ' -- moreQuery -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        List<? extends DummyTable> list10 = list.subList(0,10)
        List idList10 = new ArrayList()
        list10.each {
            idList10 << it.getId()
        }
        // test in and not in
        List inCondList = new ArrayList()
        Object[] inCondArr = new Object[10]
        list10.eachWithIndex { DummyTable entry, int i ->
            inCondList << entry.getId()
            inCondArr[i] = entry.getId()
        }
        List<? extends DummyTable> inListRt = qe.prep(
            sqlId(verboseSqlId("moreQuery inCondList"))
        ).findObjects(getCurrentClass(),new Cond.Builder().columnName("id").compareOpr("in").value(inCondList).build())

        List<? extends DummyTable> inArrRt = qe.prep(
            sqlId(verboseSqlId("moreQuery inCondArr"))
        ).findObjects(getCurrentClass(),new Cond("id","in",inCondArr))

        List<? extends DummyTable> notInListRt = qe.prep(
            sqlId(verboseSqlId("moreQuery not inCondList"))
        ).findObjects(getCurrentClass(),new Cond.Builder().columnName("id").compareOpr("not in").value(inCondList).ignoreNull(true).build())

        List<? extends DummyTable> notInArrRt = qe.prep(
            sqlId(verboseSqlId("moreQuery not inCondArr"))
        ).findObjects(getCurrentClass(),new Cond("id","not in",inCondArr))

        assert inListRt.size()==10
        assert inArrRt.size()==10
        list10.eachWithIndex { DummyTable entry, int i ->
            assert entry.getId() == inListRt[i].getId()
            assert entry.getId() == inArrRt[i].getId()
        }
        assert notInListRt.every {
            !idList10.contains(it)
        }
        assert notInArrRt.every {
            !idList10.contains(it)
        }

        //test empty list for "in"
        List<? extends DummyTable> singleListRt = qe.prep(
            sqlId(verboseSqlId("moreQuery singleListRt"))
        ).findObjects(getCurrentClass(),new Cond("id",inCondArr[0]),new Cond("id","in",Collections.emptyList()))
        assert singleListRt.size()==1
        assert singleListRt[0].getId() == inCondArr[0]


        //test between and not between
        List btCondList = [idList10[0],idList10[9]]
        Object[] btCondArr = new Object[]{idList10[0],idList10[9]}

        List<? extends DummyTable> btListRt = qe.prep(
            sqlId(verboseSqlId("moreQuery btCondList"))
        ).findObjects(getCurrentClass(),new Cond("id","between",btCondList))

        List<? extends DummyTable> btArrRt = qe.prep(
            sqlId(verboseSqlId("moreQuery btCondArr"))
        ).findObjects(getCurrentClass(),new Cond("id","between",btCondList))

        List<? extends DummyTable> notBtListRt = qe.prep(
            sqlId(verboseSqlId("moreQuery not btCondList"))
        ).findObjects(getCurrentClass(),new Cond("id","not between",btCondArr))

        List<? extends DummyTable> notBtArrRt = qe.prep(
            sqlId(verboseSqlId("moreQuery not btCondArr"))
        ).findObjects(getCurrentClass(),new Cond("id","not between",btCondArr))

        assert btListRt.size()==10
        assert btArrRt.size()==10
        list10.eachWithIndex { DummyTable entry, int i ->
            assert entry.getId() == btListRt[i].getId()
            assert entry.getId() == btArrRt[i].getId()
        }
        assert notBtListRt.every {
            !idList10.contains(it)
        }
        assert notBtArrRt.every {
            !idList10.contains(it)
        }

        //find object: default limit/offset and non-default limit/offset
        def record1 = qe.prep(
            sqlId(verboseSqlId("moreQuery findObject dft limit/offset 1"))
        ).findObject(getCurrentClass())

        def record2 = qe.prep(
            sqlId(verboseSqlId("moreQuery findObject dft limit/offset 2")),
            offset(0,1,false,new OrderCond('id','desc'))
        ).findObject(getCurrentClass())
        assert record1.getId() != record2.getId()
    }

    void genericT(){
        logger.info ' -- genericT -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def record = list.get(0)
        def id = MiscUtil.extractFieldValueFromObj(record,"id")
        def idCond = new Cond('id', id)
        def records = qe.prep(
            sqlId(verboseSqlId("genericT step1-1"))
        ).findObjectsT([idCond],getCurrentClass())
        assert records[0].getId() == id

        records = qe.prep(
            sqlId(verboseSqlId("genericT step1-2"))
        ).findObjectsT(getCurrentClass(),[idCond])
        assert records[0].getId() == id

        records = qe.prep(
            sqlId(verboseSqlId("genericT step1-3"))
        ).findObjectsT(getCurrentClass(),idCond)
        assert records[0].getId() == id

        records = qe.prep(
            sqlId(verboseSqlId("genericT step1-4"))
        ).findObjectsT(tableName(),[idCond],getCurrentClass())
        assert records[0].getId() == id

        def _record = qe.prep(
            sqlId(verboseSqlId("genericT step2-1"))
        ).findObjectT(getCurrentClass(),idCond)
        assert _record.getId() == id

        _record = qe.prep(
            sqlId(verboseSqlId("genericT step2-2"))
        ).findObjectT([idCond],getCurrentClass())
        assert _record.getId() == id

        _record = qe.prep(
            sqlId(verboseSqlId("genericT step2-3"))
        ).findObjectT(getCurrentClass(),[idCond])
        assert _record.getId() == id

        _record = qe.prep(
            sqlId(verboseSqlId("genericT step2-4"))
        ).findObjectT(tableName(),[idCond],getCurrentClass())
        assert _record.getId() == id

        def search = getCurrentClass().newInstance()
        MiscUtil.setValue(search,"id",id)
        records = qe.prep(
            sqlId(verboseSqlId("genericT step3-1"))
        ).searchObjectsT(search)
        assert records[0].getId() == id

        _record = qe.prep(
            sqlId(verboseSqlId("genericT step3-2"))
        ).searchObjectT(search)
        assert _record.getId() == id
    }

    void otherResultClassTest(){
        logger.info ' -- otherResultClassTest -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def record = list.get(0)
        OnlyId onlyId = qe.prep(
            sqlId(verboseSqlId('otherResultClassTest step1')),
            resultClass(OnlyId.class)
        ).findObject(getCurrentClass(),new Cond('id',record.getId()))
        assert onlyId.getId() == record.getId()
        assert onlyId.uselessField == null
    }

    void colHandleTest(){
        logger.info ' -- colHandleTest -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def record = list.get(0)
        CustomId customId = qe.prep(
            sqlId(verboseSqlId('colHandleTest step1')),
            resultClass(CustomId.class)
        ).findObject(getCurrentClass(),new Cond('id',record.getId()))
        assert customId.id.startsWith('CustomIdHandler-value')

        ResultCastId resultCastId = qe.prep(
            sqlId(verboseSqlId('colHandleTest step2')),
            resultClass(ResultCastId.class)
        ).findObject(getCurrentClass(),new Cond('id',record.getId()))
        assert resultCastId.id.startsWith('IdCastHandler-value')
    }

    void interceptTest(){
        logger.info ' -- interceptTest -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def record = list.get(0)
        boolean inAfterExecution = false
        List<? extends DummyTable> objects = qe.prep(
            sqlId(verboseSqlId('interceptTest step1')),
            offset(0,1,true),
            intercept(new SqlExecutionInterceptor() {
                @Override
                void beforeExecution(InterceptorContext interceptorContext) {
                    String sql = interceptorContext.getSql()
                    Object[] values = interceptorContext.getValues()
                    List<Object> valList = new ArrayList<>()
                    valList << values[0]
                    valList << values[0]
                    if(values.length>1){
                        (1 .. values.length-1).each {
                            valList << values[it]
                        }
                    }
                    interceptorContext.setSql(sql.replace('id = ?', 'id = ? and id != ?'))
                    interceptorContext.setValues(valList.toArray())
                }
                @Override
                void afterExecution(InterceptorContext interceptorContext){
                    inAfterExecution = true
                    assert interceptorContext.getRealResult() instanceof List
                    assert interceptorContext.getTimeCost() != null
                }
            })
        ).findObjects(getCurrentClass(),new Cond('id',record.getId()))
        assert objects.isEmpty()
        assert inAfterExecution

        def fakeDelNum = qe.prep(
            sqlId(verboseSqlId('interceptTest step2_1')),
            intercept(new SqlExecutionInterceptor() {
                @Override
                void beforeExecution(InterceptorContext interceptorContext) {
                    interceptorContext.setDelegatedResult(99)
                }
                @Override
                void afterExecution(InterceptorContext interceptorContext){
                    assert interceptorContext.getRealResult() == null
                    assert interceptorContext.getTimeCost() != null
                }
            })
        ).delObjects(getCurrentClass(), new Cond('id', record.getId()))
        assert fakeDelNum == 99
        assert qe.prep(sqlId(verboseSqlId('interceptTest step2_2'))).exist(getCurrentClass(), new Cond('id', record.getId()))

        List<String> sqlRecord = []
        def fakePersistNum = qe.prep(
            sqlId(verboseSqlId('interceptTest step2_3')),
            intercept(new SqlExecutionInterceptor() {
                @Override
                void beforeExecution(InterceptorContext interceptorContext) {
                    String sql = interceptorContext.getSql()
                    if(sql.startsWith('update')){
                        sqlRecord << sql
                        interceptorContext.setDelegatedResult(0)
                    }else if(sql.startsWith('insert')){
                        sqlRecord << sql
                        interceptorContext.setDelegatedResult(99)
                    }else {
                        throw new DBException('intercept test error, sql='+sql)
                    }
                }
                @Override
                void afterExecution(InterceptorContext interceptorContext){
                    assert interceptorContext.getRealResult() == null
                    assert interceptorContext.getTimeCost() != null
                }
            })
        ).persist(record, new Cond('id', record.getId()))
        assert fakePersistNum == 99
        assert sqlRecord.size() ==2
        assert sqlRecord[0].startsWith('update')
        assert sqlRecord[1].startsWith('insert')
    }

    void interceptWithGlobalTest(){
        logger.info ' -- interceptWithGlobalTest -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def record = list.get(0)
        def config = qe.getCoreRunner().getConfig()
        def oldGlobalSqlExecutionInterceptor = config.getGlobalSqlExecutionInterceptor()
        List<String> interceptorInfoTrack = []
        SqlExecutionInterceptor globalSqlExecutionInterceptor = new SqlExecutionInterceptor() {
            @Override
            void afterExecution(InterceptorContext interceptorContext) {
                interceptorInfoTrack.add('global')
            }
        }
        SqlExecutionInterceptor sqlExecutionInterceptor = new SqlExecutionInterceptor() {
            @Override
            void afterExecution(InterceptorContext interceptorContext) {
                interceptorInfoTrack.add('per')
            }
        }
        config.setGlobalSqlExecutionInterceptor(globalSqlExecutionInterceptor)

        qe.prep(
            sqlId(verboseSqlId('interceptWithGlobalTest step1'))
        ).findObject(getCurrentClass(), new Cond('id', record.getId()))
        assert interceptorInfoTrack.size()==1
        assert interceptorInfoTrack[0] == 'global'

        interceptorInfoTrack.clear()

        qe.prep(
            sqlId(verboseSqlId('interceptWithGlobalTest step2')),
            interceptWithChainMode(sqlExecutionInterceptor, SqlExecutionInterceptorChainMode.CHAIN_AFTER_GLOBAL)
        ).findObject(getCurrentClass(), new Cond('id', record.getId()))
        assert interceptorInfoTrack.size()==2
        assert interceptorInfoTrack[0] == 'global'
        assert interceptorInfoTrack[1] == 'per'

        interceptorInfoTrack.clear()

        qe.prep(
            sqlId(verboseSqlId('interceptWithGlobalTest step3')),
            interceptWithChainMode(sqlExecutionInterceptor, SqlExecutionInterceptorChainMode.CHAIN_BEFORE_GLOBAL)
        ).findObject(getCurrentClass(), new Cond('id', record.getId()))
        assert interceptorInfoTrack.size()==2
        assert interceptorInfoTrack[0] == 'per'
        assert interceptorInfoTrack[1] == 'global'

        interceptorInfoTrack.clear()

        qe.prep(
            sqlId(verboseSqlId('interceptWithGlobalTest step4')),
            interceptWithChainMode(sqlExecutionInterceptor, SqlExecutionInterceptorChainMode.OVERWRITE_GLOBAL)
        ).findObject(getCurrentClass(), new Cond('id', record.getId()))
        assert interceptorInfoTrack.size()==1
        assert interceptorInfoTrack[0] == 'per'

        interceptorInfoTrack.clear()

        config.setGlobalSqlExecutionInterceptor(oldGlobalSqlExecutionInterceptor)
    }

    void loggerLevelTest(){
        logger.info ' -- loggerLevelTest -- '
        def config = qe.getCoreRunner().getConfig()
        def oldLevel = config.getLoggerLevel()
        qe.prep(
            sqlId(verboseSqlId("level debug"))
        ).findObject(getCurrentClass())

        config.setLoggerLevel(LoggerLevel.INFO)
        qe.prep(
            sqlId(verboseSqlId("level info"))
        ).findObject(getCurrentClass())

        config.setLoggerLevel(LoggerLevel.WARN)
        qe.prep(
            sqlId(verboseSqlId("level warn"))
        ).findObject(getCurrentClass())

        config.setLoggerLevel(oldLevel)
    }

    void testRegisterTableObjectMeta(){
        logger.info ' -- testRegisterTableObjectMeta -- '
        qe.registerTableObjectMeta(RegObject,tableName())
        //this register will be ignored because "overwrite" parameter is not set and default to be false
        qe.registerTableObjectMeta(RegObject,'unknown_tbl')
        def fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(RegObject,qe.dataSource)
        def columnToFieldMap = TableObjectMetaCache.getColumnToFieldMap(RegObject,qe.dataSource)
        assert fieldToColumnMap.values().first().toLowerCase() == 'id'
        assert columnToFieldMap.values().first().toLowerCase() == 'id'

        qe.registerTableObjectMeta(true,RegObject,tableName())
        fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(RegObject,qe.dataSource)
        columnToFieldMap = TableObjectMetaCache.getColumnToFieldMap(RegObject,qe.dataSource)
        assert fieldToColumnMap.values().first().toLowerCase() == 'id'
        assert columnToFieldMap.values().first().toLowerCase() == 'id'

    }

    void testRefreshTableMetaCache(){
        logger.info ' -- testRefreshTableMetaCache -- '
        def clazz = getCurrentClass()
        if(classNeedRegMeta()){
            logger.info 'testRefreshTableMetaCache ignored'
            try {
                qe.refreshTableMetaCache(clazz)
                assert false
            } catch (Exception e) {
                assert e instanceof DBException
                assert e.getMessage().contains('refreshCache failed')
            }
            return
        }
        def fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(clazz,qe.dataSource)
        def columnToFieldMap = TableObjectMetaCache.getColumnToFieldMap(clazz,qe.dataSource)
        def primaryFields = TableObjectMetaCache.getPrimaryFields(clazz,qe.dataSource)
        def origSize = primaryFields.size()
        assert MapUtils.isNotEmpty(fieldToColumnMap)
        assert MapUtils.isNotEmpty(columnToFieldMap)
        assert CollectionUtils.isNotEmpty(primaryFields)
        def fieldKey = fieldToColumnMap.keySet()[0]
        def colKey = columnToFieldMap.keySet()[0]
        fieldToColumnMap.remove(fieldKey)
        columnToFieldMap.remove(colKey)
        primaryFields.remove(0)
        qe.refreshTableMetaCache(clazz)
        fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(clazz,qe.dataSource)
        columnToFieldMap = TableObjectMetaCache.getColumnToFieldMap(clazz,qe.dataSource)
        primaryFields = TableObjectMetaCache.getPrimaryFields(clazz,qe.dataSource)
        assert fieldToColumnMap.containsKey(fieldKey)
        assert columnToFieldMap.containsKey(colKey)
        assert primaryFields.size() == origSize
    }

    void testRefreshTableMetaCacheMulti(){
        logger.info ' -- testRefreshTableMetaCacheMulti -- '
        if(classNeedRegMeta()){
            logger.info 'testRefreshTableMetaCacheMulti ignored for '+getCurrentClass()
            return
        }
        logger.info('current class='+getCurrentClass() +' classNeedRegMeta:'+configMap().get("classNeedRegMeta"))
        qe.removeCacheAll()
        def clazz = getCurrentClass()
        def pkgName = clazz.getPackage().getName()
        Set<Class<?>> classes = TableLoc.tableClasses(pkgName,qe.dataSource)
        qe.refreshTableMetaCacheMulti(classes.toArray(new Class[0] as Class[]))
        def tableNameMap = TableObjectMetaCache.getTableNameMap(qe.dataSource)
        classes.each {
            assert tableNameMap.containsKey(it)
            def fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(it,qe.dataSource)
            def columnToFieldMap = TableObjectMetaCache.getColumnToFieldMap(it,qe.dataSource)
            def primaryFields = TableObjectMetaCache.getPrimaryFields(it,qe.dataSource)
            assert MapUtils.isNotEmpty(fieldToColumnMap)
            assert MapUtils.isNotEmpty(columnToFieldMap)
            assert CollectionUtils.isNotEmpty(primaryFields)
        }
    }

    void testRefreshTableMetaCacheForPackage(){
        logger.info ' -- testRefreshTableMetaCacheForPackage -- '
        if(classNeedRegMeta()){
            logger.info 'testRemoveAllTableMetaCache ignored'
            return
        }
        qe.removeCacheAll()
        def clazz = getCurrentClass()
        def pkgName = clazz.getPackage().getName()
        Set<Class<?>> classes = TableLoc.tableClasses(pkgName,qe.dataSource)
        def excludedClass = classes[0]
        classes.remove(excludedClass)
        qe.refreshTableMetaCacheForPackage(pkgName,excludedClass)
        def tableNameMap = TableObjectMetaCache.getTableNameMap(qe.dataSource)
        classes.each {
            assert tableNameMap.containsKey(it)
            def fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(it,qe.dataSource)
            def columnToFieldMap = TableObjectMetaCache.getColumnToFieldMap(it,qe.dataSource)
            def primaryFields = TableObjectMetaCache.getPrimaryFields(it,qe.dataSource)
            assert MapUtils.isNotEmpty(fieldToColumnMap)
            assert MapUtils.isNotEmpty(columnToFieldMap)
            assert CollectionUtils.isNotEmpty(primaryFields)
        }
        assert !tableNameMap.containsKey(excludedClass)
        assert MapUtils.isEmpty(TableObjectMetaCache.getFieldToColumnMap(excludedClass,qe.dataSource))
        assert MapUtils.isEmpty(TableObjectMetaCache.getColumnToFieldMap(excludedClass,qe.dataSource))
        assert CollectionUtils.isEmpty(TableObjectMetaCache.getPrimaryFields(excludedClass,qe.dataSource))
    }

    void testRemoveAllTableMetaCache(){
        logger.info ' -- testRemoveAllTableMetaCache -- '
        if(classNeedRegMeta()){
            logger.info 'testRemoveAllTableMetaCache ignored'
            return
        }
        def clazz = getCurrentClass()
        qe.removeCacheAll()
        def fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(clazz,qe.dataSource)
        def columnToFieldMap = TableObjectMetaCache.getColumnToFieldMap(clazz,qe.dataSource)
        def primaryFields = TableObjectMetaCache.getPrimaryFields(clazz,qe.dataSource)
        def tableNameMap = TableObjectMetaCache.getTableNameMap(qe.dataSource)
        assert MapUtils.isEmpty(fieldToColumnMap)
        assert MapUtils.isEmpty(columnToFieldMap)
        assert CollectionUtils.isEmpty(primaryFields)
        assert MapUtils.isEmpty(tableNameMap)
    }

    void testFieldInfoGetter(){
        logger.info ' -- testFieldInfoGetter -- '
        new FieldInfoGetterGroovyTest(commonTest: this).test()
        new FieldInfoGetterJavaTest(this).test()
    }

    void insertOne(){
        logger.info ' -- insertOne -- '
        def record = MiscUtil.getFirst(CommonTool.generateDummyRecords(getCurrentClass(), 1))
        def insertNum = qe.prep(
            sqlId(verboseSqlId("insertOne"))
        ).insert(record)
        assert insertNum == 1
    }

    void nullCond(){
        logger.info ' -- nullCond -- '
        def clazz = getCurrentClass()
        def record = MiscUtil.getFirst(CommonTool.generateDummyRecords(clazz, 1))
        MiscUtil.setValue(record,nullField4Test()[0],null)
        qe.prep(
            sqlId(verboseSqlId("nullCond step1"))
        ).insert(record)
        def result = qe.prep(
            offset(0,1,false,new OrderCond("id","desc")),
            sqlId(verboseSqlId("nullCond step2"))
        ).findObject(clazz, new Cond(nullField4Test()[1], new Null()))
        def fields = MiscUtil.getAllFields(clazz)
        fields.each {
            it.setAccessible(true)
            def origValue = it.get(record)
            def resultValue = it.get(result)
            if(it.name != "id"){
                compareValueEqual(origValue,resultValue)
            }
        }
    }

    void genericNamedParamUpdate(){
        logger.info ' -- genericNamedParamUpdate -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def id2Update = MiscUtil.extractFieldValueFromObj(list.get(0),"id")
        String sql = "update ${tableName()} set ${nullField4Test()[1]} = :val where id = :id"
        def updateNum = qe.genericNamedParamUpdate(sql, [val: null, id: id2Update])
        assert updateNum == 1
    }

    void updateSelective(){
        logger.info ' -- updateSelective -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def id2Update = MiscUtil.extractFieldValueFromObj(list.get(0),"id")
        def condObj = getCurrentClass().newInstance()
        condObj.setId(id2Update)
        def origRecord = qe.prep(
            sqlId(verboseSqlId("updateSelective step1"))
        ).searchObject(condObj)
        def record = MiscUtil.getFirst(CommonTool.generateDummyRecords(getCurrentClass(), 1))
        MiscUtil.setValue(record,nullField4Test()[0],null)
        def updateNum = qe.prep(
            sqlId(verboseSqlId("updateSelective step2"))
        ).updateSelectiveAutoCond(record, condObj)
        assert updateNum == 1
        def updatedRecord = qe.prep(
            sqlId(verboseSqlId("updateSelective step3"))
        ).searchObject(condObj)
        assert MiscUtil.extractFieldValueFromObj(origRecord,nullField4Test()[0]) == MiscUtil.extractFieldValueFromObj(updatedRecord,nullField4Test()[0])
        record.setId(id2Update)
        updateNum = qe.prep(
            sqlId(verboseSqlId("updateSelective step4"))
        ).updateSelectiveByPrimary(record)
        assert updateNum == 1
        updatedRecord = qe.prep(
            sqlId(verboseSqlId("updateSelective step5"))
        ).searchObject(condObj)
        assert MiscUtil.extractFieldValueFromObj(origRecord,nullField4Test()[0]) == MiscUtil.extractFieldValueFromObj(updatedRecord,nullField4Test()[0])

    }

    void extraCondUpdateSelective(){
        logger.info ' -- updateSelective -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def id2Update = MiscUtil.extractFieldValueFromObj(list.get(0),"id")
        def condObj = getCurrentClass().newInstance()
        condObj.setId(id2Update)
        def origRecord = qe.prep(
            sqlId(verboseSqlId("updateSelective step1"))
        ).searchObject(condObj)
        def record = MiscUtil.getFirst(CommonTool.generateDummyRecords(getCurrentClass(), 1))
        MiscUtil.setValue(record,nullField4Test()[0],null)
        def updateNum = qe.prep(
            sqlId(verboseSqlId("updateSelective step2")),
            addCond([new Cond("id",id2Update)])
        ).updateSelective(record)
        assert updateNum == 1
        def updatedRecord = qe.prep(
            sqlId(verboseSqlId("updateSelective step3"))
        ).searchObject(condObj)
        assert MiscUtil.extractFieldValueFromObj(origRecord,nullField4Test()[0]) == MiscUtil.extractFieldValueFromObj(updatedRecord,nullField4Test()[0])
    }

    void updateSelectiveByFieldOrColumn(){
        logger.info ' -- updateSelectiveByFieldOrColumn -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def id = MiscUtil.extractFieldValueFromObj(list.get(0),"id")
        def dbRecord = qe.prep(
            sqlId(verboseSqlId("updateSelectiveByFieldOrColumn step1"))
        ).findObject(getCurrentClass(),new Cond("id",id))
        def condValue = MiscUtil.extractFieldValueFromObj(dbRecord,"mismatchedName")
        def record2Update = getCurrentClass().newInstance()
        MiscUtil.setValue(record2Update,"mismatchedName",condValue)
        MiscUtil.setValue(record2Update,nullField4Test()[0],"valueByField")
        def updateNum = qe.prep(
            sqlId(verboseSqlId("updateSelectiveByFieldOrColumn step2"))
        ).updateSelectiveConcise(record2Update,"mismatchedName")
        assert updateNum == 1
        dbRecord = qe.prep(
            sqlId(verboseSqlId("updateSelectiveByFieldOrColumn step3"))
        ).findObject(getCurrentClass(),new Cond("id",id))
        assert MiscUtil.extractFieldValueFromObj(dbRecord,nullField4Test()[0]) == "valueByField"

        MiscUtil.setValue(record2Update,nullField4Test()[0],"valueByColumn")
        updateNum = qe.prep(
            sqlId(verboseSqlId("updateSelectiveByFieldOrColumn step4"))
        ).updateSelectiveConcise(record2Update,"name_mismatch_f")
        assert updateNum == 1
        dbRecord = qe.prep(
            sqlId(verboseSqlId("updateSelectiveByFieldOrColumn step5"))
        ).findObject(getCurrentClass(),new Cond("id",id))
        assert MiscUtil.extractFieldValueFromObj(dbRecord,nullField4Test()[0]) == "valueByColumn"

    }

    void updateFull(){
        logger.info ' -- updateFull -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def id2Update = MiscUtil.extractFieldValueFromObj(list.get(0),"id")
        def condObj = getCurrentClass().newInstance()
        condObj.setId(id2Update)
        def origRecord = qe.prep(
            sqlId(verboseSqlId("updateFull step1"))
        ).searchObject(condObj)
        def record = MiscUtil.getFirst(CommonTool.generateDummyRecords(getCurrentClass(), 1))
        MiscUtil.setValue(record,nullField4Test()[0],null)
        def fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(getCurrentClass(),qe.dataSource)
        String idCol = fieldToColumnMap.get('id')
        def updateNum = qe.prep(
            sqlId(verboseSqlId("updateFull step2"))
        ).updateFull(record, condObj,idCol)
        assert updateNum == 1
        def updatedRecord = qe.prep(
            sqlId(verboseSqlId("updateFull step3"))
        ).searchObject(condObj)
        assert MiscUtil.extractFieldValueFromObj(origRecord,nullField4Test()[0]) != null
        assert MiscUtil.extractFieldValueFromObj(updatedRecord,nullField4Test()[0]) == null
        updateNum = qe.prep(
            sqlId(verboseSqlId("updateFull step4"))
        ).update(tableName(),[(nullField4Test()[1]):'X'],[new Cond(idCol,id2Update)])
        assert updateNum == 1
        updatedRecord = qe.prep(
            sqlId(verboseSqlId("updateFull step5"))
        ).searchObject(condObj)
        assert 'X' == MiscUtil.extractFieldValueFromObj(updatedRecord,nullField4Test()[0])
        record.setId(id2Update)
        updateNum = qe.prep(
            sqlId(verboseSqlId("updateFull step6"))
        ).updateFullByPrimary(record, idCol)
        assert updateNum == 1
        updatedRecord = qe.prep(
            sqlId(verboseSqlId("updateFull step7"))
        ).searchObject(condObj)
        assert MiscUtil.extractFieldValueFromObj(updatedRecord,nullField4Test()[0]) == null

    }

    void extraCondUpdateFull(){
        logger.info ' -- extraCondUpdateFull -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def id2Update = MiscUtil.extractFieldValueFromObj(list.get(1),"id")
        def condObj = getCurrentClass().newInstance()
        condObj.setId(id2Update)
        def origRecord = qe.prep(
            sqlId(verboseSqlId("extraCondUpdateFull step1"))
        ).searchObject(condObj)
        def record = MiscUtil.getFirst(CommonTool.generateDummyRecords(getCurrentClass(), 1))
        MiscUtil.setValue(record,nullField4Test()[0],null)
        def fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(getCurrentClass(),qe.dataSource)
        String idCol = fieldToColumnMap.get('id')
        def updateNum = qe.prep(
            sqlId(verboseSqlId("extraCondUpdateFull step2")),
            addCond([new Cond("id",id2Update)])
        ).updateFull(record, [], idCol)
        assert updateNum == 1
        def updatedRecord = qe.prep(
            sqlId(verboseSqlId("extraCondUpdateFull step3"))
        ).searchObject(condObj)
        assert MiscUtil.extractFieldValueFromObj(origRecord,nullField4Test()[0]) != null
        assert MiscUtil.extractFieldValueFromObj(updatedRecord,nullField4Test()[0]) == null
    }

    void persist(){
        logger.info ' -- persist -- '
        def record = MiscUtil.getFirst(CommonTool.generateDummyRecords(getCurrentClass(), 1))
        def id = idInt()?1000000:System.currentTimeMillis()
        record.setId(null)
        def condObj = getCurrentClass().newInstance()
        condObj.setId(id)
        int origCount = qe.prep(
            sqlId(verboseSqlId("persist step1"))
        ).count(currentClass)
        def persistNum = qe.prep(
            sqlId(verboseSqlId("persist step2"))
        ).persistAutoCond(record, condObj)
        assert persistNum == 1
        int afterPersistInsertCount = qe.prep(
            sqlId(verboseSqlId("persist step3"))
        ).count(currentClass)
        assert afterPersistInsertCount - origCount == 1
        def maxId = qe.prep(
            sqlId(verboseSqlId("persist step4"))
        ).genericQry("select max(id) id from ${tableName()}").get(0).get('id')
        def fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(getCurrentClass(),qe.dataSource)
        String idCol = fieldToColumnMap.get('id')
        persistNum = qe.prep(
            sqlId(verboseSqlId("persist step5"))
        ).persist(record,new Cond(idCol,maxId))
        assert persistNum == 1
        int afterPersistUpdateCount = qe.prep(
            sqlId(verboseSqlId("persist step6"))
        ).count(currentClass)
        assert afterPersistUpdateCount == afterPersistInsertCount
    }

    void persistNoCondFail(){
        logger.info ' -- persist -- '
        def record = MiscUtil.getFirst(CommonTool.generateDummyRecords(getCurrentClass(), 1))
        try {
            qe.persist(record,new Cond[0])
            assert false
        } catch (Exception e) {
            assert e instanceof DBException
            assert e.getMessage().contains("conditions can't be empty for persist")
        }
    }

    void insertAndReturnAutoGen(){
        logger.info ' -- insertAndReturnAutoGen -- '
        def clazz = getCurrentClass()
        def record = MiscUtil.getFirst(CommonTool.generateDummyRecords(clazz, 1))
        def autoGenValue = qe.prep(
            sqlId(verboseSqlId("insertAndReturnAutoGen step1"))
        ).insertAndReturnAutoGen(record)
        assert autoGenValue!=null
        if(autoGenValue instanceof BigInteger || autoGenValue instanceof BigDecimal){
            autoGenValue = autoGenValue.longValue()
        }
        def resultRecord = qe.prep(
            sqlId(verboseSqlId("insertAndReturnAutoGen step2"))
        ).findObject(clazz, new Cond("id", autoGenValue))
        record.setId(autoGenValue)
        def fields = MiscUtil.getAllFields(clazz)
        fields.each {
            it.setAccessible(true)
            def origValue = it.get(record)
            def resultValue = it.get(resultRecord)
            compareValueEqual(origValue,resultValue)
        }

        record = MiscUtil.getFirst(CommonTool.generateDummyRecords(clazz, 1))
        autoGenValue = qe.prep(
            sqlId(verboseSqlId("insertAndReturnAutoGen step3"))
        ).insertAndReturnAutoGen(record,1)
        assert autoGenValue!=null

        record = MiscUtil.getFirst(CommonTool.generateDummyRecords(clazz, 1))
        autoGenValue = qe.prep(
            sqlId(verboseSqlId("insertAndReturnAutoGen step4"))
        ).insertAndReturnAutoGen(record,autoGenColName())
        assert autoGenValue!=null
    }

    void delOne(){
        logger.info ' -- delOne -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def id2Del = MiscUtil.extractFieldValueFromObj(list.get(0),"id")
        def del = getCurrentClass().newInstance()
        MiscUtil.setValue(del,"id",id2Del)
        def delNum = qe.prep(
            sqlId(verboseSqlId("delOne"))
        ).delObjects(del)
        assert delNum == 1
    }

    void extraCondDel(){
        logger.info ' -- extraCondDel -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def id2Del = MiscUtil.extractFieldValueFromObj(list.get(1),"id")
        def del = getCurrentClass().newInstance()
        def delNum = qe.prep(
            sqlId(verboseSqlId("extraCondDel")),
            addCond([new Cond("id",id2Del)])
        ).delObjects(del)
        assert delNum == 1
    }

    void delNoCondFail(){
        logger.info ' -- delNoCondFail -- '
        try {
            qe.delObjects(getCurrentClass())
            assert false
        } catch (Exception e) {
            assert e instanceof DBException
            assert e.getMessage().contains('Delete without condition')
        }
    }

    void batchInsertVarArg(){
        logger.info ' -- batchInsertVarArg -- '
        List<? extends DummyTable> list = CommonTool.generateDummyRecords(getCurrentClass(),2)
        def insertNum = qe.prep(
            sqlId(verboseSqlId("batchInsertVarArg"))
        ).batchInsert(list.get(0),list.get(1))
        assert insertNum == 2
    }

    void emptyCondBlock(){
        logger.info ' -- emptyCondBlock -- '
        List<? extends DummyTable> list = GeneralThreadLocal.get("allRecords")
        def record = list.get(0)
        SqlExecutionInterceptor interceptor = new SqlExecutionInterceptor() {
            @Override
            void beforeExecution(InterceptorContext interceptorContext) {
                interceptorContext.setDelegatedResult(1)
            }
        }
        try {
            qe.prep(
                sqlId(verboseSqlId("emptyCondBlock step1")),
                intercept(interceptor)
            ).updateSelective(record)
            assert false
        } catch (e) {
            assert e instanceof DBException
            assert e.getMessage().contains('Update without condition')
        }
        try {
            qe.prep(
                sqlId(verboseSqlId("emptyCondBlock step2")),
                intercept(interceptor)
            ).delObjects(getCurrentClass())
            assert false
        } catch (e) {
            assert e instanceof DBException
            assert e.getMessage().contains('Delete without condition')
        }
        try {
            qe.prep(
                sqlId(verboseSqlId("emptyCondBlock step3")),
                intercept(interceptor)
            ).updateSelective(record,new Cond('id',null))
            assert false
        } catch (e) {
            assert e instanceof DBException
            assert e.getMessage().contains('Update without condition')
        }
        try {
            qe.prep(
                sqlId(verboseSqlId("emptyCondBlock step4")),
                intercept(interceptor)
            ).delObjects(getCurrentClass(),new Cond('id',null))
            assert false
        } catch (e) {
            assert e instanceof DBException
            assert e.getMessage().contains('Delete without condition')
        }
    }

    void delAll(){
        logger.info ' -- delAll -- '
        def del = getCurrentClass().newInstance()
        def delNum = qe.prep(
            sqlId(verboseSqlId("delAll")),
            allowEmptyUpdateCond()
        ).delObjects(del)
        assert delNum > 0
    }

    void tx(){
        if(classNeedRegMeta()){
            // class registered under different datasource (tx context is with TransactionAwareDataSourceProxy)
            return
        }
        logger.info ' -- tx -- '

        String createTableTemplate = CommonInfo.createTableMap.get(getDbType())
        String createTableSql = createTableTemplate.replace("TABLE_PLACEHOLDER",tableName())
        Closure clSetup = { QueryEntry queryEntry ->
            queryEntry.prep(
                sqlId("tx 000")
            ).genericUpdate(createTableSql)
        }

        Closure clNormal = { QueryEntry queryEntry ->
            def record = MiscUtil.getFirst(CommonTool.generateDummyRecords(getCurrentClass(), 1))
            queryEntry.prep(
                sqlId(verboseSqlId("tx 001"))
            ).insert(record)
        }
        Closure<Boolean> validNormal = { QueryEntry queryEntry ->
            return queryEntry.prep(
                sqlId(verboseSqlId("tx 002"))
            ).searchObjects(getCurrentClass().newInstance()).size() == 1
        }
        assert TransactionTest.tx(getDbType(),clSetup,clNormal,validNormal)
        qe.prep(
            sqlId(verboseSqlId("tx 003")),
            allowEmptyUpdateCond()
        ).delObjects(getCurrentClass().newInstance())

        Closure clException = { QueryEntry queryEntry ->
            def record = MiscUtil.getFirst(CommonTool.generateDummyRecords(getCurrentClass(), 1))
            queryEntry.prep(
                sqlId(verboseSqlId("tx 004"))
            ).insert(record)

            queryEntry.prep(
                sqlId(verboseSqlId("tx 005"))
            ).updateSelective(record,new Cond("field_no_exist","xxx"))
        }
        Closure<Boolean> validException = { QueryEntry queryEntry ->
            return queryEntry.prep(
                sqlId(verboseSqlId("tx 006"))
            ).searchObjects(getCurrentClass().newInstance()).size() == 0
        }
        assert TransactionTest.tx(getDbType(),clSetup,clException,validException)
    }

    String verboseSqlId(String sqlId){
        return "${getQueryEntryDbType()}|${getDbType()} - ${currentClass.simpleName} - ${tableName()}:$sqlId"
    }

    static void compareValueEqual(Object origValue, Object resultValue){
        if(origValue!=null){
            if(origValue instanceof Double){
                assert ((Double) origValue).intValue() == ((Double) resultValue).intValue()
            }else if(origValue instanceof Date){
                assert resultValue !=null
            }else if(origValue instanceof String){
                assert ((String)origValue).trim() == ((String)resultValue).trim()
            } else {
                assert origValue == resultValue
            }
        }
    }
}