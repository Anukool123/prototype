package com.mlizhi.base.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mlizhi.base.dao.identityscope.IdentityScope;
import com.mlizhi.base.dao.internal.DaoConfig;

public class InternalUnitTestDaoAccess<T, K> {
    private final AbstractDao<T, K> dao;

    public InternalUnitTestDaoAccess(SQLiteDatabase db, Class<AbstractDao<T, K>> daoClass, IdentityScope<?, ?> identityScope) throws Exception {
        new DaoConfig(db, daoClass).setIdentityScope(identityScope);
        this.dao = (AbstractDao) daoClass.getConstructor(new Class[]{DaoConfig.class}).newInstance(new Object[]{daoConfig});
    }

    public K getKey(T entity) {
        return this.dao.getKey(entity);
    }

    public Property[] getProperties() {
        return this.dao.getProperties();
    }

    public boolean isEntityUpdateable() {
        return this.dao.isEntityUpdateable();
    }

    public T readEntity(Cursor cursor, int offset) {
        return this.dao.readEntity(cursor, offset);
    }

    public K readKey(Cursor cursor, int offset) {
        return this.dao.readKey(cursor, offset);
    }

    public AbstractDao<T, K> getDao() {
        return this.dao;
    }
}
