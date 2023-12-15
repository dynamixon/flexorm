package io.github.dynamixon.flexorm.misc;

import io.github.dynamixon.flexorm.CoreRunner;
import io.github.dynamixon.flexorm.logic.TableObjectMetaCache;
import io.github.dynamixon.flexorm.pojo.Cond;
import io.github.dynamixon.flexorm.pojo.FieldInfoGetter;
import io.github.dynamixon.flexorm.pojo.InnerCond;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

/**
 * @author Jianfeng.Mao2
 * @date 23-12-15
 */
public class CondUtil {

    public static void resolveColumnNameFromFieldInfoGetter(CoreRunner coreRunner, Cond cond){
        try {
            if(cond==null){
                return;
            }
            InnerCond innerCond = cond.getInnerCond();
            if(innerCond!=null&& CollectionUtils.isNotEmpty(innerCond.getInnerCondList())){
                innerCond.getInnerCondList().forEach(condFromInner -> resolveColumnNameFromFieldInfoGetter(coreRunner,condFromInner));
            }
            FieldInfoGetter<?> fieldInfoGetter = cond.getFieldInfoGetter();
            if(fieldInfoGetter == null){
                return;
            }
            SerializedLambda serializedLambda = getSerializedLambda(fieldInfoGetter);
            String implMethodName = serializedLambda.getImplMethodName();
            String propName = methodToProperty(implMethodName);
            String implClass = serializedLambda.getImplClass();
            Class<?> tableClass = Class.forName(implClass.replace("/","."));
            TableObjectMetaCache.initTableObjectMeta(tableClass,coreRunner);
            MetaHolder metaHolder = TableObjectMetaCache.getMetaMap().get(coreRunner.getDataSource());
            Map<Class<?>, Map<String, String>> fieldToColumnClassMap = metaHolder.getFieldToColumnClassMap();
            if(MapUtils.isEmpty(fieldToColumnClassMap)){
                throw new RuntimeException("No fieldToColumn Info for "+tableClass);
            }
            String colName = fieldToColumnClassMap.get(tableClass).get(propName);
            if(StringUtils.isBlank(colName)){
                throw new RuntimeException("No colName Info for "+propName+", tableClass="+tableClass);
            }
            cond.setColumnName(colName);
        } catch (Exception e) {
            throw new DBException(e);
        }
    }
    public static String methodToProperty(String name) {
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

    public static <T> SerializedLambda getSerializedLambda(FieldInfoGetter<T> getter) {
        try {
            Method method = getter.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            return (SerializedLambda) method.invoke(getter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
