package com.mlizhi.base.dao;

import android.database.sqlite.SQLiteDatabase;
import com.mlizhi.base.dao.identityscope.IdentityScopeType;
import com.mlizhi.base.dao.internal.DaoConfig;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDaoMaster {
    protected final Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap;
    protected final SQLiteDatabase db;
    protected final int schemaVersion;

    public abstract AbstractDaoSession newSession();

    public abstract AbstractDaoSession newSession(IdentityScopeType identityScopeType);

    public AbstractDaoMaster(SQLiteDatabase db, int schemaVersion) {
        this.db = db;
        this.schemaVersion = schemaVersion;
        this.daoConfigMap = new HashMap();
    }

    protected void registerDaoClass(Class<? extends AbstractDao<?, ?>> daoClass) {
        this.daoConfigMap.put(daoClass, new DaoConfig(this.db, daoClass));
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    public SQLiteDatabase getDatabase() {
        return this.db;
    }
}
