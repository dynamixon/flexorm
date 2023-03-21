package io.github.dynamixon.flexorm.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    /**
     * The table name to which the Class is mapped.
     */
    String value();

    /**
     * Whether the fields of the Class should be mapped to table columns with or without {@link Column} annotation.
     * If a field is annotated with {@link Column}, The attributes from {@link Column} will take precedence.
     */
    boolean autoColumnDetection() default false;
}
