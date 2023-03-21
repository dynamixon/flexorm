package io.github.dynamixon.flexorm.logic;


import io.github.dynamixon.flexorm.annotation.Table;
import io.github.dynamixon.flexorm.pojo.MetaHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import javax.sql.DataSource;
import java.util.*;

import static org.reflections.scanners.Scanners.TypesAnnotated;

public class TableLoc {

    public static String findTableName(Class<?> type, DataSource dataSource){
        String tableName = findTableNameFromMetaCache(type,dataSource);
        if(StringUtils.isBlank(tableName)){
            tableName = findTableNameByAnnotation(type);
        }
        return tableName;
    }

    public static String findTableNameFromMetaCache(Class<?> type, DataSource dataSource){
        return TableObjectMetaCache.getTableName(type,dataSource);
    }

    public static String findTableNameByAnnotation(Class<?> type){
        String tableName = null;
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            boolean present = c.isAnnotationPresent(Table.class);
            if(present){
                Table table = c.getAnnotation(Table.class);
                tableName = table.value();
                break;
            }
        }
        return tableName;
    }

    public static Set<Class<?>> tableClasses(String packageName, DataSource dataSource){
        Set<Class<?>> classSet = new HashSet<>();
        Map<Class<?>, String> classMapFromCache = TableObjectMetaCache.getTableNameMap(dataSource);
        if(MapUtils.isNotEmpty(classMapFromCache)){
            classMapFromCache.keySet().stream().filter(clazz->{
                String classPkgName = clazz.getPackage().getName();
                return Objects.equals(classPkgName,packageName) || classPkgName.startsWith(packageName+".");
            }).forEach(classSet::add);
        }
        Set<Class<?>> classesFromPackage = new Reflections(packageName, TypesAnnotated).get(TypesAnnotated.with(Table.class).asClass());
        if (CollectionUtils.isNotEmpty(classesFromPackage)) {
            classSet.addAll(classesFromPackage);
        }
        return classSet;
    }

    public static Set<Class<?>> tableClasses(String packageName){
        Set<Class<?>> classSet = new HashSet<>();
        Map<DataSource, MetaHolder> metaMap = TableObjectMetaCache.getMetaMap();
        Collection<MetaHolder> metaHolders = metaMap.values();
        metaHolders.forEach(mh -> {
            Map<Class<?>, String> classMapFromCache = mh.getTableNameMap();
            if(MapUtils.isNotEmpty(classMapFromCache)){
                classMapFromCache.keySet().stream().filter(clazz->{
                    String classPkgName = clazz.getPackage().getName();
                    return Objects.equals(classPkgName,packageName) || classPkgName.startsWith(packageName+".");
                }).forEach(classSet::add);
            }
        });
        Set<Class<?>> classesFromPackage = new Reflections(packageName, TypesAnnotated).get(TypesAnnotated.with(Table.class).asClass());
        if (CollectionUtils.isNotEmpty(classesFromPackage)) {
            classSet.addAll(classesFromPackage);
        }
        return classSet;
    }

    public static Set<Class<?>> findClasses(String table,String packageName){
        return findClasses(table,tableClasses(packageName));
    }

    public static Set<Class<?>> findClasses(String table, Collection<Class<?>> tableClasses){
        Set<Class<?>> clazzList = new HashSet<>();
        Map<DataSource, MetaHolder> metaMap = TableObjectMetaCache.getMetaMap();
        Collection<MetaHolder> values = metaMap.values();
        values.forEach(mh -> {
            Map<Class<?>, String> tableNameMap = mh.getTableNameMap();
            for (Class<?> aClass : tableClasses) {
                String tableName = tableNameMap.get(aClass);
                if(table.equalsIgnoreCase(tableName)){
                    clazzList.add(aClass);
                }
            }
        });
        for (Class<?> aClass : tableClasses) {
            if(clazzList.contains(aClass)){
                continue;
            }
            String tableName = findTableNameByAnnotation(aClass);
            if(table.equalsIgnoreCase(tableName)){
                clazzList.add(aClass);
            }
        }
        return clazzList;
    }
}
