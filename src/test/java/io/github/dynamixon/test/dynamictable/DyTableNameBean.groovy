package io.github.dynamixon.test.dynamictable;

import io.github.dynamixon.flexorm.annotation.Table

/**
 * @author Jianfeng.Mao2
 * @date 23-10-20
 */
@Table(value = "tbl_PLACEHOLDER",tableNameDynamic = true, tableNameHandlerClass = TestDyTableNameHandler.class, autoColumnDetection = true)
class DyTableNameBean {

    private String id

    String getId() {
        return id
    }

    void setId(String id) {
        this.id = id
    }
}
