package io.github.dynamixon.dataobject.sqlite;

import io.github.dynamixon.dataobject.DummyTable;
import io.github.dynamixon.flexorm.annotation.Primary;
import io.github.dynamixon.flexorm.annotation.Table;
import io.github.dynamixon.flexorm.annotation.Column;
import lombok.Data;
import uk.co.jemos.podam.common.PodamDoubleValue;
import uk.co.jemos.podam.common.PodamExclude;
import uk.co.jemos.podam.common.PodamStringValue;

import java.io.Serializable;

/**  */
@Table(value = "dummy_table",autoColumnDetection = true)
@Data
public class DummyTableSqliteAlt extends DummyTable implements Serializable {

  /**  */
  @PodamExclude
  @Primary
  //@Column("ID")
  private Long id;

  /**  */
  @PodamDoubleValue(maxValue = 999.0)
  //@Column("real_f")
  private Double realF;

  /**  */
  @PodamDoubleValue(maxValue = 999.0)
  //@Column("numeric_f")
  private Double numericF;

  /**  */
  @PodamStringValue(length = 5)
  //@Column("text_f")
  private String textF;

  @PodamStringValue(length = 5)
  @Column("name_mismatch_f")
  private String mismatchedName;

}