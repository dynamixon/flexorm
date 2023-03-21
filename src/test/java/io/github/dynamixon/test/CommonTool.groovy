package io.github.dynamixon.test


import io.github.dynamixon.dataobject.DummyTable
import io.github.dynamixon.dataobject.mysql.DummyTableMysql
import uk.co.jemos.podam.api.PodamFactory
import uk.co.jemos.podam.api.PodamFactoryImpl

class CommonTool {
    static void main(String[] args) {

        def records = generateDummyRecords(DummyTableMysql.class, 10)
        records.each {
            println it
        }
    }

    static <T extends DummyTable> List<T> generateDummyRecords(Class<T> cls, int num){
        PodamFactory factory = new PodamFactoryImpl()
        List<T> records = new ArrayList()
        num.times {
            records.add(factory.manufacturePojo(cls))
        }
        return records
    }
}
