package io.github.dynamixon.flexorm.dialect.pagination;

import io.github.dynamixon.flexorm.dialect.DialectConst;

import java.util.List;

public class H2Pagination implements Pagination{
    @Override
    public String getDatabaseType() {
        return DialectConst.H2;
    }
    @Override
    public String paging(Integer offset, Integer limit, String sql, List<Object> values) {
        if(offset!=null&&limit!=null) {
            sql+=" limit ?,? ";
            values.add(offset);
            values.add(limit);
        }
        return sql;
    }
}
