package io.github.dynamixon.dataobject.mysql


import io.github.dynamixon.dataobject.DummyTable
import io.github.dynamixon.flexorm.annotation.Primary
import io.github.dynamixon.flexorm.annotation.Table
import io.github.dynamixon.flexorm.annotation.Column
import uk.co.jemos.podam.common.PodamCharValue
import uk.co.jemos.podam.common.PodamDoubleValue
import uk.co.jemos.podam.common.PodamExclude
import uk.co.jemos.podam.common.PodamIntValue
import uk.co.jemos.podam.common.PodamLongValue
import uk.co.jemos.podam.common.PodamStringValue

@Table("dummy_table")
class DummyTableMysqlGv  extends DummyTable implements Serializable{

    /**  */
    @PodamExclude
    @Primary
    @Column("id")
    private Long id

    /**  */
    @PodamIntValue(maxValue = 5)
    @Column("tinyint_f")
    private Integer tinyintF

    /**  */
    @PodamIntValue(maxValue = 10)
    @Column("smallint_f")
    private Integer smallintF

    /**  */
    @PodamIntValue(minValue = 2000,maxValue = 2090)
    @Column("year_f")
    private Integer yearF

    /**  */
    @PodamIntValue(minValue = 1,maxValue = 10000)
    @Column("int_f")
    private Integer intF

    /**  */
    @Column("bit_f")
    private Boolean bitF

    /**  */
    @PodamLongValue(maxValue = 999999L)
    @Column("bigint_f")
    private Long bigintF

    /**  */
    @PodamDoubleValue(maxValue = 999.0D)
    @Column("float_f")
    private Double floatF

    /**  */
    @PodamDoubleValue(maxValue = 9999.0D)
    @Column("double_f")
    private Double doubleF

    /**  */
    @PodamDoubleValue(maxValue = 9999.0D)
    @Column("decimal_f")
    private Double decimalF

    /**  */
    @PodamDoubleValue(maxValue = 9999.0D)
    @Column("numeric_f")
    private Double numericF

    /**  */
    @Column("dateTime_f")
    private Date datetimeF

    /**  */
    @Column("timestamp_f")
    private Date timestampF

    /**  */
    @Column("date_f")
    private Date dateF

    /**  */
    @Column("time_f")
    private Date timeF

    /**  */
    @PodamCharValue
    @Column("char_f")
    private String charF

    /**  */
    @PodamStringValue(length = 5)
    @Column("varchar_f")
    private String varcharF

    @PodamStringValue(length = 5)
    @Column("name_mismatch_f")
    private String mismatchedName

    /**  */
    @PodamStringValue(length = 20)
    @Column("text_f")
    private String textF

    /**  */
    @PodamStringValue(length = 100)
    @Column("longtext_f")
    private String longtextF

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    Integer getTinyintF() {
        return tinyintF
    }

    void setTinyintF(Integer tinyintF) {
        this.tinyintF = tinyintF
    }

    Integer getSmallintF() {
        return smallintF
    }

    void setSmallintF(Integer smallintF) {
        this.smallintF = smallintF
    }

    Integer getYearF() {
        return yearF
    }

    void setYearF(Integer yearF) {
        this.yearF = yearF
    }

    Integer getIntF() {
        return intF
    }

    void setIntF(Integer intF) {
        this.intF = intF
    }

    Boolean getBitF() {
        return bitF
    }

    void setBitF(Boolean bitF) {
        this.bitF = bitF
    }

    Long getBigintF() {
        return bigintF
    }

    void setBigintF(Long bigintF) {
        this.bigintF = bigintF
    }

    Double getFloatF() {
        return floatF
    }

    void setFloatF(Double floatF) {
        this.floatF = floatF
    }

    Double getDoubleF() {
        return doubleF
    }

    void setDoubleF(Double doubleF) {
        this.doubleF = doubleF
    }

    Double getDecimalF() {
        return decimalF
    }

    void setDecimalF(Double decimalF) {
        this.decimalF = decimalF
    }

    Double getNumericF() {
        return numericF
    }

    void setNumericF(Double numericF) {
        this.numericF = numericF
    }

    Date getDatetimeF() {
        return datetimeF
    }

    void setDatetimeF(Date datetimeF) {
        this.datetimeF = datetimeF
    }

    Date getTimestampF() {
        return timestampF
    }

    void setTimestampF(Date timestampF) {
        this.timestampF = timestampF
    }

    Date getDateF() {
        return dateF
    }

    void setDateF(Date dateF) {
        this.dateF = dateF
    }

    Date getTimeF() {
        return timeF
    }

    void setTimeF(Date timeF) {
        this.timeF = timeF
    }

    String getCharF() {
        return charF
    }

    void setCharF(String charF) {
        this.charF = charF
    }

    String getVarcharF() {
        return varcharF
    }

    void setVarcharF(String varcharF) {
        this.varcharF = varcharF
    }

    String getTextF() {
        return textF
    }

    void setTextF(String textF) {
        this.textF = textF
    }

    String getLongtextF() {
        return longtextF
    }

    void setLongtextF(String longtextF) {
        this.longtextF = longtextF
    }

    String getMismatchedName() {
        return mismatchedName
    }

    void setMismatchedName(String mismatchedName) {
        this.mismatchedName = mismatchedName
    }
}
