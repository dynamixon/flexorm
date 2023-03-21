package io.github.dynamixon.dataobject.hsqldb;

import io.github.dynamixon.dataobject.DummyTable;
import lombok.Data;
import uk.co.jemos.podam.common.*;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
public class DummyTableHsqlDbReg extends DummyTable implements Serializable {

  @PodamExclude
  private Integer id;

  @PodamIntValue(maxValue = 5)
  private Integer tinyintF;

  @PodamIntValue(maxValue = 10)
  private Integer smallintF;

  @PodamIntValue(minValue = 1,maxValue = 10000)
  private Integer integerF;

  @PodamLongValue(maxValue = 999999)
  private Long bigintF;

  @PodamDoubleValue(maxValue = 9999.0)
  private Double realF;

  @PodamDoubleValue(maxValue = 9999.0)
  private Double floatF;

  @PodamDoubleValue(maxValue = 99999.0)
  private Double doubleF;

  @PodamDoubleValue(maxValue = 99999.0)
  private Double decimalF;

  @PodamDoubleValue(maxValue = 99999.0)
  private Double numericF;

  @PodamBooleanValue
  private Boolean booleanF;

  private Date datetimeF;

  private Date timestampF;

  private Date dateF;

  private Date timeF;

  @PodamCharValue(maxValue = 'Z')
  private String charF;

  @PodamStringValue(length = 5)
  private String varcharF;

  @PodamStringValue(length = 5)
  private String mismatchedName;

  @PodamStringValue(length = 100)
  private String longvarcharF;

  private UUID uuidF;

}