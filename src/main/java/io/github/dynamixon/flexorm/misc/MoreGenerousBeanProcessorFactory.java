package io.github.dynamixon.flexorm.misc;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MoreGenerousBeanProcessorFactory {

    private static final Map<DataSource,Map<Class<?>,MoreGenerousBeanProcessor>> dsBeanProcessorMap = MetaHolder.initMap("dsBeanProcessorMap");

    public static MoreGenerousBeanProcessor populateBeanProcessor(Class<?> clazz, DataSource dataSource){
        if(dsBeanProcessorMap.containsKey(dataSource)){
            Map<Class<?>,MoreGenerousBeanProcessor> beanProcessorMap = dsBeanProcessorMap.get(dataSource);
            if(beanProcessorMap.containsKey(clazz)){
                return beanProcessorMap.get(clazz);
            }else {
                MoreGenerousBeanProcessor beanProcessor = new MoreGenerousBeanProcessor(clazz,dataSource);
                if(beanProcessorMap instanceof ConcurrentHashMap) {
                    beanProcessorMap.put(clazz, beanProcessor);
                }else {
                    synchronized (MoreGenerousBeanProcessorFactory.class){
                        beanProcessorMap.put(clazz, beanProcessor);
                    }
                }
                return beanProcessor;
            }
        }else {
            Map<Class<?>,MoreGenerousBeanProcessor> beanProcessorMap = MetaHolder.initMap("beanProcessorMap");
            MoreGenerousBeanProcessor beanProcessor = new MoreGenerousBeanProcessor(clazz,dataSource);
            beanProcessorMap.put(clazz,beanProcessor);
            if(dsBeanProcessorMap instanceof ConcurrentHashMap) {
                dsBeanProcessorMap.put(dataSource, beanProcessorMap);
            }else {
                synchronized (MoreGenerousBeanProcessorFactory.class){
                    dsBeanProcessorMap.put(dataSource, beanProcessorMap);
                }
            }
            return beanProcessor;
        }
    }
}