package io.github.dynamixon.flexorm.pojo;

/**
 * null value representation for Cond value
 * @author mjf
 * @date 2020/1/7
 */
public class Null {

    private static final Null INSTANCE = new Null();

    public static Null instance(){
        return INSTANCE;
    }
}
