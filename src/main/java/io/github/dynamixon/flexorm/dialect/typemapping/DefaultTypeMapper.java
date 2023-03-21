package io.github.dynamixon.flexorm.dialect.typemapping;

import io.github.dynamixon.flexorm.dialect.DialectConst;

import java.util.HashMap;
import java.util.Map;

public class DefaultTypeMapper implements TypeMapper {
    @Override
    public String getDatabaseType() {
        return DialectConst.DEFAULT;
    }
    @Override
    public Map<String, String> getTypeMap() {
        return new HashMap<>();
    }
}
