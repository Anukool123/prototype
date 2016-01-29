package com.mlizhi.modules.spec.dao;

import android.database.sqlite.SQLiteDatabase;
import com.mlizhi.base.dao.AbstractDao;
import com.mlizhi.base.dao.AbstractDaoSession;
import com.mlizhi.base.dao.identityscope.IdentityScopeType;
import com.mlizhi.base.dao.internal.DaoConfig;
import com.mlizhi.modules.spec.dao.model.ContentModel;
import com.mlizhi.modules.spec.dao.model.DetectModel;
import java.util.Map;

public class DaoSession extends AbstractDaoSession {
    private final ContentDao contentDao;
    private final DaoConfig contentDaoConfig;
    private final DetectDao detectDao;
    private final DaoConfig detectDaoConfig;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap) {
        super(db);
        this.detectDaoConfig = ((DaoConfig) daoConfigMap.get(DetectDao.class)).clone();
        this.detectDaoConfig.initIdentityScope(type);
        this.detectDao = new DetectDao(this.detectDaoConfig, this);
        registerDao(DetectModel.class, this.detectDao);
        this.contentDaoConfig = ((DaoConfig) daoConfigMap.get(ContentDao.class)).clone();
        this.contentDaoConfig.initIdentityScope(type);
        this.contentDao = new ContentDao(this.contentDaoConfig, this);
        registerDao(ContentModel.class, this.contentDao);
    }

    public void clear() {
        this.detectDaoConfig.getIdentityScope().clear();
        this.contentDaoConfig.getIdentityScope().clear();
    }

    public DetectDao getDetectDao() {
        return this.detectDao;
    }

    public ContentDao getContentDao() {
        return this.contentDao;
    }
}
