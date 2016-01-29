package com.mlizhi.base.dao.query;

import android.database.Cursor;
import com.mlizhi.base.dao.AbstractDao;
import com.mlizhi.base.dao.DaoException;

public class CountQuery<T> extends AbstractQuery<T> {
    private final QueryData<T> queryData;

    private static final class QueryData<T2> extends AbstractQueryData<T2, CountQuery<T2>> {
        private QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues) {
            super(dao, sql, initialValues);
        }

        protected CountQuery<T2> createQuery() {
            return new CountQuery(this.dao, this.sql, (String[]) this.initialValues.clone(), null);
        }
    }

    public /* bridge */ /* synthetic */ void setParameter(int i, Object obj) {
        super.setParameter(i, obj);
    }

    static <T2> CountQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        return (CountQuery) new QueryData(sql, AbstractQuery.toStringArray(initialValues), null).forCurrentThread();
    }

    private CountQuery(QueryData<T> queryData, AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        super(dao, sql, initialValues);
        this.queryData = queryData;
    }

    public CountQuery<T> forCurrentThread() {
        return (CountQuery) this.queryData.forCurrentThread(this);
    }

    public long count() {
        checkThread();
        Cursor cursor = this.dao.getDatabase().rawQuery(this.sql, this.parameters);
        try {
            if (!cursor.moveToNext()) {
                throw new DaoException("No result for count");
            } else if (!cursor.isLast()) {
                throw new DaoException("Unexpected row count: " + cursor.getCount());
            } else if (cursor.getColumnCount() != 1) {
                throw new DaoException("Unexpected column count: " + cursor.getColumnCount());
            } else {
                long j = cursor.getLong(0);
                return j;
            }
        } finally {
            cursor.close();
        }
    }
}
