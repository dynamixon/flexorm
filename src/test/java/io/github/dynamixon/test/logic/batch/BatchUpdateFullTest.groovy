package io.github.dynamixon.test.logic.batch

import io.github.dynamixon.flexorm.QueryEntry
import io.github.dynamixon.flexorm.dialect.DialectConst
import io.github.dynamixon.flexorm.logic.TableObjectMetaCache
import io.github.dynamixon.flexorm.misc.GeneralThreadLocal
import io.github.dynamixon.flexorm.misc.MiscUtil
import io.github.dynamixon.flexorm.pojo.Cond
import io.github.dynamixon.flexorm.pojo.CondCrafter
import io.github.dynamixon.moredata.LogicTableA
import io.github.dynamixon.test.CommonInfo
import io.github.dynamixon.test.logic.LogicTestBase
import org.apache.commons.lang3.tuple.Pair
import org.apache.commons.lang3.tuple.Triple
import org.junit.Test

class BatchUpdateFullTest implements LogicTestBase{

    private static final String BATCH_UPDATE_FULL_PARAM_TL = '##batchUpdateTriples'

    private static QueryEntry queryEntry = new DummyUpdateQueryEntry()

    @Test
    @Override
    void test() {
        batchUpdateFullByPrimary()
        batchUpdateFullAutoCond()
        batchUpdateFullWithCondCrafter()
    }

    static void batchUpdateFullByPrimary(){
        try {
            queryEntry.batchUpdateFullByPrimary([
                    Pair.of(new LogicTableA(id: 1L, intF: 999),['double_f']),
                    Pair.of(new LogicTableA(id: 2L, intF: 666),['boolean_f'])
            ])
            basicAssert()
        } finally {
            GeneralThreadLocal.unset(BATCH_UPDATE_FULL_PARAM_TL)
        }
    }

    static void batchUpdateFullAutoCond(){
        try {
            queryEntry.batchUpdateFullAutoCond([
                    Triple.of(new LogicTableA(intF: 999),new LogicTableA(id: 1L),['id', 'double_f']),
                    Triple.of(new LogicTableA(intF: 666),new LogicTableA(id: 2L),['id', 'boolean_f'])
            ])
            basicAssert()
        } finally {
            GeneralThreadLocal.unset(BATCH_UPDATE_FULL_PARAM_TL)
        }
    }

    static void batchUpdateFullWithCondCrafter(){
        CondCrafter<List<Cond>> crafter = c -> c
        queryEntry.batchUpdateFullWithCondCrafter([
                Pair.of(Triple.of(new LogicTableA(intF: 999),crafter,[new Cond(columnName: 'id', value: 1L)]),['id', 'double_f']),
                Pair.of(Triple.of(new LogicTableA(intF: 666),crafter,[new Cond(columnName: 'id', value: 2L)]),['id', 'boolean_f'])
        ])
        basicAssert()
    }

    static void basicAssert(){
        List<Triple<String,Map<String, Object>, List<Cond>>> batchUpdateTriples = GeneralThreadLocal.get(BATCH_UPDATE_FULL_PARAM_TL)
        def colValMap = getColValMap(new LogicTableA(id: 1L, intF: 999), ['id', 'double_f'])
        mapEquals(colValMap, batchUpdateTriples[0].getMiddle())
        assert batchUpdateTriples[0].getRight()[0].columnName == 'id'
        assert batchUpdateTriples[0].getRight()[0].value == 1L
        colValMap = getColValMap(new LogicTableA(id: 2L, intF: 666), ['id', 'boolean_f'])
        mapEquals(colValMap, batchUpdateTriples[1].getMiddle())
        assert batchUpdateTriples[1].getRight()[0].columnName == 'id'
        assert batchUpdateTriples[1].getRight()[0].value == 2L
    }

    static Map<String,Object> getColValMap(LogicTableA logicTableA, List<String> ignoredCols){
        def mapObject = MiscUtil.mapObject(logicTableA)
        def fieldToColumnMap = TableObjectMetaCache.getFieldToColumnMap(LogicTableA.class, queryEntry.getDataSource())
        Map<String,Object> colValMap = fieldToColumnMap.collectEntries {[(it.value),mapObject[it.key]]}
        colValMap.removeAll { it.key in ignoredCols}
        return colValMap
    }

    static void mapEquals(Map<String,Object> map1, Map<String,Object> map2){
        assert map1.size() == map2.size()
        map1.each {
            assert map2.get(it.key) == it.value
        }
    }

    static class DummyUpdateQueryEntry extends QueryEntry{

        DummyUpdateQueryEntry() {
            super(CommonInfo.getDataSource(DialectConst.H2))
        }
        @Override
        int[] batchUpdate(List<Triple<String,Map<String, Object>, List<Cond>>> batchUpdateTriples) {
            GeneralThreadLocal.set(BATCH_UPDATE_FULL_PARAM_TL,batchUpdateTriples)
            return new int[0]
        }
    }
}
