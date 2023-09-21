package io.github.dynamixon.flexorm.columnhandler;

import io.github.dynamixon.flexorm.pojo.BeanClassFieldNameKey;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BeanFieldColumnHandler<T> {

    BeanClassFieldNameKey id();

    T apply(ResultSet rs, int columnIndex) throws SQLException;
}
