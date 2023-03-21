package io.github.dynamixon.test

import io.github.dynamixon.flexorm.enums.CondAndOr
import io.github.dynamixon.flexorm.logic.SqlBuilder
import io.github.dynamixon.flexorm.pojo.Cond
import io.github.dynamixon.flexorm.pojo.InnerCond
import io.github.dynamixon.flexorm.pojo.SqlValuePart
import org.apache.commons.lang3.StringUtils
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SqlBuilderTest {
    protected static final Logger logger = LoggerFactory.getLogger(SqlBuilderTest.class)

    private static String regulateSqlPart(String sqlPart){
        return StringUtils.trim(sqlPart.replaceAll('\\(\\s+','(')
            .replaceAll('\\s+\\)',')')
            .replaceAll('\\s+', ' '))
    }
    private static void assertEqual(SqlValuePart expected, SqlValuePart actual){
        assert expected.getSqlPart() == regulateSqlPart(actual.getSqlPart())
        assert expected.getValueParts().size() == actual.getValueParts().size()
        expected.getValueParts().eachWithIndex { Object entry, int i ->
            assert entry == actual.getValueParts()[i]
        }
    }

    @Test
    void testInnerAndOrBasic(){
        def sqlBuilder = new SqlBuilder()
        SqlValuePart expected = new SqlValuePart(
            sqlPart: '(f1 = ? and f2 = ?) or (f3 = ? and f4 = ?)',
            valueParts: [1,2,3,4]
        )
        def sqlValuePart = sqlBuilder.buildCondPart(CondAndOr.OR.value, [
            new Cond.Builder().innerCond(new InnerCond(
                [new Cond('f1', 1), new Cond('f2', 2)]
            )).build(),
            new Cond.Builder().innerCond(new InnerCond(
                [new Cond('f3', 3), new Cond('f4', 4)]
            )).build()
        ])
        logger.info("1 sqlValuePart=$sqlValuePart")
        assertEqual(expected,sqlValuePart)

        expected = new SqlValuePart(
            sqlPart: 'out = ? and ((f1 = ? and f2 = ?) or (f3 = ? and f4 = ?))',
            valueParts: [0,1,2,3,4]
        )
        sqlValuePart = sqlBuilder.buildCondPart(CondAndOr.AND.value, [
            new Cond('out',0),
            new Cond.Builder()
                .innerCond(new InnerCond(CondAndOr.OR,[
                    new Cond.Builder().innerCond(new InnerCond(
                        [new Cond('f1', 1), new Cond('f2', 2)]
                    )
                    ).build(),
                    new Cond.Builder().innerCond(new InnerCond(
                        [new Cond('f3', 3), new Cond('f4', 4)]
                    )
                    ).build()
                ]))
                .build()
        ])
        logger.info("2 sqlValuePart=$sqlValuePart")
        assertEqual(expected,sqlValuePart)

        expected = new SqlValuePart(
            sqlPart: 'out = ? and ((f1 = ?) or (f2 = ?))',
            valueParts: [0,1,2]
        )
        sqlValuePart = sqlBuilder.buildCondPart(CondAndOr.AND.value, [
            new Cond('out',0),
            new Cond.Builder()
                .innerCond(new InnerCond(CondAndOr.OR,[
                    new Cond.Builder().innerCond(new InnerCond(
                        [new Cond('f1', 1)]
                    )
                    ).build(),
                    new Cond.Builder().innerCond(new InnerCond(
                        [new Cond('f2', 2)]
                    )
                    ).build()
                ]))
                .build()
        ])
        logger.info("3 sqlValuePart=$sqlValuePart")
        assertEqual(expected,sqlValuePart)

        expected = new SqlValuePart(
            sqlPart: 'out = ? and ((x1 = ? and (f1 = ? and f2 = ?)) or (x2 = ? and (f3 = ? and f4 = ?)))',
            valueParts: [0,'x1',1,2,'x2',3,4]
        )
        sqlValuePart = sqlBuilder.buildCondPart(CondAndOr.AND.value, [
            new Cond('out',0),
            new Cond.Builder()
                .innerCond(new InnerCond(CondAndOr.OR,[
                    new Cond.Builder().columnName('x1').compareOpr('=').value('x1').innerCond(
                        new InnerCond([new Cond('f1', 1), new Cond('f2', 2)])
                    ).build(),
                    new Cond.Builder().columnName('x2').compareOpr('=').value('x2').innerCond(
                        new InnerCond([new Cond('f3', 3), new Cond('f4', 4)])
                    ).build()
                ]))
                .build()
        ])
        logger.info("4 sqlValuePart=$sqlValuePart")
        assertEqual(expected,sqlValuePart)

        expected = new SqlValuePart(
            sqlPart: 'out = ? and ((x1 = ? or (f1 = ? and f2 = ?)) or (x2 = ? or (f3 = ? and f4 = ?)))',
            valueParts: [0,'x1',1,2,'x2',3,4]
        )
        sqlValuePart = sqlBuilder.buildCondPart(CondAndOr.AND.value, [
            new Cond('out',0),
            new Cond.Builder()
                .innerCond(new InnerCond(CondAndOr.OR,[
                    new Cond.Builder().columnName('x1').compareOpr('=').value('x1').andOrToInner(CondAndOr.OR).innerCond(
                        new InnerCond([new Cond('f1', 1), new Cond('f2', 2)])
                    ).build(),
                    new Cond.Builder().columnName('x2').compareOpr('=').value('x2').andOrToInner(CondAndOr.OR).innerCond(
                        new InnerCond([new Cond('f3', 3), new Cond('f4', 4)])
                    ).build()
                ]))
                .build()
        ])
        logger.info("5 sqlValuePart=$sqlValuePart")
        assertEqual(expected,sqlValuePart)

        expected = new SqlValuePart(
            sqlPart: 'out = ? and ((x1 = ? and (f1 = ? or f2 = ?)) or (x2 = ? and (f3 = ? or f4 = ?)))',
            valueParts: [0,'x1',1,2,'x2',3,4]
        )
        sqlValuePart = sqlBuilder.buildCondPart(CondAndOr.AND.value, [
            new Cond('out',0),
            new Cond.Builder()
                .innerCond(new InnerCond(CondAndOr.OR,[
                    new Cond.Builder().columnName('x1').compareOpr('=').value('x1').innerCond(
                        new InnerCond(CondAndOr.OR,[new Cond('f1', 1), new Cond('f2', 2)])
                    ).build(),
                    new Cond.Builder().columnName('x2').compareOpr('=').value('x2').innerCond(
                        new InnerCond(CondAndOr.OR,[new Cond('f3', 3), new Cond('f4', 4)])
                    ).build()
                ]))
                .build()
        ])
        logger.info("6 sqlValuePart=$sqlValuePart")
        assertEqual(expected,sqlValuePart)

        expected = new SqlValuePart(
            sqlPart: 'out = ? and ((x1 = ? and (f1 = ? or f2 in (?,?,?))) or (x2 = ? and (f3 = ? or f4 between ? and ?)))',
            valueParts: [0,'x1',1,21,22,23,'x2',3,41,42]
        )
        sqlValuePart = sqlBuilder.buildCondPart(CondAndOr.AND.value, [
            new Cond('out',0),
            new Cond.Builder()
                .innerCond(new InnerCond(CondAndOr.OR,[
                    new Cond.Builder().columnName('x1').compareOpr('=').value('x1').innerCond(
                        new InnerCond(CondAndOr.OR,[new Cond('f1', 1), new Cond('f2','in', [21,22,23])])
                    ).build(),
                    new Cond.Builder().columnName('x2').compareOpr('=').value('x2').innerCond(
                        new InnerCond(CondAndOr.OR,[new Cond('f3', 3), new Cond('f4', 'between',[41,42])])
                    ).build()
                ]))
                .build()
        ])
        logger.info("6 sqlValuePart=$sqlValuePart")
        assertEqual(expected,sqlValuePart)
    }
}
