package com.mlizhi.base.dao.query;

import com.mlizhi.base.dao.AbstractDao;
import com.mlizhi.base.dao.DaoException;
import com.mlizhi.base.dao.DaoLog;
import com.mlizhi.base.dao.InternalQueryDaoAccess;
import com.mlizhi.base.dao.Property;
import com.mlizhi.base.dao.internal.SqlUtils;
import com.mlizhi.base.dao.query.WhereCondition.PropertyCondition;
import com.mlizhi.base.dao.query.WhereCondition.StringCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class QueryBuilder<T> {
    public static boolean LOG_SQL;
    public static boolean LOG_VALUES;
    private final AbstractDao<T, ?> dao;
    private StringBuilder joinBuilder;
    private Integer limit;
    private Integer offset;
    private StringBuilder orderBuilder;
    private final String tablePrefix;
    private final List<Object> values;
    private final List<WhereCondition> whereConditions;

    public static <T2> QueryBuilder<T2> internalCreate(AbstractDao<T2, ?> dao) {
        return new QueryBuilder(dao);
    }

    protected QueryBuilder(AbstractDao<T, ?> dao) {
        this(dao, "T");
    }

    protected QueryBuilder(AbstractDao<T, ?> dao, String tablePrefix) {
        this.dao = dao;
        this.tablePrefix = tablePrefix;
        this.values = new ArrayList();
        this.whereConditions = new ArrayList();
    }

    private void checkOrderBuilder() {
        if (this.orderBuilder == null) {
            this.orderBuilder = new StringBuilder();
        } else if (this.orderBuilder.length() > 0) {
            this.orderBuilder.append(",");
        }
    }

    public QueryBuilder<T> where(WhereCondition cond, WhereCondition... condMore) {
        this.whereConditions.add(cond);
        for (WhereCondition whereCondition : condMore) {
            checkCondition(whereCondition);
            this.whereConditions.add(whereCondition);
        }
        return this;
    }

    public QueryBuilder<T> whereOr(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        this.whereConditions.add(or(cond1, cond2, condMore));
        return this;
    }

    public WhereCondition or(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return combineWhereConditions(" OR ", cond1, cond2, condMore);
    }

    public WhereCondition and(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return combineWhereConditions(" AND ", cond1, cond2, condMore);
    }

    protected WhereCondition combineWhereConditions(String combineOp, WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        StringBuilder builder = new StringBuilder("(");
        List<Object> combinedValues = new ArrayList();
        addCondition(builder, combinedValues, cond1);
        builder.append(combineOp);
        addCondition(builder, combinedValues, cond2);
        for (WhereCondition cond : condMore) {
            builder.append(combineOp);
            addCondition(builder, combinedValues, cond);
        }
        builder.append(')');
        return new StringCondition(builder.toString(), combinedValues.toArray());
    }

    protected void addCondition(StringBuilder builder, List<Object> values, WhereCondition condition) {
        checkCondition(condition);
        condition.appendTo(builder, this.tablePrefix);
        condition.appendValuesTo(values);
    }

    protected void checkCondition(WhereCondition whereCondition) {
        if (whereCondition instanceof PropertyCondition) {
            checkProperty(((PropertyCondition) whereCondition).property);
        }
    }

    public <J> QueryBuilder<J> join(Class<J> cls, Property toOneProperty) {
        throw new UnsupportedOperationException();
    }

    public <J> QueryBuilder<J> joinToMany(Class<J> cls, Property toManyProperty) {
        throw new UnsupportedOperationException();
    }

    public QueryBuilder<T> orderAsc(Property... properties) {
        orderAscOrDesc(" ASC", properties);
        return this;
    }

    public QueryBuilder<T> orderDesc(Property... properties) {
        orderAscOrDesc(" DESC", properties);
        return this;
    }

    private void orderAscOrDesc(String ascOrDescWithLeadingSpace, Property... properties) {
        for (Property property : properties) {
            checkOrderBuilder();
            append(this.orderBuilder, property);
            if (String.class.equals(property.type)) {
                this.orderBuilder.append(" COLLATE LOCALIZED");
            }
            this.orderBuilder.append(ascOrDescWithLeadingSpace);
        }
    }

    public QueryBuilder<T> orderCustom(Property property, String customOrderForProperty) {
        checkOrderBuilder();
        append(this.orderBuilder, property).append(' ');
        this.orderBuilder.append(customOrderForProperty);
        return this;
    }

    public QueryBuilder<T> orderRaw(String rawOrder) {
        checkOrderBuilder();
        this.orderBuilder.append(rawOrder);
        return this;
    }

    protected StringBuilder append(StringBuilder builder, Property property) {
        checkProperty(property);
        builder.append(this.tablePrefix).append('.').append('\'').append(property.columnName).append('\'');
        return builder;
    }

    protected void checkProperty(Property property) {
        if (this.dao != null) {
            boolean found = false;
            for (Property property2 : this.dao.getProperties()) {
                if (property == property2) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new DaoException("Property '" + property.name + "' is not part of " + this.dao);
            }
        }
    }

    public QueryBuilder<T> limit(int limit) {
        this.limit = Integer.valueOf(limit);
        return this;
    }

    public QueryBuilder<T> offset(int offset) {
        this.offset = Integer.valueOf(offset);
        return this;
    }

    public Query<T> build() {
        String select;
        if (this.joinBuilder == null || this.joinBuilder.length() == 0) {
            select = InternalQueryDaoAccess.getStatements(this.dao).getSelectAll();
        } else {
            select = SqlUtils.createSqlSelect(this.dao.getTablename(), this.tablePrefix, this.dao.getAllColumns());
        }
        StringBuilder builder = new StringBuilder(select);
        appendWhereClause(builder, this.tablePrefix);
        if (this.orderBuilder != null && this.orderBuilder.length() > 0) {
            builder.append(" ORDER BY ").append(this.orderBuilder);
        }
        int limitPosition = -1;
        if (this.limit != null) {
            builder.append(" LIMIT ?");
            this.values.add(this.limit);
            limitPosition = this.values.size() - 1;
        }
        int offsetPosition = -1;
        if (this.offset != null) {
            if (this.limit == null) {
                throw new IllegalStateException("Offset cannot be set without limit");
            }
            builder.append(" OFFSET ?");
            this.values.add(this.offset);
            offsetPosition = this.values.size() - 1;
        }
        String sql = builder.toString();
        if (LOG_SQL) {
            DaoLog.m20d("Built SQL for query: " + sql);
        }
        if (LOG_VALUES) {
            DaoLog.m20d("Values for query: " + this.values);
        }
        return Query.create(this.dao, sql, this.values.toArray(), limitPosition, offsetPosition);
    }

    public DeleteQuery<T> buildDelete() {
        String tablename = this.dao.getTablename();
        StringBuilder builder = new StringBuilder(SqlUtils.createSqlDelete(tablename, null));
        appendWhereClause(builder, this.tablePrefix);
        String sql = builder.toString().replace(this.tablePrefix + ".'", new StringBuilder(String.valueOf(tablename)).append(".'").toString());
        if (LOG_SQL) {
            DaoLog.m20d("Built SQL for delete query: " + sql);
        }
        if (LOG_VALUES) {
            DaoLog.m20d("Values for delete query: " + this.values);
        }
        return DeleteQuery.create(this.dao, sql, this.values.toArray());
    }

    public CountQuery<T> buildCount() {
        StringBuilder builder = new StringBuilder(SqlUtils.createSqlSelectCountStar(this.dao.getTablename(), this.tablePrefix));
        appendWhereClause(builder, this.tablePrefix);
        String sql = builder.toString();
        if (LOG_SQL) {
            DaoLog.m20d("Built SQL for count query: " + sql);
        }
        if (LOG_VALUES) {
            DaoLog.m20d("Values for count query: " + this.values);
        }
        return CountQuery.create(this.dao, sql, this.values.toArray());
    }

    private void appendWhereClause(StringBuilder builder, String tablePrefixOrNull) {
        this.values.clear();
        if (!this.whereConditions.isEmpty()) {
            builder.append(" WHERE ");
            ListIterator<WhereCondition> iter = this.whereConditions.listIterator();
            while (iter.hasNext()) {
                if (iter.hasPrevious()) {
                    builder.append(" AND ");
                }
                WhereCondition condition = (WhereCondition) iter.next();
                condition.appendTo(builder, tablePrefixOrNull);
                condition.appendValuesTo(this.values);
            }
        }
    }

    public List<T> list() {
        return build().list();
    }

    public LazyList<T> listLazy() {
        return build().listLazy();
    }

    public LazyList<T> listLazyUncached() {
        return build().listLazyUncached();
    }

    public CloseableListIterator<T> listIterator() {
        return build().listIterator();
    }

    public T unique() {
        return build().unique();
    }

    public T uniqueOrThrow() {
        return build().uniqueOrThrow();
    }

    public long count() {
        return buildCount().count();
    }
}
