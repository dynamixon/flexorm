package io.github.dynamixon.flexorm.logic;

import io.github.dynamixon.flexorm.CoreRunner;
import io.github.dynamixon.flexorm.QueryEntry;
import io.github.dynamixon.flexorm.annotation.Column;
import io.github.dynamixon.flexorm.annotation.Primary;
import io.github.dynamixon.flexorm.annotation.Table;
import io.github.dynamixon.flexorm.misc.DBException;
import io.github.dynamixon.flexorm.misc.MiscUtil;
import io.github.dynamixon.flexorm.misc.MetaHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TableObjectMetaCache {
    private static final Logger logger = LoggerFactory.getLogger(TableObjectMetaCache.class);

    private final static Map<DataSource, MetaHolder> metaMap = MetaHolder.initMap("metaMap");

    private static MetaHolder getOrInitMetaHolder(DataSource dataSource){
        if(metaMap.containsKey(dataSource)){
            return metaMap.get(dataSource);
        }else {
            if(metaMap instanceof ConcurrentHashMap){
                MetaHolder metaHolder = new MetaHolder();
                metaMap.put(dataSource, metaHolder);
                return metaHolder;
            }else {
                synchronized (TableObjectMetaCache.class) {
                    if (metaMap.containsKey(dataSource)) {
                        return metaMap.get(dataSource);
                    } else {
                        MetaHolder metaHolder = new MetaHolder();
                        metaMap.put(dataSource, metaHolder);
                        return metaHolder;
                    }
                }
            }
        }
    }

    public static Map<DataSource, MetaHolder> getMetaMap(){
        return metaMap;
    }

    public static void initTableObjectMeta(Class<?> tableClass, QueryEntry queryEntry){
        initTableObjectMeta(tableClass,queryEntry.getCoreRunner());
    }
    public static void initTableObjectMeta(Class<?> tableClass, CoreRunner coreRunner){
        if(metaInitComplete(tableClass,coreRunner)){
            return;
        }
        String className = tableClass.getName();
        Table table = tableClass.getAnnotation(Table.class);
        if(table==null){
            throw new DBException("Init Table Meta failed: tableClass:"+ className +" has no @Table annotation!");
        }
        String tableName = TableLoc.fromTableAnnotation(tableClass);
        Map<String,String> fieldToColumnMap = new HashMap<>();
        Map<String,String> columnToFieldMap = new HashMap<>();
        List<String> primaryFields = new ArrayList<>();
        List<String> colNames = null;
        if(table.autoColumnDetection()){
            colNames = coreRunner.getColNames(tableName);
        }
        List<Field> fields = MiscUtil.getAllFields(tableClass);
        for (Field field : fields) {
            // parsing Column info
            Column column = field.getAnnotation(Column.class);
            String fieldName = field.getName();
            String regulatedFieldName = fieldName.replace("_", "").toLowerCase();
            if(column!=null){
                String colName4SqlCompose = StringUtils.isNotBlank(column.customValue())?column.customValue():
                    (StringUtils.isNotBlank(column.value())?column.value():fieldName);
                String colName4FieldMapping = StringUtils.isNotBlank(column.value())?column.value():fieldName;
                fieldToColumnMap.put(fieldName,colName4SqlCompose);
                columnToFieldMap.put(colName4FieldMapping.toLowerCase(),fieldName);
            }else {
                if(CollectionUtils.isNotEmpty(colNames)){
                    colNames.forEach(colName -> {
                        String regulatedColName = colName.replace("_","").toLowerCase();
                        if(regulatedFieldName.equals(regulatedColName)){
                            fieldToColumnMap.put(fieldName,colName);
                            columnToFieldMap.put(colName.toLowerCase(),fieldName);
                        }
                    });
                }
            }
            // parsing Primary info
            Primary primary = field.getAnnotation(Primary.class);
            if(primary!=null){
                primaryFields.add(fieldName);
            }

        }
        MetaHolder metaHolder = getOrInitMetaHolder(coreRunner.getDataSource());
        metaHolder.putToMap(metaHolder.getTableNameMap(),tableClass,table.tableNameDynamic()?TableLoc.dynamicTablePlaceholder:tableName);
        metaHolder.putToMap(metaHolder.getPrimaryFieldsClassMap(),tableClass,primaryFields);
        metaHolder.putToMap(metaHolder.getFieldToColumnClassMap(),tableClass,fieldToColumnMap);
        metaHolder.putToMap(metaHolder.getColumnToFieldClassMap(),tableClass,columnToFieldMap);
        logger.info("tableClass:{} meta inited.",tableClass);
    }

    public static void registerTableObjectMeta(boolean overwrite,Class<?> tableClass, CoreRunner coreRunner,String tableName){
        registerTableObjectMeta(overwrite,tableClass,coreRunner,tableName,null,null,null);
    }

    public static void registerTableObjectMeta(boolean overwrite, Class<?> tableClass, CoreRunner coreRunner,String tableName,Map<String,String> fieldToColumnCustomMap,Map<String,String> columnToFieldCustomMap,List<String> primaryFields){
        registerTableObjectMeta(overwrite,true,tableClass,coreRunner,tableName,fieldToColumnCustomMap,columnToFieldCustomMap,primaryFields);
    }

    public static void registerTableObjectMeta(boolean overwrite,boolean mergeWithRealTable, Class<?> tableClass, CoreRunner coreRunner,String tableName,Map<String,String> fieldToColumnCustomMap,Map<String,String> columnToFieldCustomMap,List<String> primaryFields){
        if(!overwrite&&metaInitComplete(tableClass,coreRunner)){
            return;
        }
        Map<String,String> fieldToColumnMap = new HashMap<>();
        Map<String,String> columnToFieldMap = new HashMap<>();
        if(mergeWithRealTable) {
            List<String> colNames = coreRunner.getColNames(tableName);
            List<Field> fields = MiscUtil.getAllFields(tableClass);
            for (Field field : fields) {
                String fieldName = field.getName();
                String regulatedFieldName = fieldName.replace("_", "").toLowerCase();
                colNames.forEach(colName -> {
                    String regulatedColName = colName.replace("_", "").toLowerCase();
                    if (regulatedFieldName.equals(regulatedColName)) {
                        fieldToColumnMap.put(fieldName, colName);
                        columnToFieldMap.put(colName.toLowerCase(), fieldName);
                    }
                });
            }
        }
        if(MapUtils.isNotEmpty(fieldToColumnCustomMap)){
            fieldToColumnMap.putAll(fieldToColumnCustomMap);
        }
        if(MapUtils.isNotEmpty(columnToFieldCustomMap)){
            columnToFieldMap.putAll(columnToFieldCustomMap);
        }
        MetaHolder metaHolder = getOrInitMetaHolder(coreRunner.getDataSource());

        boolean tableNameDynamic = false;
        Table table = tableClass.getAnnotation(Table.class);
        if(table!=null){
            tableNameDynamic = table.tableNameDynamic();
        }
        metaHolder.putToMap(metaHolder.getTableNameMap(),tableClass,tableNameDynamic?TableLoc.dynamicTablePlaceholder:tableName);
        metaHolder.putToMap(metaHolder.getPrimaryFieldsClassMap(),tableClass,primaryFields==null?Collections.emptyList():primaryFields);
        metaHolder.putToMap(metaHolder.getFieldToColumnClassMap(),tableClass,fieldToColumnMap);
        metaHolder.putToMap(metaHolder.getColumnToFieldClassMap(),tableClass,columnToFieldMap);
        logger.info("tableClass:{} meta registered. datasource:{}",tableClass,coreRunner.getDataSource());
    }

    public static boolean metaInitComplete(Class<?> tableClass, CoreRunner coreRunner){
        MetaHolder metaHolder = getOrInitMetaHolder(coreRunner.getDataSource());
        return metaHolder.getTableNameMap().containsKey(tableClass) && metaHolder.getFieldToColumnClassMap().containsKey(tableClass) && metaHolder.getColumnToFieldClassMap().containsKey(tableClass);
    }

    public static Map<String,String> getFieldToColumnMap(Class<?> tableClass,DataSource dataSource){
        MetaHolder metaHolder = getOrInitMetaHolder(dataSource);
        return metaHolder.getFieldToColumnClassMap().get(tableClass);
    }

    public static Map<String,String> getColumnToFieldMap(Class<?> tableClass,DataSource dataSource){
        MetaHolder metaHolder = getOrInitMetaHolder(dataSource);
        return metaHolder.getColumnToFieldClassMap().get(tableClass);
    }

    public static List<String> getPrimaryFields(Class<?> tableClass,DataSource dataSource) {
        MetaHolder metaHolder = getOrInitMetaHolder(dataSource);
        return metaHolder.getPrimaryFieldsClassMap().get(tableClass);
    }

    public static Map<Class<?>,String> getTableNameMap(DataSource dataSource) {
        MetaHolder metaHolder = getOrInitMetaHolder(dataSource);
        return metaHolder.getTableNameMap();
    }

    public static String getTableName(Class<?> tableClass,DataSource dataSource) {
        MetaHolder metaHolder = getOrInitMetaHolder(dataSource);
        return metaHolder.getTableNameMap().get(tableClass);
    }

    public static void removeCacheAll(DataSource dataSource){
        MetaHolder metaHolder = getOrInitMetaHolder(dataSource);
        metaHolder.removeAll(metaHolder.getFieldToColumnClassMap());
        metaHolder.removeAll(metaHolder.getColumnToFieldClassMap());
        metaHolder.removeAll(metaHolder.getPrimaryFieldsClassMap());
        metaHolder.removeAll(metaHolder.getTableNameMap());
    }

    public static void removeCache(Class<?> tableClass,DataSource dataSource){
        MetaHolder metaHolder = getOrInitMetaHolder(dataSource);
        metaHolder.removeFromMap(metaHolder.getFieldToColumnClassMap(),tableClass);
        metaHolder.removeFromMap(metaHolder.getColumnToFieldClassMap(),tableClass);
        metaHolder.removeFromMap(metaHolder.getPrimaryFieldsClassMap(),tableClass);
        metaHolder.removeFromMap(metaHolder.getTableNameMap(),tableClass);
    }

    public static void refreshCache(Class<?> tableClass, QueryEntry queryEntry){
        if(tableClass.isAnnotationPresent(Table.class)){
            removeCache(tableClass,queryEntry.getDataSource());
            initTableObjectMeta(tableClass,queryEntry);
        }else {
            throw new DBException("refreshCache failed: tableClass:"+ tableClass.getName() +" has no @Table annotation!");
        }
    }

    public static boolean isPrimaryField(Class<?> tableClass,String fieldName, QueryEntry queryEntry){
        MetaHolder metaHolder = getOrInitMetaHolder(queryEntry.getDataSource());
        List<String> primaryFields = metaHolder.getPrimaryFieldsClassMap().get(tableClass);
        if(CollectionUtils.isEmpty(primaryFields)){
            return false;
        }
        return primaryFields.contains(fieldName);
    }
}
