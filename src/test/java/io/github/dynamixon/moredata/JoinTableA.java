package io.github.dynamixon.moredata;

import io.github.dynamixon.flexorm.annotation.Column;
import io.github.dynamixon.flexorm.annotation.Primary;
import io.github.dynamixon.flexorm.annotation.Table;
import lombok.Data;
import uk.co.jemos.podam.common.*;

import java.util.Date;

/**
 * @author Jianfeng.Mao2
 * @date 23-12-29
 */
@Table(value = "join_table_A",autoColumnDetection = false)
@Data
public class JoinTableA {
    @PodamExclude
    @Primary
    @Column("id")
    private Long id;

    @PodamIntValue(maxValue = 1000)
    @Column("int_f")
    private Integer intF;

    @Column("boolean_f")
    private Boolean booleanF;

    @PodamLongValue(maxValue = 999999)
    @Column("bigint_f")
    private Long bigintF;

    @PodamDoubleValue(maxValue = 9999.0)
    @Column("double_f")
    private Double doubleF;

    @Column("timestamp_f")
    private Date timestampF;

    @PodamStringValue(length = 10)
    @Column("varchar_f")
    private String varcharF;

    @PodamStringValue(length = 50)
    @Column("name_mismatch_f")
    private String mismatchedName;

    @PodamStringValue(length = 50)
    @Column(value = "desc", customValue = "`desc`")
    private String desc;
}
