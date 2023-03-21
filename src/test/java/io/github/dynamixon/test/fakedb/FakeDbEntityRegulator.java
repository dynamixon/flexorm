package io.github.dynamixon.test.fakedb;

import io.github.dynamixon.flexorm.dialect.regulate.HsqlDbEntityRegulator;

public class FakeDbEntityRegulator extends HsqlDbEntityRegulator {
    @Override
    public String getDatabaseType() {
        return FakeDBConst.DB_TYPE;
    }
}
