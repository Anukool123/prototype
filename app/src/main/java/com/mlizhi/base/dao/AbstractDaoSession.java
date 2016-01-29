package com.mlizhi.base.dao;

import android.database.sqlite.SQLiteDatabase;
import com.mlizhi.base.dao.async.AsyncSession;
import com.mlizhi.base.dao.query.QueryBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class AbstractDaoSession {
    private final SQLiteDatabase db;
    private final Map<Class<?>, AbstractDao<?, ?>> entityToDao;

    public AbstractDaoSession(SQLiteDatabase db) {
        this.db = db;
        this.entityToDao = new HashMap();
    }

    protected <T> void registerDao(Class<T> entityClass, AbstractDao<T, ?> dao) {
        this.entityToDao.put(entityClass, dao);
    }

    public <T> long insert(T entity) {
        return getDao(entity.getClass()).insert(entity);
    }

    public <T> long insertOrReplace(T entity) {
        return getDao(entity.getClass()).insertOrReplace(entity);
    }

    public <T> void refresh(T entity) {
        getDao(entity.getClass()).refresh(entity);
    }

    public <T> void update(T entity) {
        getDao(entity.getClass()).update(entity);
    }

    public <T> void delete(T entity) {
        getDao(entity.getClass()).delete(entity);
    }

    public <T> void deleteAll(Class<T> entityClass) {
        getDao(entityClass).deleteAll();
    }

    public <T, K> T load(Class<T> entityClass, K key) {
        return getDao(entityClass).load(key);
    }

    public <T, K> List<T> loadAll(Class<T> entityClass) {
        return getDao(entityClass).loadAll();
    }

    public <T, K> List<T> queryRaw(Class<T> entityClass, String where, String... selectionArgs) {
        return getDao(entityClass).queryRaw(where, selectionArgs);
    }

    public <T> QueryBuilder<T> queryBuilder(Class<T> entityClass) {
        return getDao(entityClass).queryBuilder();
    }

    public AbstractDao<?, ?> getDao(Class<? extends Object> entityClass) {
        AbstractDao<?, ?> dao = (AbstractDao) this.entityToDao.get(entityClass);
        if (dao != null) {
            return dao;
        }
        throw new DaoException("No DAO registered for " + entityClass);
    }

    public void runInTx(Runnable runnable) {
        this.db.beginTransaction();
        try {
            runnable.run();
            this.db.setTransactionSuccessful();
        } finally {
            this.db.endTransaction();
        }
    }

    public <V> V callInTx(Callable<V> callable) throws Exception {
        this.db.beginTransaction();
        try {
            V result = callable.call();
            this.db.setTransactionSuccessful();
            return result;
        } finally {
            this.db.endTransaction();
        }
    }

    public <V> V callInTxNoException(Callable<V> callable) {
        this.db.beginTransaction();
        try {
            V result = callable.call();
            this.db.setTransactionSuccessful();
            this.db.endTransaction();
            return result;
        } catch (Exception e) {
            throw new DaoException("Callable failed", e);
        } catch (Throwable th) {
            this.db.endTransaction();
        }
    }

    public SQLiteDatabase getDatabase() {
        return this.db;
    }

    public AsyncSession startAsyncSession() {
        return new AsyncSession(this);
    }
}
