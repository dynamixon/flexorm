package io.github.dynamixon.dataobject;

import io.github.dynamixon.flexorm.annotation.Table;
import lombok.Data;

/**
 * for BeanFieldColumnHandler spi test
 * @author maojianfeng
 * @date 22-7-8
 */
@Table(value = "dummy_table",autoColumnDetection = true)
@Data
public class CustomId {
    private String id;

    private String uselessField;
}
