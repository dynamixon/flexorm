package io.github.dynamixon.flexorm.dialect.pagination;

import java.util.List;

public class DefaultPagination implements Pagination {
    @Override
    public String paging(Integer offset, Integer limit, String sql, List<Object> values) {
        return sql;
    }
}
