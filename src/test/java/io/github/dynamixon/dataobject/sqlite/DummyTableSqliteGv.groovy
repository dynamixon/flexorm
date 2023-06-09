package io.github.dynamixon.dataobject.sqlite


import io.github.dynamixon.dataobject.DummyTable
import io.github.dynamixon.flexorm.annotation.Column
import io.github.dynamixon.flexorm.annotation.Primary
import io.github.dynamixon.flexorm.annotation.Table
import uk.co.jemos.podam.common.PodamDoubleValue
import uk.co.jemos.podam.common.PodamExclude
import uk.co.jemos.podam.common.PodamStringValue

@Table("dummy_table")
class DummyTableSqliteGv extends DummyTable implements Serializable{

    /**  */
    @PodamExclude
    @Primary
    @Column("ID")
    private Long id

    /**  */
    @PodamDoubleValue(maxValue = 999.0D)
    @Column("real_f")
    private Double realF

    /**  */
    @PodamDoubleValue(maxValue = 999.0D)
    @Column("numeric_f")
    private Double numericF

    /**  */
    @PodamStringValue(length = 5)
    @Column("text_f")
    private String textF

    @PodamStringValue(length = 5)
    @Column("name_mismatch_f")
    private String mismatchedName

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    Double getRealF() {
        return realF
    }

    void setRealF(Double realF) {
        this.realF = realF
    }

    Double getNumericF() {
        return numericF
    }

    void setNumericF(Double numericF) {
        this.numericF = numericF
    }

    String getTextF() {
        return textF
    }

    void setTextF(String textF) {
        this.textF = textF
    }

    String getMismatchedName() {
        return mismatchedName
    }

    void setMismatchedName(String mismatchedName) {
        this.mismatchedName = mismatchedName
    }
}
