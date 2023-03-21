package io.github.dynamixon.flexorm.dialect.pagination;


import java.util.List;

public class DummyOfflinePagination implements OfflinePagination {
    @Override
    public String getDatabaseType() {
        return "$DUMMY";
    }
    @Override
    public <T> List<T> paginate(List<T> collection, Integer offset, Integer limit) {
        return collection;
    }
}
