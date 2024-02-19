package io.github.dynamixon.flexorm.misc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author maojianfeng
 * @date 22-9-5
 */
public class InterceptorContext {
    private String sql;
    private Object[] values;
    private boolean resultDelegate;
    private Object delegatedResult;
    private Object realResult;
    private Long timeCost;
    private Map<String,Object> extraContextInfo;

    public InterceptorContext() {
    }

    public InterceptorContext(String sql, Object[] values) {
        this.sql = sql;
        this.values = values;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public boolean isResultDelegate() {
        return resultDelegate;
    }

    public Object getDelegatedResult() {
        return delegatedResult;
    }

    public void setDelegatedResult(Object delegatedResult) {
        this.delegatedResult = delegatedResult;
        this.resultDelegate = true;
    }

    public Object getRealResult() {
        return realResult;
    }

    public void setRealResult(Object realResult) {
        this.realResult = realResult;
    }

    public Long getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(Long timeCost) {
        this.timeCost = timeCost;
    }

    public <T> T getGenericDelegateResult(){
        if(resultDelegate){
            return delegatedResult ==null?null:(T) delegatedResult;
        }
        return null;
    }

    public Map<String, Object> getExtraContextInfo() {
        return extraContextInfo;
    }

    public void setExtraContextInfo(Map<String, Object> extraContextInfo) {
        this.extraContextInfo = extraContextInfo;
    }

    public void putToExtraContextInfo(String key, Object value){
        synchronized (InterceptorContext.class){
            if(extraContextInfo == null){
                extraContextInfo = new ConcurrentHashMap<>();
            }
            extraContextInfo.put(key, value);
        }
    }

    public void putAllToExtraContextInfo(Map<String, Object> map){
        if(map == null){
            return;
        }
        synchronized (InterceptorContext.class){
            if(extraContextInfo == null){
                extraContextInfo = new ConcurrentHashMap<>();
            }
            extraContextInfo.putAll(map);
        }
    }

    public <T> T removeFromExtraContextInfo(String key){
        synchronized (InterceptorContext.class){
            if(extraContextInfo != null){
                return (T) extraContextInfo.remove(key);
            }
        }
        return null;
    }

    public <T> T getFromExtraContextInfo(String key){
        if(extraContextInfo != null){
            return (T) extraContextInfo.get(key);
        }
        return null;
    }
}
