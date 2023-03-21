package io.github.dynamixon.flexorm.columnhandler;

import io.github.dynamixon.flexorm.pojo.BeanClassFieldNameKey;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BeanFieldColumnHandler {

    BeanClassFieldNameKey id();

    Object apply(ResultSet rs, int columnIndex) throws SQLException;
}
