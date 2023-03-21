package io.github.dynamixon.dataobject;

import io.github.dynamixon.flexorm.annotation.ResultCast;
import io.github.dynamixon.test.colhandle.IdCastHandler;
import lombok.Data;

/**
 * for BeanFieldColumnHandler spi test
 * @author maojianfeng
 * @date 22-7-8
 */
@Data
public class ResultCastId {
    @ResultCast(IdCastHandler.class)
    private String id;

    private String uselessField;
}
