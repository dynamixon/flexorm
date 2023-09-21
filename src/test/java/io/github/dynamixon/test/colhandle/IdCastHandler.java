package io.github.dynamixon.test.colhandle;

import io.github.dynamixon.flexorm.columnhandler.ResultCastHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IdCastHandler implements ResultCastHandler<String> {

    @Override
    public String apply(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex);
        return "IdCastHandler-value:"+value;
    }
}
