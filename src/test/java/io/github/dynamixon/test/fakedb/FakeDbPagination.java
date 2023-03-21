package io.github.dynamixon.test.fakedb;

import io.github.dynamixon.flexorm.dialect.pagination.HsqlDbPagination;

public class FakeDbPagination extends HsqlDbPagination {
    @Override
    public String getDatabaseType() {
        return FakeDBConst.DB_TYPE;
    }
}
