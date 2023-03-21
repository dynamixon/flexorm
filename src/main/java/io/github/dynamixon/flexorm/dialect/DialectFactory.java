package io.github.dynamixon.flexorm.dialect;


import java.util.Arrays;
import java.util.List;

import static io.github.dynamixon.flexorm.dialect.DialectConst.*;


public class DialectFactory {
    public static List<String> SUPPORTED_DB = Arrays.asList(MYSQL,PG,H2,SQLITE,HSQLDB,MSSQL);
}
