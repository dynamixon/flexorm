package io.github.dynamixon.flexorm.pojo;

import java.io.Serializable;

/**
 * @author Jianfeng.Mao2
 * @date 23-12-15
 */
public interface FieldInfoGetter<T> extends Serializable {

    Object get(T source);
}
