package io.github.dynamixon.test.sqlparse

import net.sf.jsqlparser.util.validation.Validation
import net.sf.jsqlparser.util.validation.ValidationError
import net.sf.jsqlparser.util.validation.feature.DatabaseType

class SqlParseTest {

    static void main(String[] args) {
        String sql = '''select * from 
( select a.*, ROWNUM rnum from 
  ( SELECT * 
FROM   sometable
ORDER BY name ) a 
  where ROWNUM <= 10 )
where rnum  >= 0;
'''

// validate statement if it's valid for all given databases.
        Validation validation = new Validation(Arrays.asList(DatabaseType.ORACLE), sql);
        List<ValidationError> errors = validation.validate();
        println errors
    }
}
