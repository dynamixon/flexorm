package io.github.dynamixon.test.colhandle;

import io.github.dynamixon.dataobject.CustomId;
import io.github.dynamixon.flexorm.columnhandler.BeanFieldColumnHandler;
import io.github.dynamixon.flexorm.pojo.BeanClassFieldNameKey;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomIdHandler implements BeanFieldColumnHandler<String> {
    @Override
    public BeanClassFieldNameKey id() {
        return new BeanClassFieldNameKey(CustomId.class,"id");
    }

    @Override
    public String apply(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex);
        return "CustomIdHandler-value:"+value;
    }
}
