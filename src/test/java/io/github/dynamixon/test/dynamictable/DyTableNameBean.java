package io.github.dynamixon.test.dynamictable;

import io.github.dynamixon.flexorm.annotation.Table;
import lombok.Data;

/**
 * @author Jianfeng.Mao2
 * @date 23-10-20
 */
@Data
@Table(value = "tbl_PLACEHOLDER",tableNameDynamic = true, tableNameHandlerClass = TestDyTableNameHandler.class, autoColumnDetection = true)
public class DyTableNameBean {

    private String id;
}
