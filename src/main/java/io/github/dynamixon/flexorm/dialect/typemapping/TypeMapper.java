package io.github.dynamixon.flexorm.dialect.typemapping;

import io.github.dynamixon.flexorm.dialect.DatabaseTypeMatcher;

import java.util.Map;

public interface TypeMapper extends DatabaseTypeMatcher {
    Map<String,String> getTypeMap();
}
