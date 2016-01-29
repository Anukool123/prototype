package com.mlizhi.base.dao.query;

import android.database.sqlite.SQLiteDatabase;
import com.mlizhi.base.dao.AbstractDao;

public class DeleteQuery<T> extends AbstractQuery<T> {
    private final QueryData<T> queryData;

    private static final class QueryData<T2> extends AbstractQueryData<T2, DeleteQuery<T2>> {
        private QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues) {
            super(dao, sql, initialValues);
        }

        protected DeleteQuery<T2> createQuery() {
            return new DeleteQuery(this.dao, this.sql, (String[]) this.initialValues.clone(), null);
        }
    }

    public /* bridge */ /* synthetic */ void setParameter(int i, Object obj) {
        super.setParameter(i, obj);
    }

    static <T2> DeleteQuery<T2> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues) {
        return (DeleteQuery) new QueryData(sql, AbstractQuery.toStringArray(initialValues), null).forCurrentThread();
    }

    private DeleteQuery(QueryData<T> queryData, AbstractDao<T, ?> dao, String sql, String[] initialValues) {
        super(dao, sql, initialValues);
        this.queryData = queryData;
    }

    public DeleteQuery<T> forCurrentThread() {
        return (DeleteQuery) this.queryData.forCurrentThread(this);
    }

    public void executeDeleteWithoutDetachingEntities() {
        checkThread();
        SQLiteDatabase db = this.dao.getDatabase();
        if (db.isDbLockedByCurrentThread()) {
            this.dao.getDatabase().execSQL(this.sql, this.parameters);
            return;
        }
        db.beginTransaction();
        try {
            this.dao.getDatabase().execSQL(this.sql, this.parameters);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
