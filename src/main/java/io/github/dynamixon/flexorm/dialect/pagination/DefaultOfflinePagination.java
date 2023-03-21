package io.github.dynamixon.flexorm.dialect.pagination;


import io.github.dynamixon.flexorm.misc.MiscUtil;

import java.util.List;

public class DefaultOfflinePagination implements OfflinePagination {
    @Override
    public <T> List<T> paginate(List<T> collection, Integer offset, Integer limit) {
        return MiscUtil.paginate(collection,offset,limit);
    }
}
