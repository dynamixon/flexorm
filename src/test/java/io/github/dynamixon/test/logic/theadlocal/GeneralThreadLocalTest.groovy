package io.github.dynamixon.test.logic.theadlocal

import io.github.dynamixon.flexorm.misc.GeneralThreadLocal
import io.github.dynamixon.test.logic.LogicTestBase
import org.junit.Test

class GeneralThreadLocalTest implements LogicTestBase {
    @Test
    @Override
    void test() {
        def origMap = GeneralThreadLocal.get()
        try {
            nullSafeTest()
        } finally {
            GeneralThreadLocal.set(origMap)
        }
    }

    static void nullSafeTest(){
        Map<String,Object> map = new HashMap<>()
        map.put(null,'123')
        map.put('123',null)
        map.put('qwe','asd')
        GeneralThreadLocal.set(map)
        assert GeneralThreadLocal.get('qwe') == 'asd'

        GeneralThreadLocal.unset()
        assert GeneralThreadLocal.get('qwe') == null

        GeneralThreadLocal.set('test-key',null)
        assert GeneralThreadLocal.get('test-key') == null

        GeneralThreadLocal.set(null,'test-val')
        assert GeneralThreadLocal.get(null) == null

        GeneralThreadLocal.set('test-key','test-val')
        assert GeneralThreadLocal.get('test-key') == 'test-val'

        GeneralThreadLocal.unset(null)
        assert true

        GeneralThreadLocal.unset('test-key')
        assert GeneralThreadLocal.get('test-key') == null
    }
}
