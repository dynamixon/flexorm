package io.github.dynamixon.flexorm.misc;

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
}
