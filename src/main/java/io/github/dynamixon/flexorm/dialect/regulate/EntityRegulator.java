package io.github.dynamixon.flexorm.dialect.regulate;

import io.github.dynamixon.flexorm.dialect.DatabaseTypeMatcher;

public interface EntityRegulator extends DatabaseTypeMatcher {
    default String simpleTable(String table){
        return table;
    }
}
