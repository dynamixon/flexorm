package io.github.dynamixon.flexorm.misc;

@FunctionalInterface
public interface DelegatedResultGenerator<T> {

    T generate(InterceptorContext context);
}
