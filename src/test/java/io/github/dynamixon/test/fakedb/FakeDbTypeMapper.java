package io.github.dynamixon.test.fakedb;

import io.github.dynamixon.flexorm.dialect.typemapping.HsqlDbTypeMapper;

public class FakeDbTypeMapper extends HsqlDbTypeMapper {
    @Override
    public String getDatabaseType() {
        return FakeDBConst.DB_TYPE;
    }
}
