package io.github.dynamixon.flexorm.columnhandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultCastHandler {
    Object apply(ResultSet rs, int columnIndex) throws SQLException;
}
