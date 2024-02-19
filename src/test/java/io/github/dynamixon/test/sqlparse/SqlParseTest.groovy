package io.github.dynamixon.test.sqlparse

import net.sf.jsqlparser.util.validation.Validation
import net.sf.jsqlparser.util.validation.ValidationError
import net.sf.jsqlparser.util.validation.feature.DatabaseType

class SqlParseTest {

    static void main(String[] args) {
        String sql = '''select TBL_A.id, TBL_B.int_f from join_table_A TBL_A  left join join_table_B TBL_B on TBL_A.id = TBL_B.id  and TBL_A.varchar_f = ? where TBL_A.id = ? and TBL_B.int_f = ?'''

// validate statement if it's valid for all given databases.
        Validation validation = new Validation(Arrays.asList(DatabaseType.H2), sql);
        List<ValidationError> errors = validation.validate();
        println errors
    }
}
