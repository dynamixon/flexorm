package io.github.dynamixon.flexorm.dialect.pagination;

import io.github.dynamixon.flexorm.dialect.DatabaseTypeMatcher;

import java.util.List;

public interface Pagination extends DatabaseTypeMatcher {
    String paging(Integer offset, Integer limit, String sql, List<Object> values);
}
