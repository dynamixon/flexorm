package io.github.dynamixon.flexorm.misc;

/**
 * @author maojianfeng
 * @date 22-9-5
 */
public interface SqlExecutionInterceptor {

    /**
     * Denote whether the SqlInterceptor will apply to all SQL executions within a same QueryEntry method, such as [batchInsert, persist ...].
     */
    default boolean spanWithin(){
        return true;
    }

    default void beforeExecution(InterceptorContext interceptorContext){}

    default void afterExecution(InterceptorContext interceptorContext){}
}
