package io.github.dynamixon.test


import com.google.common.reflect.ClassPath
import io.github.dynamixon.flexorm.misc.MetaHolder
import io.github.dynamixon.test.parallel.FunctionWrapper
import io.github.dynamixon.test.parallel.ParallelTaskHub
import org.apache.commons.collections.CollectionUtils
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.function.Function

class MetaHolderTest {
    protected static final Logger logger = LoggerFactory.getLogger(SqlBuilderTest.class)

    private static Set<Class<?>> classes(){
        return ClassPath.from(ClassLoader.getSystemClassLoader()).getAllClasses()
            .findAll {it.packageName.contains('org.spring')}
            .collect().collect({try {
            def load = it.load()
            println load.getName()
            return load
        } catch (Throwable t) {
          //ignore
            return MetaHolderTest.class
        }}).toSet()
    }
    @Test
    void synchronizedPutTest(){
        System.setProperty('weak.hash.map.as.cache','true')
        try {
            MetaHolder metaHolder = new MetaHolder()
            def classes = classes()
            List<FunctionWrapper> tasks = []
            classes.each {
                tasks.add(new FunctionWrapper(new Function<Void, Void>() {
                    @Override
                    Void apply(Void param) {
                        def tableNameMap = metaHolder.getTableNameMap()
                        metaHolder.putToMap(tableNameMap,it,it.getName())
                        logger.info('class='+tableNameMap.get(it))
                        return null
                    }
                },null))
                tasks.add(new FunctionWrapper(new Function<Void, Void>() {
                    @Override
                    Void apply(Void param) {
                        def tableNameMap = metaHolder.getTableNameMap()
                        logger.info('class22='+tableNameMap.get(it))
                        return null
                    }
                },null))
            }
            ParallelTaskHub.exeTasksWithParam(tasks)
            def tableNameMap = metaHolder.getTableNameMap()
            def size = tableNameMap.keySet().size()
            def classSize = classes.size()
            def classesSet = tableNameMap.keySet().collect {it.name}
            def misClasses = classes.findAll { !classesSet.contains(it.name) }
            assert CollectionUtils.isEmpty(misClasses)
            assert size == classSize
        } finally {
            System.setProperty('weak.hash.map.as.cache','false')
        }
    }
}
