package io.github.dynamixon.flexorm.annotation;

import io.github.dynamixon.flexorm.columnhandler.ResultCastHandler;

import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResultCast {
    Class<? extends ResultCastHandler<?>> value();
}
