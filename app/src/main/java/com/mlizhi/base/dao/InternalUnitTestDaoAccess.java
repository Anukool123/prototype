package com.mlizhi.base.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mlizhi.base.dao.identityscope.IdentityScope;
import com.mlizhi.base.dao.internal.DaoConfig;

import java.lang.reflect.Constructor;

public class InternalUnitTestDaoAccess<T, K> {
    private final AbstractDao<T, K> dao;
    private final java.lang.Object daoConfig = null;

    public InternalUnitTestDaoAccess(SQLiteDatabase db, Class<AbstractDao<T, K>> daoClass, IdentityScope<?, ?> identityScope) throws Exception {
        DaoConfig daoConfig = new DaoConfig(db, daoClass);
        daoConfig.setIdentityScope(identityScope);
        Constructor<AbstractDao<T, K>> constructor = daoClass.getConstructor(DaoConfig.class);
        dao = constructor.newInstance(daoConfig);
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
