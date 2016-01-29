package com.mlizhi.base.dao;

import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.mlizhi.base.dao.identityscope.IdentityScope;
import com.mlizhi.base.dao.identityscope.IdentityScopeLong;
import com.mlizhi.base.dao.internal.DaoConfig;
import com.mlizhi.base.dao.internal.FastCursor;
import com.mlizhi.base.dao.internal.TableStatements;
import com.mlizhi.base.dao.query.Query;
import com.mlizhi.base.dao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractDao<T, K> {
    protected final DaoConfig config;
    protected final SQLiteDatabase db;
    protected IdentityScope<K, T> identityScope;
    protected IdentityScopeLong<T> identityScopeLong;
    protected final int pkOrdinal;
    protected final AbstractDaoSession session;
    protected TableStatements statements;

    protected abstract void bindValues(SQLiteStatement sQLiteStatement, T t);

    protected abstract K getKey(T t);

    protected abstract boolean isEntityUpdateable();

    protected abstract T readEntity(Cursor cursor, int i);

    protected abstract void readEntity(Cursor cursor, T t, int i);

    protected abstract K readKey(Cursor cursor, int i);

    protected abstract K updateKeyAfterInsert(T t, long j);

    public AbstractDao(DaoConfig config) {
        this(config, null);
    }

    public AbstractDao(DaoConfig config, AbstractDaoSession daoSession) {
        this.config = config;
        this.session = daoSession;
        this.db = config.db;
        this.identityScope = config.getIdentityScope();
        if (this.identityScope instanceof IdentityScopeLong) {
            this.identityScopeLong = (IdentityScopeLong) this.identityScope;
        }
        this.statements = config.statements;
        this.pkOrdinal = config.pkProperty != null ? config.pkProperty.ordinal : -1;
    }

    public AbstractDaoSession getSession() {
        return this.session;
    }

    TableStatements getStatements() {
        return this.config.statements;
    }

    public String getTablename() {
        return this.config.tablename;
    }

    public Property[] getProperties() {
        return this.config.properties;
    }

    public Property getPkProperty() {
        return this.config.pkProperty;
    }

    public String[] getAllColumns() {
        return this.config.allColumns;
    }

    public String[] getPkColumns() {
        return this.config.pkColumns;
    }

    public String[] getNonPkColumns() {
        return this.config.nonPkColumns;
    }

    public T load(K key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }
        if (this.identityScope != null) {
            T entity = this.identityScope.get(key);
            if (entity != null) {
                return entity;
            }
        }
        return loadUniqueAndCloseCursor(this.db.rawQuery(this.statements.getSelectByKey(), new String[]{key.toString()}));
    }

    public T loadByRowId(long rowId) {
        return loadUniqueAndCloseCursor(this.db.rawQuery(this.statements.getSelectByRowId(), new String[]{Long.toString(rowId)}));
    }

    protected T loadUniqueAndCloseCursor(Cursor cursor) {
        try {
            T loadUnique = loadUnique(cursor);
            return loadUnique;
        } finally {
            cursor.close();
        }
    }

    protected T loadUnique(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return null;
        }
        if (cursor.isLast()) {
            return loadCurrent(cursor, 0, true);
        }
        throw new DaoException("Expected unique result, but count was " + cursor.getCount());
    }

    public List<T> loadAll() {
        return loadAllAndCloseCursor(this.db.rawQuery(this.statements.getSelectAll(), null));
    }

    public boolean detach(T entity) {
        if (this.identityScope == null) {
            return false;
        }
        return this.identityScope.detach(getKeyVerified(entity), entity);
    }

    protected List<T> loadAllAndCloseCursor(Cursor cursor) {
        try {
            List<T> loadAllFromCursor = loadAllFromCursor(cursor);
            return loadAllFromCursor;
        } finally {
            cursor.close();
        }
    }

    public void insertInTx(Iterable<T> entities) {
        insertInTx(entities, isEntityUpdateable());
    }

    public void insertInTx(T... entities) {
        insertInTx(Arrays.asList(entities), isEntityUpdateable());
    }

    public void insertInTx(Iterable<T> entities, boolean setPrimaryKey) {
        executeInsertInTx(this.statements.getInsertStatement(), entities, setPrimaryKey);
    }

    public void insertOrReplaceInTx(Iterable<T> entities, boolean setPrimaryKey) {
        executeInsertInTx(this.statements.getInsertOrReplaceStatement(), entities, setPrimaryKey);
    }

    public void insertOrReplaceInTx(Iterable<T> entities) {
        insertOrReplaceInTx(entities, isEntityUpdateable());
    }

    public void insertOrReplaceInTx(T... entities) {
        insertOrReplaceInTx(Arrays.asList(entities), isEntityUpdateable());
    }

    private void executeInsertInTx(SQLiteStatement stmt, Iterable<T> entities, boolean setPrimaryKey) {
        this.db.beginTransaction();
        try {
            synchronized (stmt) {
                if (this.identityScope != null) {
                    this.identityScope.lock();
                }
                try {
                    Iterator it = entities.iterator();
                    while (it.hasNext()) {
                        Object entity = it.next();
                        bindValues(stmt, entity);
                        if (setPrimaryKey) {
                            updateKeyAfterInsertAndAttach(entity, stmt.executeInsert(), false);
                        } else {
                            stmt.execute();
                        }
                    }
                    if (this.identityScope != null) {
                        this.identityScope.unlock();
                    }
                } catch (Throwable th) {
                    if (this.identityScope != null) {
                        this.identityScope.unlock();
                    }
                }
            }
            this.db.setTransactionSuccessful();
        } finally {
            this.db.endTransaction();
        }
    }

    public long insert(T entity) {
        return executeInsert(entity, this.statements.getInsertStatement());
    }

    public long insertWithoutSettingPk(T entity) {
        long rowId;
        SQLiteStatement stmt = this.statements.getInsertStatement();
        if (this.db.isDbLockedByCurrentThread()) {
            synchronized (stmt) {
                bindValues(stmt, entity);
                rowId = stmt.executeInsert();
            }
        } else {
            this.db.beginTransaction();
            try {
                synchronized (stmt) {
                    bindValues(stmt, entity);
                    rowId = stmt.executeInsert();
                }
                this.db.setTransactionSuccessful();
            } finally {
                this.db.endTransaction();
            }
        }
        return rowId;
    }

    public long insertOrReplace(T entity) {
        return executeInsert(entity, this.statements.getInsertOrReplaceStatement());
    }

    private long executeInsert(T entity, SQLiteStatement stmt) {
        long rowId;
        if (this.db.isDbLockedByCurrentThread()) {
            synchronized (stmt) {
                bindValues(stmt, entity);
                rowId = stmt.executeInsert();
            }
        } else {
            this.db.beginTransaction();
            try {
                synchronized (stmt) {
                    bindValues(stmt, entity);
                    rowId = stmt.executeInsert();
                }
                this.db.setTransactionSuccessful();
            } finally {
                this.db.endTransaction();
            }
        }
        updateKeyAfterInsertAndAttach(entity, rowId, true);
        return rowId;
    }

    protected void updateKeyAfterInsertAndAttach(T entity, long rowId, boolean lock) {
        if (rowId != -1) {
            attachEntity(updateKeyAfterInsert(entity, rowId), entity, lock);
        } else {
            DaoLog.m28w("Could not insert row (executeInsert returned -1)");
        }
    }

    protected List<T> loadAllFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<T> list = new ArrayList(count);
        if (cursor instanceof CrossProcessCursor) {
            CursorWindow window = ((CrossProcessCursor) cursor).getWindow();
            if (window != null) {
                if (window.getNumRows() == count) {
                    cursor = new FastCursor(window);
                } else {
                    DaoLog.m20d("Window vs. result size: " + window.getNumRows() + "/" + count);
                }
            }
        }
        if (cursor.moveToFirst()) {
            if (this.identityScope != null) {
                this.identityScope.lock();
                this.identityScope.reserveRoom(count);
            }
            while (true) {
                try {
                    list.add(loadCurrent(cursor, 0, false));
                    if (!cursor.moveToNext()) {
                        break;
                    }
                } finally {
                    if (this.identityScope != null) {
                        this.identityScope.unlock();
                    }
                }
            }
        }
        return list;
    }

    protected final T loadCurrent(Cursor cursor, int offset, boolean lock) {
        T entity;
        if (this.identityScopeLong != null) {
            if (offset != 0 && cursor.isNull(this.pkOrdinal + offset)) {
                return null;
            }
            long key = cursor.getLong(this.pkOrdinal + offset);
            entity = lock ? this.identityScopeLong.get2(key) : this.identityScopeLong.get2NoLock(key);
            if (entity != null) {
                return entity;
            }
            entity = readEntity(cursor, offset);
            attachEntity(entity);
            if (lock) {
                this.identityScopeLong.put2(key, entity);
                return entity;
            }
            this.identityScopeLong.put2NoLock(key, entity);
            return entity;
        } else if (this.identityScope != null) {
            K key2 = readKey(cursor, offset);
            if (offset != 0 && key2 == null) {
                return null;
            }
            entity = lock ? this.identityScope.get(key2) : this.identityScope.getNoLock(key2);
            if (entity != null) {
                return entity;
            }
            entity = readEntity(cursor, offset);
            attachEntity(key2, entity, lock);
            return entity;
        } else if (offset != 0 && readKey(cursor, offset) == null) {
            return null;
        } else {
            entity = readEntity(cursor, offset);
            attachEntity(entity);
            return entity;
        }
    }

    protected final <O> O loadCurrentOther(AbstractDao<O, ?> dao, Cursor cursor, int offset) {
        return dao.loadCurrent(cursor, offset, true);
    }

    public List<T> queryRaw(String where, String... selectionArg) {
        return loadAllAndCloseCursor(this.db.rawQuery(new StringBuilder(String.valueOf(this.statements.getSelectAll())).append(where).toString(), selectionArg));
    }

    public Query<T> queryRawCreate(String where, Object... selectionArg) {
        return queryRawCreateListArgs(where, Arrays.asList(selectionArg));
    }

    public Query<T> queryRawCreateListArgs(String where, Collection<Object> selectionArg) {
        return Query.internalCreate(this, new StringBuilder(String.valueOf(this.statements.getSelectAll())).append(where).toString(), selectionArg.toArray());
    }

    public void deleteAll() {
        this.db.execSQL("DELETE FROM '" + this.config.tablename + "'");
        if (this.identityScope != null) {
            this.identityScope.clear();
        }
    }

    public void delete(T entity) {
        assertSinglePk();
        deleteByKey(getKeyVerified(entity));
    }

    public void deleteByKey(K key) {
        assertSinglePk();
        SQLiteStatement stmt = this.statements.getDeleteStatement();
        if (this.db.isDbLockedByCurrentThread()) {
            synchronized (stmt) {
                deleteByKeyInsideSynchronized(key, stmt);
            }
        } else {
            this.db.beginTransaction();
            try {
                synchronized (stmt) {
                    deleteByKeyInsideSynchronized(key, stmt);
                }
                this.db.setTransactionSuccessful();
            } finally {
                this.db.endTransaction();
            }
        }
        if (this.identityScope != null) {
            this.identityScope.remove((Object) key);
        }
    }

    private void deleteByKeyInsideSynchronized(K key, SQLiteStatement stmt) {
        if (key instanceof Long) {
            stmt.bindLong(1, ((Long) key).longValue());
        } else if (key == null) {
            throw new DaoException("Cannot delete entity, key is null");
        } else {
            stmt.bindString(1, key.toString());
        }
        stmt.execute();
    }

    private void deleteInTxInternal(Iterable<T> entities, Iterable<K> keys) {
        assertSinglePk();
        SQLiteStatement stmt = this.statements.getDeleteStatement();
        Iterable keysToRemoveFromIdentityScope = null;
        this.db.beginTransaction();
        try {
            synchronized (stmt) {
                Iterator it;
                if (this.identityScope != null) {
                    this.identityScope.lock();
                    keysToRemoveFromIdentityScope = new ArrayList();
                }
                if (entities != null) {
                    for (T entity : entities) {
                        try {
                            K key = getKeyVerified(entity);
                            deleteByKeyInsideSynchronized(key, stmt);
                            if (keysToRemoveFromIdentityScope != null) {
                                keysToRemoveFromIdentityScope.add(key);
                            }
                        } catch (Throwable th) {
                            if (this.identityScope != null) {
                                this.identityScope.unlock();
                            }
                        }
                    }
                }
                if (keys != null) {
                    it = keys.iterator();
                    while (it.hasNext()) {
                        Object key2 = it.next();
                        deleteByKeyInsideSynchronized(key2, stmt);
                        if (keysToRemoveFromIdentityScope != null) {
                            keysToRemoveFromIdentityScope.add(key2);
                        }
                    }
                }
                if (this.identityScope != null) {
                    this.identityScope.unlock();
                }
            }
            this.db.setTransactionSuccessful();
            if (!(keysToRemoveFromIdentityScope == null || this.identityScope == null)) {
                this.identityScope.remove(keysToRemoveFromIdentityScope);
            }
            this.db.endTransaction();
        } catch (Throwable th2) {
            this.db.endTransaction();
        }
    }

    public void deleteInTx(Iterable<T> entities) {
        deleteInTxInternal(entities, null);
    }

    public void deleteInTx(T... entities) {
        deleteInTxInternal(Arrays.asList(entities), null);
    }

    public void deleteByKeyInTx(Iterable<K> keys) {
        deleteInTxInternal(null, keys);
    }

    public void deleteByKeyInTx(K... keys) {
        deleteInTxInternal(null, Arrays.asList(keys));
    }

    public void refresh(T entity) {
        assertSinglePk();
        K key = getKeyVerified(entity);
        Cursor cursor = this.db.rawQuery(this.statements.getSelectByKey(), new String[]{key.toString()});
        try {
            if (!cursor.moveToFirst()) {
                throw new DaoException("Entity does not exist in the database anymore: " + entity.getClass() + " with key " + key);
            } else if (cursor.isLast()) {
                readEntity(cursor, entity, 0);
                attachEntity(key, entity, true);
            } else {
                throw new DaoException("Expected unique result, but count was " + cursor.getCount());
            }
        } finally {
            cursor.close();
        }
    }

    public void update(T entity) {
        assertSinglePk();
        SQLiteStatement stmt = this.statements.getUpdateStatement();
        if (this.db.isDbLockedByCurrentThread()) {
            synchronized (stmt) {
                updateInsideSynchronized(entity, stmt, true);
            }
            return;
        }
        this.db.beginTransaction();
        try {
            synchronized (stmt) {
                updateInsideSynchronized(entity, stmt, true);
            }
            this.db.setTransactionSuccessful();
        } finally {
            this.db.endTransaction();
        }
    }

    public QueryBuilder<T> queryBuilder() {
        return QueryBuilder.internalCreate(this);
    }

    protected void updateInsideSynchronized(T entity, SQLiteStatement stmt, boolean lock) {
        bindValues(stmt, entity);
        int index = this.config.allColumns.length + 1;
        K key = getKey(entity);
        if (key instanceof Long) {
            stmt.bindLong(index, ((Long) key).longValue());
        } else if (key == null) {
            throw new DaoException("Cannot update entity without key - was it inserted before?");
        } else {
            stmt.bindString(index, key.toString());
        }
        stmt.execute();
        attachEntity(key, entity, lock);
    }

    protected final void attachEntity(K key, T entity, boolean lock) {
        attachEntity(entity);
        if (this.identityScope != null && key != null) {
            if (lock) {
                this.identityScope.put(key, entity);
            } else {
                this.identityScope.putNoLock(key, entity);
            }
        }
    }

    protected void attachEntity(T t) {
    }

    public void updateInTx(Iterable<T> entities) {
        SQLiteStatement stmt = this.statements.getUpdateStatement();
        this.db.beginTransaction();
        RuntimeException txEx = null;
        try {
            synchronized (stmt) {
                if (this.identityScope != null) {
                    this.identityScope.lock();
                }
                try {
                    for (T entity : entities) {
                        updateInsideSynchronized(entity, stmt, false);
                    }
                    if (this.identityScope != null) {
                        this.identityScope.unlock();
                    }
                } catch (Throwable th) {
                    if (this.identityScope != null) {
                        this.identityScope.unlock();
                    }
                }
            }
            this.db.setTransactionSuccessful();
        } catch (RuntimeException e) {
            txEx = e;
        } finally {
            try {
                this.db.endTransaction();
            } catch (RuntimeException e2) {
                if (txEx != null) {
                    DaoLog.m29w("Could not end transaction (rethrowing initial exception)", e2);
                    throw txEx;
                }
                throw e2;
            }
        }
    }

    public void updateInTx(T... entities) {
        updateInTx(Arrays.asList(entities));
    }

    protected void assertSinglePk() {
        if (this.config.pkColumns.length != 1) {
            throw new DaoException(this + " (" + this.config.tablename + ") does not have a single-column primary key");
        }
    }

    public long count() {
        return DatabaseUtils.queryNumEntries(this.db, new StringBuilder(String.valueOf('\'')).append(this.config.tablename).append('\'').toString());
    }

    protected K getKeyVerified(T entity) {
        K key = getKey(entity);
        if (key != null) {
            return key;
        }
        if (entity == null) {
            throw new NullPointerException("Entity may not be null");
        }
        throw new DaoException("Entity has no key");
    }

    public SQLiteDatabase getDatabase() {
        return this.db;
    }
}
