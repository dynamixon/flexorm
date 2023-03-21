package io.github.dynamixon.dataobject.sub

import io.github.dynamixon.flexorm.annotation.Table

@Table("sub_no_such_tbl")
class SubNoSuchTableGv {
    private String dummyField

    String getDummyField() {
        return dummyField
    }

    void setDummyField(String dummyField) {
        this.dummyField = dummyField
    }
}
