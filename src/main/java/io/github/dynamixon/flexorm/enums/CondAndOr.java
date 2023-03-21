package io.github.dynamixon.flexorm.enums;

/**
 * @author maojianfeng
 * @date 22-9-8
 */
public enum CondAndOr {
    AND("and"),
    OR("or"),
    ;

    private final String value;

    public String getValue() {
        return value;
    }

    CondAndOr(String value) {
        this.value = value;
    }
}
