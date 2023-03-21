package io.github.dynamixon.test.transaction

import io.github.dynamixon.flexorm.QueryEntry
import io.github.dynamixon.flexorm.misc.GeneralThreadLocal
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

class TransactionTest {
    private static final Logger logger = LoggerFactory.getLogger(TransactionTest.class)

    static boolean tx(String dbType,Closure setupCl,Closure txCl,Closure<Boolean> validateCl){
        boolean valid = false
        GeneralThreadLocal.set("db_type", dbType)
        try (ApplicationContext ctx = new AnnotationConfigApplicationContext("io.github.dynamixon.test.transaction")) {
            QueryEntry queryEntry = ctx.getBean(QueryEntry)
            TxService txService = ctx.getBean(TxService)
            try {
                setupCl(queryEntry)
                txService.txTest(txCl)
            } catch (Exception e) {
                logger.error(e.getMessage())
            }
            valid = validateCl(queryEntry)
        }finally{
            GeneralThreadLocal.unset("db_type")
        }
        return valid
    }
}
