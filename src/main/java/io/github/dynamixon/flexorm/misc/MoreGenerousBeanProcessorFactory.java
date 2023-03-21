package io.github.dynamixon.flexorm.misc;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MoreGenerousBeanProcessorFactory {

    private static final Map<DataSource,Map<String,MoreGenerousBeanProcessor>> dsBeanProcessorMap = new ConcurrentHashMap<>();

    public static MoreGenerousBeanProcessor populateBeanProcessor(Class<?> clazz, DataSource dataSource){
        String clazzName = clazz.getName();
        if(dsBeanProcessorMap.containsKey(dataSource)){
            Map<String,MoreGenerousBeanProcessor> beanProcessorMap = dsBeanProcessorMap.get(dataSource);
            if(beanProcessorMap.containsKey(clazzName)){
                return beanProcessorMap.get(clazzName);
            }else {
                MoreGenerousBeanProcessor beanProcessor = new MoreGenerousBeanProcessor(clazz,dataSource);
                beanProcessorMap.put(clazzName,beanProcessor);
                return beanProcessor;
            }
        }else {
            Map<String,MoreGenerousBeanProcessor> beanProcessorMap = new ConcurrentHashMap<>();
            MoreGenerousBeanProcessor beanProcessor = new MoreGenerousBeanProcessor(clazz,dataSource);
            beanProcessorMap.put(clazzName,beanProcessor);
            dsBeanProcessorMap.put(dataSource,beanProcessorMap);
            return beanProcessor;
        }
    }
}
