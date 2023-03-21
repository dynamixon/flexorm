package io.github.dynamixon.dataobject.sqlite;

import io.github.dynamixon.dataobject.DummyTable;
import lombok.Data;
import uk.co.jemos.podam.common.PodamDoubleValue;
import uk.co.jemos.podam.common.PodamExclude;
import uk.co.jemos.podam.common.PodamStringValue;

import java.io.Serializable;

@Data
public class DummyTableSqliteReg extends DummyTable implements Serializable {

  /**  */
  @PodamExclude
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
  private String mismatchedName;

}