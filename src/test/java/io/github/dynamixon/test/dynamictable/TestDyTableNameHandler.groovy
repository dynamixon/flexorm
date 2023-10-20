package io.github.dynamixon.test.dynamictable

import io.github.dynamixon.flexorm.annotation.Table
import io.github.dynamixon.flexorm.misc.GeneralThreadLocal
import io.github.dynamixon.flexorm.misc.TableNameHandler


class TestDyTableNameHandler implements TableNameHandler{
    @Override
    String handle(Class<?> tableClass){
        Table table = tableClass.getAnnotation(Table.class);
        def origTableName = table.value()
        String dynamicPart = GeneralThreadLocal.get('tbl_dynamic_part')
        return origTableName.replace('PLACEHOLDER',dynamicPart)
    }
}
