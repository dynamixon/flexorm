package io.github.dynamixon.flexorm.annotation;

import java.lang.annotation.*;

/**
 * @author halflife3
 * @date 2019/6/19
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    /**
     * As the canonical table column name, the bean field name will apply should this value be empty.
     */
    String value() default "";
    /**
     * Used to compose sql, the above value(or bean field name if value itself is empty) will be used if empty.
     */
    String customValue() default "";
}
