package io.github.dynamixon.flexorm.misc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GeneralThreadLocal {
    private static final ThreadLocal<Map<String,Object>> COMMON_TL = new ThreadLocal<>();

    public static void set(Map<String,Object> map){
        if(map==null){
            COMMON_TL.remove();
            return;
        }
        if(map instanceof ConcurrentHashMap){
            COMMON_TL.set(map);
        }else {
            Map<String, Object> concurrentHashMap = new ConcurrentHashMap<>();
            map.forEach((k,v) -> {
                if(k!=null&&v!=null){
                    concurrentHashMap.put(k,v);
                }
            });
            COMMON_TL.set(concurrentHashMap);
        }
    }
    public static void set(String key,Object value) {
        if(key==null||value==null){
            return;
        }
        Map<String, Object> map = COMMON_TL.get();
        if(map==null){
            map = new ConcurrentHashMap<>();
            map.put(key,value);
            COMMON_TL.set(map);
        }else {
            get().put(key,value);
        }
    }

    public static void unset() {
        COMMON_TL.remove();
    }

    public static void unset(String key) {
        if(key==null){
            return;
        }
        Map<String, Object> objectMap = get();
        if(objectMap!=null){
            objectMap.remove(key);
        }
    }

    public static Map<String,Object> get(){
        return COMMON_TL.get();
    }

    public static  <T> T get(String key) {
        if(key==null){
            return null;
        }
        Map<String, Object> map = get();
        Object o = map!=null?map.get(key):null;
        return o!=null?(T) o:null;
    }

    public static boolean containsKey(String key){
        if(key==null){
            return false;
        }
        Map<String, Object> map = get();
        return map!=null&&map.containsKey(key);
    }

}
