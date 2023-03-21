package io.github.dynamixon.flexorm.dialect.pagination;

import io.github.dynamixon.flexorm.dialect.DialectConst;

import java.util.List;

public class MsSqlPagination implements Pagination{
    @Override
    public String getDatabaseType() {
        return DialectConst.MSSQL;
    }
    @Override
    public String paging(Integer offset, Integer limit, String sql, List<Object> values) {
        if(offset!=null&&limit!=null) {
            if(!sql.toLowerCase().contains("order by")){
                sql+=" order by (select null) offset ? rows fetch next ? rows only";
            }else {
                sql+=" offset ? rows fetch next ? rows only";
            }
            values.add(offset);
            values.add(limit);
        }
        return sql;
    }
}
