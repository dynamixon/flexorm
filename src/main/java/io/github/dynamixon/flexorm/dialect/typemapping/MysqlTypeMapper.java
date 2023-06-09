package io.github.dynamixon.flexorm.dialect.typemapping;

import io.github.dynamixon.flexorm.dialect.DialectConst;

import java.util.Map;
import java.util.TreeMap;

public class MysqlTypeMapper implements TypeMapper {
    @Override
    public String getDatabaseType() {
        return DialectConst.MYSQL;
    }
    @Override
    public Map<String, String> getTypeMap() {
        Map<String,String> typeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        typeMap.put("TINYINT","Integer");
        typeMap.put("SMALLINT","Integer");
        typeMap.put("YEAR","Integer");
        typeMap.put("INT","Integer");
        typeMap.put("BIT","Boolean");
        typeMap.put("BIGINT","Long");
        typeMap.put("FLOAT","Double");
        typeMap.put("DOUBLE","Double");
        typeMap.put("DECIMAL","Double");
        typeMap.put("NUMERIC","Double");
        typeMap.put("DATETIME","java.util.Date");
        typeMap.put("TIMESTAMP","java.util.Date");
        typeMap.put("DATE","java.util.Date");
        typeMap.put("TIME","java.util.Date");
        typeMap.put("CHAR","String");
        typeMap.put("VARCHAR","String");
        typeMap.put("TEXT","String");
        typeMap.put("LONGTEXT","String");
        return typeMap;
    }
}
