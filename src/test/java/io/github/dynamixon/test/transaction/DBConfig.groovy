package io.github.dynamixon.test.transaction

import io.github.dynamixon.flexorm.QueryEntry
import io.github.dynamixon.flexorm.misc.GeneralThreadLocal
import io.github.dynamixon.test.CommonInfo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.transaction.TransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.sql.DataSource
import java.util.concurrent.ConcurrentHashMap

@EnableTransactionManagement
@Configuration
class DBConfig {

    private static Map<String,TransactionAwareDataSourceProxy> dataSourceProxyMap = new ConcurrentHashMap<>()

    @Bean
    DataSource dataSource(){
        String dbType = GeneralThreadLocal.get("db_type")
        if(dataSourceProxyMap.containsKey(dbType)){
            return dataSourceProxyMap.get(dbType)
        }else {
            def dataSourceProxy = new TransactionAwareDataSourceProxy(CommonInfo.getDataSource(dbType))
            dataSourceProxyMap.put(dbType,dataSourceProxy)
            return dataSourceProxy
        }
    }

    @Bean
    QueryEntry queryEntry(DataSource dataSource){
        return new QueryEntry(dataSource)
    }

    @Bean
    TransactionManager transactionManager(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource)
    }
}
