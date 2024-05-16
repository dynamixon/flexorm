package io.github.dynamixon.flexorm.misc;

@FunctionalInterface
public interface DelegatedResultGenerator {

    Object generate(InterceptorContext context);
}
