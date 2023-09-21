package io.github.dynamixon.flexorm.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author maojianfeng
 * @date 23-3-14
 */
public class MetaHolder {
    private static final Logger logger = LoggerFactory.getLogger(MetaHolder.class);
    private final Map<Class<?>, Map<String,String>> fieldToColumnClassMap = initMap("fieldToColumnClassMap");
    private final Map<Class<?>, Map<String,String>> columnToFieldClassMap = initMap("columnToFieldClassMap");
    private final Map<Class<?>, List<String>> primaryFieldsClassMap = initMap("primaryFieldsClassMap");
    private final Map<Class<?>, String> tableNameMap = initMap("tableNameMap");

    public static <K,V> Map<K,V> initMap(String initInfo){
        //Optionally Use WeakHashMap to prevent potential memory leak when the keys of this map should be Garbage Collected if not otherwise referenced.
        String weakAsCache = System.getProperty(DzConst.WEAK_HASH_MAP_AS_CACHE);
        if(Objects.equals(weakAsCache,"true")){
            logger.info(initInfo+" ==> WeakHashMap");
            return new WeakHashMap<>();
        }
        logger.info(initInfo+" ==> ConcurrentHashMap");
        return new ConcurrentHashMap<>();
    }

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

    public <T> void putToMap(Map<Class<?>, T> map, Class<?> classKey, T value){
        if(map instanceof ConcurrentHashMap){
            map.put(classKey,value);
        }else {
            synchronized (MetaHolder.class) {
                map.put(classKey, value);
            }
        }
    }

    public <T> void removeFromMap(Map<Class<?>, T> map, Class<?> classKey){
        if(map instanceof ConcurrentHashMap){
            map.remove(classKey);
        }else {
            synchronized (MetaHolder.class) {
                map.remove(classKey);
            }
        }
    }

    public <T> void removeAll(Map<Class<?>, T> map){
        if(map instanceof ConcurrentHashMap){
            map.clear();
        }else {
            synchronized (MetaHolder.class) {
                map.clear();
            }
        }
    }
}