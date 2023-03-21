package io.github.dynamixon.flexorm.pojo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author maojianfeng
 * @date 23-3-14
 */
public class MetaHolder {
    private final Map<Class<?>, Map<String,String>> fieldToColumnClassMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, Map<String,String>> columnToFieldClassMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<String>> primaryFieldsClassMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, String> tableNameMap = new ConcurrentHashMap<>();

    public Map<Class<?>, Map<String, String>> getFieldToColumnClassMap() {
        return fieldToColumnClassMap;
    }

    public Map<Class<?>, Map<String, String>> getColumnToFieldClassMap() {
        return columnToFieldClassMap;
    }

    public Map<Class<?>, List<String>> getPrimaryFieldsClassMap() {
        return primaryFieldsClassMap;
    }

    public Map<Class<?>, String> getTableNameMap() {
        return tableNameMap;
    }
}
