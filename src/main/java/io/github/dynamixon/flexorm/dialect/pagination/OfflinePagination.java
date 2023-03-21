package io.github.dynamixon.flexorm.dialect.pagination;

import io.github.dynamixon.flexorm.dialect.DatabaseTypeMatcher;

import java.util.List;

public interface OfflinePagination extends DatabaseTypeMatcher {
    <T> List<T> paginate(List<T> collection, Integer offset, Integer limit);
}
