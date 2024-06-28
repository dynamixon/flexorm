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
    private Throwable exception;
    private volatile Map<String,Object> extraContextInfo;

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

    public void setResultDelegate(boolean resultDelegate) {
        this.resultDelegate = resultDelegate;
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

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Map<String, Object> getExtraContextInfo() {
        return extraContextInfo;
    }

    public void setExtraContextInfo(Map<String, Object> extraContextInfo) {
        this.extraContextInfo = extraContextInfo;
    }

    public boolean hasException(){
        return exception != null;
    }

    public <T> T getGenericDelegateResult(){
        if(!resultDelegate){
            return null;
        }
        if(delegatedResult == null){
            return null;
        }
        if(delegatedResult instanceof DelegatedResultGenerator){
            return ((DelegatedResultGenerator<T>) delegatedResult).generate(this);
        }
        return (T) delegatedResult;
    }

    public void putToExtraContextInfo(String key, Object value){
            if(extraContextInfo == null){
                synchronized (this){
                    if(extraContextInfo == null) {
                        extraContextInfo = new ConcurrentHashMap<>();
                    }
                }
            }
            extraContextInfo.put(key, value);
    }

    public void putAllToExtraContextInfo(Map<String, Object> map){
        if(map == null){
            return;
        }
        if(extraContextInfo == null){
            synchronized (this){
                if(extraContextInfo == null) {
                    extraContextInfo = new ConcurrentHashMap<>();
                }
            }
        }
        extraContextInfo.putAll(map);
    }

    public <T> T removeFromExtraContextInfo(String key){
        if(extraContextInfo != null){
            return (T) extraContextInfo.remove(key);
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
