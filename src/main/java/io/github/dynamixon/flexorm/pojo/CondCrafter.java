package io.github.dynamixon.flexorm.pojo;

import java.util.List;

@FunctionalInterface
public interface CondCrafter<E> {
    List<Cond> craft(E e);
}
