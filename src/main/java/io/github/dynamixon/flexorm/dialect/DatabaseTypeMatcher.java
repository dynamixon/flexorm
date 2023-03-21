package io.github.dynamixon.flexorm.dialect;

public interface DatabaseTypeMatcher {
    default String getDatabaseType(){
        return DialectConst.DEFAULT;
    }
    default boolean match(String databaseType){
        return databaseType.equalsIgnoreCase(getDatabaseType());
    }
}
