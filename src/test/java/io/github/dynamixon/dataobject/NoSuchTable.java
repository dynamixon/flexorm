package io.github.dynamixon.dataobject;

import io.github.dynamixon.flexorm.annotation.Table;
import lombok.Data;

@Table("no_such_tbl")
@Data
public class NoSuchTable {
    private String dummyField;
}
