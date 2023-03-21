package io.github.dynamixon.dataobject

import io.github.dynamixon.flexorm.annotation.Table

@Table("no_such_tbl")
class NoSuchTableGv {
    private String dummyField

    String getDummyField() {
        return dummyField
    }

    void setDummyField(String dummyField) {
        this.dummyField = dummyField
    }
}
