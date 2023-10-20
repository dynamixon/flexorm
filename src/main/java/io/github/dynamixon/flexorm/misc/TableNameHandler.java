package io.github.dynamixon.flexorm.misc;


import io.github.dynamixon.flexorm.annotation.Table;

/**
 * @author Jianfeng.Mao2
 * @date 23-10-19
 */
public interface TableNameHandler {

    default String handle(Class<?> tableClass){
        Table table = tableClass.getAnnotation(Table.class);
        if(table==null){
            throw new DBException("Handle table name failed: tableClass:"+ tableClass.getName() +" has no @Table annotation!");
        }
        return table.value();
    }
}
