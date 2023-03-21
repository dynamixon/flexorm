package io.github.dynamixon.dataobject.sub;

import io.github.dynamixon.flexorm.annotation.Table;
import lombok.Data;

@Table("sub_no_such_tbl")
@Data
public class SubNoSuchTable {
    private String dummyField;
}
