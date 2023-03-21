package io.github.dynamixon.flexorm.dialect.regulate;

import io.github.dynamixon.flexorm.dialect.DialectConst;

public class H2DbEntityRegulator implements EntityRegulator{
    @Override
    public String getDatabaseType() {
        return DialectConst.H2;
    }

    @Override
    public String simpleTable(String table){
        return table.toUpperCase();
    }
}
