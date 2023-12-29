package io.github.dynamixon.flexorm.misc;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.dynamixon.flexorm.CoreRunner;
import io.github.dynamixon.flexorm.logic.TableObjectMetaCache;
import io.github.dynamixon.flexorm.pojo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Jianfeng.Mao2
 * @date 23-12-15
 */
public class FieldInfoMethodRefUtil {

    private static final LoadingCache<FieldInfoGetter<?>, MethodRef> FIELD_INFO_GETTER_CACHE = CacheBuilder.newBuilder().maximumSize(cacheCount()).build(
        new CacheLoader<FieldInfoGetter<?>, MethodRef>() {
            @Override
            public MethodRef load(@Nonnull FieldInfoGetter<?> getter){
                return getMethodRefFromJava(getter);
            }
        }
    );

    public static long cacheCount(){
        String cacheCountStr = System.getProperty(DzConst.FIELD_INFO_GETTER_CACHE_COUNT);
        if(StringUtils.isBlank(cacheCountStr)){
            return 1024L;
        }
        return Long.parseLong(cacheCountStr);
    }

    public static void resolveColumnNameFromFieldInfoGetter(CoreRunner coreRunner, Cond ... conds){
        resolveColumnNameFromFieldInfoGetter(coreRunner,Arrays.asList(conds));
    }

    public static void resolveColumnNameFromFieldInfoGetter(CoreRunner coreRunner, List<Cond> conds){
        try {
            if(CollectionUtils.isEmpty(conds)){
                return;
            }
            for (Cond cond : conds) {
                if(cond==null){
                    continue;
                }
                if(StringUtils.isNotBlank(cond.getColumnName())){
                    continue;
                }
                InnerCond innerCond = cond.getInnerCond();
                if(innerCond!=null){
                    resolveColumnNameFromFieldInfoGetter(coreRunner,innerCond.getInnerCondList());
                }
                FieldInfoGetter<?> fieldInfoGetter = cond.getFieldInfoGetter();
                if(fieldInfoGetter == null){
                    continue;
                }
                MethodRef methodRef = getMethodRef(fieldInfoGetter);
                String propName = methodToProperty(methodRef.getMethodName());
                Class<?> tableClass = methodRef.getClazz();
                TableObjectMetaCache.initTableObjectMeta(tableClass,coreRunner);
                MetaHolder metaHolder = TableObjectMetaCache.getMetaMap().get(coreRunner.getDataSource());
                Map<Class<?>, Map<String, String>> fieldToColumnClassMap = metaHolder.getFieldToColumnClassMap();
                if(MapUtils.isEmpty(fieldToColumnClassMap)){
                    FIELD_INFO_GETTER_CACHE.invalidate(fieldInfoGetter);
                    throw new RuntimeException("No fieldToColumn Info for "+tableClass);
                }
                Map<String, String> fieldToColumnMap = fieldToColumnClassMap.get(tableClass);
                if(MapUtils.isEmpty(fieldToColumnMap)){
                    FIELD_INFO_GETTER_CACHE.invalidate(fieldInfoGetter);
                    throw new RuntimeException("No column info found for tableClass="+tableClass);
                }
                String colName = fieldToColumnMap.get(propName);
                if(StringUtils.isBlank(colName)){
                    Map<String,String> searchMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    searchMap.putAll(fieldToColumnMap);
                    colName = searchMap.get(propName);
                    if(StringUtils.isBlank(colName)) {
                        FIELD_INFO_GETTER_CACHE.invalidate(fieldInfoGetter);
                        throw new RuntimeException("No colName Info for " + propName + ", tableClass=" + tableClass);
                    }
                }
                cond.setColumnName(colName);
            }
        } catch (Exception e) {
            throw new DBException(e);
        }
    }
    private static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            throw new RuntimeException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
        }

        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }
        return name;
    }

    private static MethodRef getMethodRef(FieldInfoGetter<?> getter){
        try {
            Method[] declaredMethods = getter.getClass().getDeclaredMethods();
            Optional<Method> writeReplaceMethodOp = Arrays.stream(declaredMethods).filter(m -> "writeReplace".equals(m.getName())).findFirst();
            if(writeReplaceMethodOp.isPresent()){
                return FIELD_INFO_GETTER_CACHE.get(getter);
            }else {
                //Groovy uses Closure to support method reference
                return getMethodRefFromGroovyProxy(getter);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed trying construct method reference",e);
        }
    }

    public static MethodRef getMethodRefFromJava(FieldInfoGetter<?> getter) {
        try {
            Method writeReplace = getter.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(getter);
            Class<?> tableClass;
            String implMethodName = serializedLambda.getImplMethodName();
            MethodType methodType = MethodType.fromMethodDescriptorString(
                serializedLambda.getInstantiatedMethodType(), Thread.currentThread().getContextClassLoader());
            int parameterCount = methodType.parameterCount();
            if(parameterCount>0){
                tableClass = methodType.parameterType(0);
            }else {
                String implClass = serializedLambda.getImplClass();
                tableClass = Class.forName(implClass.replace("/","."));
            }
            return new MethodRef(implMethodName,tableClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed trying construct method reference from java",e);
        }
    }

    public static MethodRef getMethodRefFromGroovyProxy(Object proxy) {
        try {
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            Object handler = h.get(proxy);
            Field delegate = handler.getClass().getSuperclass().getDeclaredField("delegate");
            delegate.setAccessible(true);
            Object methodClosure = delegate.get(handler);
            Field methodField = methodClosure.getClass().getDeclaredField("method");
            methodField.setAccessible(true);
            String methodName = (String) methodField.get(methodClosure);
            Field methodClosureDelegate = methodClosure.getClass().getSuperclass().getDeclaredField("delegate");
            methodClosureDelegate.setAccessible(true);
            Class<?> clazz = (Class<?>)methodClosureDelegate.get(methodClosure);
            return new MethodRef(methodName,clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed trying construct method reference from groovy",e);
        }
    }
}
