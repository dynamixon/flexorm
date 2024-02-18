package io.github.dynamixon.flexorm.logic;

import io.github.dynamixon.flexorm.CoreRunner;
import io.github.dynamixon.flexorm.annotation.CondOpr;
import io.github.dynamixon.flexorm.dialect.pagination.DefaultPagination;
import io.github.dynamixon.flexorm.dialect.pagination.Pagination;
import io.github.dynamixon.flexorm.enums.CondAndOr;
import io.github.dynamixon.flexorm.misc.DBException;
import io.github.dynamixon.flexorm.misc.FieldInfoMethodRefUtil;
import io.github.dynamixon.flexorm.misc.MiscUtil;
import io.github.dynamixon.flexorm.pojo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

public class SqlBuilder {
    private final CoreRunner coreRunner;
    private final Pagination pagination;

    public SqlBuilder() {
        this.coreRunner = null;
        this.pagination = new DefaultPagination();
    }

    public SqlBuilder(CoreRunner coreRunner) {
        this.coreRunner = coreRunner;
        this.pagination = coreRunner.getPagination();
    }

    private static String cleanSqlCond(String sql) {
        return sql.replaceAll("where 1=1\\s+and", "where");
    }

    public SqlValuePart buildCondPart(String andOr, List<Cond> conds) {
        return buildCondPart(andOr, conds, false);
    }

    public SqlValuePart buildCondPart(String andOr, List<Cond> conds, boolean inner) {
        if (CollectionUtils.isEmpty(conds)) {
            return null;
        }
        StringBuilder condPart = new StringBuilder();
        List<Object> values = new ArrayList<>();
        boolean firstUnit = true;
        for (Cond unit : conds) {
            String leftP = inner && unit.hasInnerConds() ? "(" : "";
            int origLength = condPart.length();

            String field = unit.getColumnName();
            String operator = StringUtils.trimToEmpty(unit.getCompareOpr()).toLowerCase().replaceAll("\\s+", " ");
            Object value = unit.getValue();
            if (("in".equalsIgnoreCase(operator) || "not in".equalsIgnoreCase(operator)) && value != null) {
                Collection<?> inValueList;
                if (value.getClass().isArray()) {
                    inValueList = MiscUtil.fromArray(value);
                } else if (value instanceof Collection) {
                    inValueList = (Collection<?>) value;
                } else {
                    throw new DBException("value must be an array or a collection when operator is [" + operator + "]");
                }
                if (CollectionUtils.isNotEmpty(inValueList)) {
                    condPart.append(andOrConcat(andOr, firstUnit)).append(leftP).append(field).append(" ").append(operator).append(" (");
                    for (Object valueTmp : inValueList) {
                        condPart.append("?,");
                        values.add(valueTmp);
                    }
                    condPart.deleteCharAt(condPart.length() - 1);
                    condPart.append(") ");
                    firstUnit = false;
                }
            } else if (("between".equalsIgnoreCase(operator) || "not between".equalsIgnoreCase(operator)) && value != null) {
                Object v1 = null;
                Object v2 = null;
                List<?> listValue;
                if (value.getClass().isArray()) {
                    listValue = MiscUtil.fromArray(value);
                } else if (value instanceof List) {
                    listValue = (List<?>) value;
                } else {
                    throw new DBException("value must be an array or a list when operator is [" + operator + "]");
                }
                if (CollectionUtils.isNotEmpty(listValue) && listValue.size() == 2) {
                    v1 = listValue.get(0);
                    v2 = listValue.get(1);
                }
                if (v1 != null && v2 != null) {
                    condPart.append(andOrConcat(andOr, firstUnit)).append(leftP).append(field).append(" ").append(operator).append(" ? and ? ");
                    values.add(v1);
                    values.add(v2);
                    firstUnit = false;
                }
            } else if (value == null) {
                if (!unit.ignoreNull()) {
                    condPart.append(andOrConcat(andOr, firstUnit)).append(leftP).append(field).append(" ").append(operator).append(" ");
                    firstUnit = false;
                }
            } else if (value instanceof Null) {
                condPart.append(andOrConcat(andOr, firstUnit)).append(leftP).append(field).append(" is null ");
                firstUnit = false;
            } else {
                condPart.append(andOrConcat(andOr, firstUnit)).append(leftP).append(field).append(" ").append(operator).append(" ?");
                values.add(value);
                firstUnit = false;
            }

            SqlValuePart innerPart = null;
            if (unit.hasInnerConds()) {
                innerPart = buildCondPart(unit.innerCondAndOr().getValue(), unit.innerCondList(), true);
            }
            if (innerPart != null && innerPart.valid()) {
                String prepend;
                boolean emptyUnit = origLength == condPart.length();
                boolean condPartEmpty = condPart.length() == 0;
                if (!inner || !emptyUnit) {
                    String andOrToInner = !emptyUnit?unit.andOrToInner().getValue():andOr;
                    prepend = condPartEmpty ? "(" : " "+andOrToInner+" (";
                    condPart.append(prepend).append(innerPart.getSqlPart()).append(") ");
                } else {
                    prepend = condPartEmpty ? " " : andOr + " ";
                    condPart.append(prepend).append("(").append(innerPart.getSqlPart()).append(") ");
                }
                if (inner && !emptyUnit) {
                    // paired with the leftP appended above
                    condPart.append(") ");
                }
                values.addAll(innerPart.getValueParts());
            }
        }
        return new SqlValuePart(condPart.toString(), values);
    }

    private String andOrConcat(String andOr, boolean firstUnit) {
        return firstUnit ? "" : " " + andOr + " ";
    }

    private void fillJoinPart(QueryConditionBundle qc, StringBuilder select, List<Object> values) {
        List<JoinInstruction> joinInstructions = qc.getJoinInstructions();
        if(CollectionUtils.isEmpty(joinInstructions)){
            return;
        }
        joinInstructions.forEach(joinInstruction -> {
            SqlValuePart sqlValuePart = buildCondPart(CondAndOr.AND.getValue(), joinInstruction.getJoinConds());
            if(sqlValuePart != null && sqlValuePart.valid()){
                select.append(" on ").append(sqlValuePart.getSqlPart());
                values.addAll(sqlValuePart.getValueParts());
            }
        });
    }

    private void fillWherePart(ConditionBundle cond, StringBuilder where, List<Object> values) {
        if (cond == null) {
            return;
        }
        List<Cond> conditionAndList = cond.getConditionAndList();
        SqlValuePart andPart = buildCondPart("and", conditionAndList);
        if (andPart != null && andPart.valid()) {
            where.append(" and ").append(andPart.getSqlPart());
            values.addAll(andPart.getValueParts());
        }
        List<Cond> conditionOrList = cond.getConditionOrList();
        SqlValuePart orPart = buildCondPart("or", conditionOrList);
        if (orPart != null && orPart.valid()) {
            where.append(" and (").append(orPart.getSqlPart()).append(")");
            values.addAll(orPart.getValueParts());
        }
    }

    private void fillGroupByPart(List<String> groupByColumns, StringBuilder where) {
        if (CollectionUtils.isNotEmpty(groupByColumns)) {
            where.append(" group by");
            for (String groupByColumn : groupByColumns) {
                where.append(" ").append(groupByColumn).append(",");
            }
            where.deleteCharAt(where.length() - 1);
        }
    }

    private void fillHavingPart(List<Cond> havingConds, StringBuilder where, List<Object> values) {
        SqlValuePart sqlValuePart = buildCondPart("and", havingConds);
        if (sqlValuePart != null && sqlValuePart.valid()) {
            where.append(" having ");
            where.append(sqlValuePart.getSqlPart());
            values.addAll(sqlValuePart.getValueParts());
        }
    }

    private void fillOrderByPart(List<OrderCond> orderConds, StringBuilder where) {
        if (CollectionUtils.isNotEmpty(orderConds)) {
            where.append(" order by");
            for (OrderCond orderCond : orderConds) {
                String orderByField = orderCond.getOrderByColumn();
                String orderByType = orderCond.getOrderByType();
                where.append(" ").append(orderByField).append(" ").append(orderByType).append(",");
            }
            where.deleteCharAt(where.length() - 1);
        }
    }

    private String determineTableAliasForJoin(QueryConditionBundle qc) {
        if(CollectionUtils.isEmpty(qc.getJoinInstructions())){
            return null;
        }
        return StringUtils.isBlank(qc.getTableAliasForJoin())?qc.getTargetTable():qc.getTableAliasForJoin();
    }

    private void resolveForJoin(QueryConditionBundle qc){
        Class<?> tableClass = qc.getTableClass();
        String tableAliasForJoin = qc.getTableAliasForJoin();
        TableObjectMetaCache.initTableObjectMeta(tableClass,coreRunner);
        Map<String, String> columnToFieldMap = TableObjectMetaCache.getColumnToFieldMap(tableClass,coreRunner.getDataSource());
        Set<String> columnsNames = columnToFieldMap.keySet();
        qc.setSelectColumns(getColumnsForJoin(columnsNames,qc.getSelectColumns(),tableAliasForJoin));
        qc.setGroupByColumns(getColumnsForJoin(columnsNames,qc.getGroupByColumns(),tableAliasForJoin));
        qc.setOrderConds(getOrderCondsForJoin(columnsNames,qc.getOrderConds(),tableAliasForJoin));
        qc.setConditionAndList(getCondListForJoin(columnsNames,qc.getConditionAndList(),tableAliasForJoin));
        qc.setConditionOrList(getCondListForJoin(columnsNames,qc.getConditionOrList(),tableAliasForJoin));
        qc.setHavingConds(getCondListForJoin(columnsNames,qc.getHavingConds(),tableAliasForJoin));
    }

    private List<String> getColumnsForJoin(Set<String> tableColumnsNames,List<String> columns,String tableAliasForJoin){
        if(StringUtils.isBlank(tableAliasForJoin) || CollectionUtils.isEmpty(columns)){
            return columns;
        }
        List<String> columnsForJoin = new ArrayList<>();
        columns.forEach(column -> {
            if(tableColumnsNames.contains(column)){
                columnsForJoin.add(tableAliasForJoin+"."+column);
            }else {
                columnsForJoin.add(column);
            }
        });
        return columnsForJoin;
    }

    private List<OrderCond> getOrderCondsForJoin(Set<String> tableColumnsNames,List<OrderCond> orderConds,String tableAliasForJoin){
        if(StringUtils.isBlank(tableAliasForJoin) || CollectionUtils.isEmpty(orderConds)){
            return orderConds;
        }
        List<OrderCond> orderCondsForJoin = new ArrayList<>();
        orderConds.forEach(orderCond -> {
            if(tableColumnsNames.contains(orderCond.getOrderByColumn())){
                orderCond.setOrderByColumn(tableAliasForJoin+"."+orderCond.getOrderByColumn());
            }
            orderCondsForJoin.add(orderCond);
        });
        return orderCondsForJoin;
    }

    private List<Cond> getCondListForJoin(Set<String> tableColumnsNames,List<Cond> condList,String tableAliasForJoin){
        if(StringUtils.isBlank(tableAliasForJoin) || CollectionUtils.isEmpty(condList)){
            return condList;
        }
        List<Cond> condListForJoin = new ArrayList<>();
        condList.forEach(cond -> {
            InnerCond innerCond = cond.getInnerCond();
            if(innerCond !=null&&CollectionUtils.isNotEmpty(innerCond.getInnerCondList())){
                cond.setInnerCond(new InnerCond(innerCond.getInnerCondAndOr(),getCondListForJoin(tableColumnsNames,innerCond.getInnerCondList(),tableAliasForJoin)));
            }
            if(tableColumnsNames.contains(cond.getColumnName())){
                cond.setColumnName(tableAliasForJoin+"."+cond.getColumnName());
            }
            condListForJoin.add(cond);
        });
        return condListForJoin;
    }

    public SqlPreparedBundle composeSelect(QueryConditionBundle qc) {
        resolveColumnNameFromFieldInfoGetter(qc);
        SqlPreparedBundle sp = new SqlPreparedBundle();
        StringBuilder where = new StringBuilder(" where 1=1 ");
        List<Object> values = new ArrayList<>();
        StringBuilder select = new StringBuilder("select");
        List<String> selectColumns = qc.getSelectColumns();
        String mainTableAliasForJoin = determineTableAliasForJoin(qc);
        boolean requireJoin = StringUtils.isNotBlank(mainTableAliasForJoin);
        if (selectColumns != null && !selectColumns.isEmpty()) {
            for (String intendedField : selectColumns) {
                select.append(" ").append(intendedField).append(",");
            }
            select.deleteCharAt(select.length() - 1);
            select.append(" from ");
        } else if (qc.isOnlyCount()) {
            select.append(" count(*) as count from ");
        } else {
            String allColumns = "*";
            if(requireJoin){
                allColumns = mainTableAliasForJoin+"."+allColumns;
            }
            select.append(" ").append(allColumns).append(" from ");
        }
        select.append(qc.getTargetTable()).append(" ").append((requireJoin&&!mainTableAliasForJoin.equals(qc.getTargetTable()))?mainTableAliasForJoin+" ":"");

        if(requireJoin){
            resolveForJoin(qc);
            fillJoinPart(qc, select, values);
        }

        int origWhereLength = where.length();
        fillWherePart(qc, where, values);
        sp.setWithCondition(where.length() != origWhereLength);
        fillGroupByPart(qc.getGroupByColumns(), where);
        fillHavingPart(qc.getHavingConds(), where, values);
        fillOrderByPart(qc.getOrderConds(), where);
        String baseSql = cleanSqlCond(select.append(where).toString());
        sp.setSql(pagination.paging(qc.getOffset(), qc.getLimit(), baseSql, values));
        sp.setValues(values.toArray());
        return sp;
    }

    public SqlPreparedBundle composeDelete(ConditionBundle cb) {
        resolveColumnNameFromFieldInfoGetterBase(cb);
        SqlPreparedBundle sp = new SqlPreparedBundle();
        List<Object> values = new ArrayList<>();
        StringBuilder delete = new StringBuilder("delete from ").append(cb.getTargetTable());
        StringBuilder where = new StringBuilder(" where 1=1 ");
        int origWhereLength = where.length();
        fillWherePart(cb, where, values);
        sp.setWithCondition(where.length() != origWhereLength);
        sp.setSql(cleanSqlCond(delete.append(where).toString()));
        sp.setValues(values.toArray());
        return sp;
    }

    public SqlPreparedBundle composeUpdate(UpdateConditionBundle uc) {
        resolveColumnNameFromFieldInfoGetterBase(uc);
        SqlPreparedBundle sp = new SqlPreparedBundle();
        StringBuilder where = new StringBuilder(" where 1=1 ");
        List<Object> values = new ArrayList<>();
        StringBuilder update = new StringBuilder("update ").append(uc.getTargetTable()).append(" ");
        List<FieldValuePair> values2Update = uc.getValues2Update();
        if (CollectionUtils.isNotEmpty(values2Update)) {
            update.append(" set ");
            for (FieldValuePair pair : values2Update) {
                String field = pair.getField();
                Object value = pair.getValue();
                update.append(field).append("=?,");
                values.add(value);
            }
            update.deleteCharAt(update.length() - 1);
            int origWhereLength = where.length();
            fillWherePart(uc, where, values);
            sp.setWithCondition(where.length() != origWhereLength);
            sp.setSql(cleanSqlCond(update.append(where).toString()));
            sp.setValues(values.toArray());
        }
        return sp;
    }

    public List<Cond> buildConds(Object obj) {
        if (obj == null) {
            return null;
        }
        List<Cond> conds = new ArrayList<>();
        try {
            Class<?> clazz = obj.getClass();
            List<Field> fields = MiscUtil.getAllFields(clazz);
            for (Field field : fields) {
                if (field.isSynthetic()) {
                    continue;
                }
                CondOpr condOpr = field.getAnnotation(CondOpr.class);
                if (condOpr == null) {
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value == null) {
                    continue;
                }
                if (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value)) {
                    continue;
                }
                String opr = StringUtils.trimToEmpty(condOpr.value()).toLowerCase().replaceAll("\\s+", " ");
                String columnName = condOpr.columnName();
                if (StringUtils.isBlank(columnName)) {
                    columnName = field.getName();
                }
                if (opr.contains("like")) {
                    value = "%" + value + "%";
                }
                conds.add(new Cond(columnName, opr, value));
            }
        } catch (IllegalAccessException e) {
            throw new DBException(e);
        }
        return conds;
    }

    public void resolveColumnNameFromFieldInfoGetter(QueryConditionBundle qryCondition){
        if(qryCondition==null){
            return;
        }
        resolveColumnNameFromFieldInfoGetterBase(qryCondition);
        FieldInfoMethodRefUtil.resolveColumnNameFromFieldInfoGetter(coreRunner,qryCondition.getHavingConds());
    }

    public void resolveColumnNameFromFieldInfoGetterBase(ConditionBundle conditionBundle){
        if(conditionBundle==null){
            return;
        }
        FieldInfoMethodRefUtil.resolveColumnNameFromFieldInfoGetter(coreRunner,conditionBundle.getConditionAndList());
        FieldInfoMethodRefUtil.resolveColumnNameFromFieldInfoGetter(coreRunner,conditionBundle.getConditionOrList());
    }
}
