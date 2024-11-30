package io.github.dynamixon.flexorm.misc;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by maojianfeng on 9/12/16.
 */
public class MiscUtil {

    public static <T> T getFirst(Collection<? extends T> coll){
        T t = null;
        if(CollectionUtils.isNotEmpty(coll)){
            Iterator<? extends T> iterator = coll.iterator();
            t = iterator.hasNext() ? iterator.next() : null;
        }
        return t;
    }

    public static Map<String,Object> mapObject(Object o) {
        Map<String,Object> map = new HashMap<>();
        if(o!=null){
            List<Field> allFields = getAllFields(o.getClass());
            for(Field field:allFields){
                map.put(field.getName(), getValueSafe(o,field));
            }
        }
        return map;
    }

    public static Map<String,Field> mapFieldFromObj(Object o){
        return mapFieldFromObj(o,true);
    }
    public static Map<String,Field> mapFieldFromObj(Object o,boolean caseSensitive){
        Class<?> type = o.getClass();
        return mapFieldFromClass(type,caseSensitive);
    }

    public static Map<String,Field> mapFieldFromClass(Class<?> type){
        return mapFieldFromClass(type,true);
    }
    public static Map<String,Field> mapFieldFromClass(Class<?> type,boolean caseSensitive){
        Map<String,Field> map = null;
        if(caseSensitive){
            map = new HashMap<>();
        }else {
            map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        }
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            Field[] declaredFields = c.getDeclaredFields();
            for (Field field : declaredFields) {
                if(field.isSynthetic()) {
                    continue;
                }
                String name = field.getName();
                if(!map.containsKey(name)) {
                    map.put(name, field);
                }
            }
        }
        return map;
    }

    public static void setValue(Object target,String fieldName,Object value) {
        setValue(target,getField(target.getClass(),fieldName),value);
    }

    public static void setValueSafe(Object target,String fieldName,Object value) {
        setValueSafe(target,getField(target.getClass(),fieldName),value);
    }

    public static void setValue(Object target,Field field,Object value) {
        if(target==null||field==null){
            return;
        }
        try {
            if(!field.isAccessible()){
                field.setAccessible(true);
            }
            field.set(target,value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setValueSafe(Object target,Field field,Object value) {
        try {
            setValue(target,field,value);
        } catch (SecurityException e) {
            try {
                PropertyUtils.setProperty(target,field.getName(),value);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        } catch (Throwable e){
            throw new RuntimeException(e);
        }
    }

    public static <T> T extractFieldValueFromObj(Object o,String fieldName) {
        if(o==null || fieldName==null || fieldName.isEmpty()){
            return null;
        }
        Field field = getField(o.getClass(),fieldName);
        return getValue(o,field);
    }

    public static <T> T getValue(Object o,Field field) {
        try {
            if(o==null||field==null){
                return null;
            }
            if(!field.isAccessible()){
                field.setAccessible(true);
            }
            Object value = field.get(o);
            return value==null?null:(T)value;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getValueSafe(Object o,Field field) {
        try {
            return getValue(o,field);
        } catch (SecurityException e) {
            try {
                Object val = PropertyUtils.getProperty(o, field.getName());
                return val==null?null:(T)val;
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        } catch (Throwable e){
            throw new RuntimeException(e);
        }

    }

    public static <T> T getValue(Object o,String fieldName) {
        return extractFieldValueFromObj(o,fieldName);
    }

    public static <T> T getValueSafe(Object o,String fieldName) {
        try {
            return getValue(o,fieldName);
        } catch (SecurityException e) {
            try {
                Object val = PropertyUtils.getProperty(o, fieldName);
                return val==null?null:(T)val;
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        } catch (Throwable e){
            throw new RuntimeException(e);
        }
    }

    public static List<Field> getAllFields(Class<?> clazz){
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            Field[] declaredFields = c.getDeclaredFields();
            fields.addAll(Arrays.asList(declaredFields));
            fields.removeIf(Field::isSynthetic);
        }
        return fields;
    }

    public static Field getField(Class<?> clazz,String fieldName){
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            Field[] declaredFields = c.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if(!declaredField.isSynthetic()&&fieldName.equals(declaredField.getName())){
                    return declaredField;
                }
            }
        }
        return null;
    }

    public static List<Object> fromArray(Object array){
        List<Object> list = new ArrayList<>();
        if(array!=null){
            int length = Array.getLength(array);
            for (int i = 0; i < length; i ++) {
                Object arrayElement = Array.get(array, i);
                list.add(arrayElement);
            }
        }
        return list;
    }
    public static <T> List<T> paginate(List<T> collection, Integer offset, Integer limit){
        if(CollectionUtils.isNotEmpty(collection)&&offset!=null&&limit!=null) {
            return collection.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
        }else {
            return collection;
        }
    }

    @SafeVarargs
    public static <T> List<T> combineList(List<T> ... values) {
        return Stream.of(values)
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
