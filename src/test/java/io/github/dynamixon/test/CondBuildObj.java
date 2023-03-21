package io.github.dynamixon.test;

import io.github.dynamixon.flexorm.annotation.CondOpr;
import lombok.Data;

import java.util.List;

/**
 * @author maojianfeng
 * @date 2020/7/25
 */
@Data
public class CondBuildObj {
    @CondOpr
    private String val1;
    @CondOpr(columnName = "val_2")
    private String val2;
    @CondOpr(value = "like")
    private String val3;
    @CondOpr(value = "in",columnName = "val_4")
    private List<Long> val4;
    @CondOpr(value = "in",columnName = "val_5")
    private List<String> val5;
}
