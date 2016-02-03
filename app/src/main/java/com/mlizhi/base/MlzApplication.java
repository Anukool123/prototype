package com.mlizhi.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.mlizhi.base.imageloader.cache.disc.naming.Md5FileNameGenerator;
import com.mlizhi.base.imageloader.core.ImageLoader;
import com.mlizhi.base.imageloader.core.ImageLoaderConfiguration.Builder;
import com.mlizhi.base.imageloader.core.assist.QueueProcessingType;
import com.mlizhi.base.imageloader.core.download.BaseImageDownloader;
import com.mlizhi.modules.spec.dao.DaoMaster;
import com.mlizhi.modules.spec.dao.DaoMaster.DevOpenHelper;
import com.mlizhi.modules.spec.dao.DaoSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MlzApplication extends Application {
    private List<Activity> activities;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SQLiteDatabase db;
    private Set<AppStateListener> mAppStateListeners;
    private ConcurrentHashMap<String, String> mGlobalVariables;

    public interface AppStateListener {
        void onStateChanged(String str, String str2);
    }

    public MlzApplication() {
        this.activities = new ArrayList();
    }

    public void onCreate() {
        super.onCreate();
        this.mGlobalVariables = new ConcurrentHashMap();
        this.mAppStateListeners = Collections.synchronizedSet(new HashSet());
        initImageLoader(getApplicationContext());
        this.db = new DevOpenHelper(this, "mlizhi-bbc-db", null).getWritableDatabase();
        this.daoMaster = new DaoMaster(this.db);
        this.daoSession = this.daoMaster.newSession();
    }

    public void addActivity(Activity activity) {
        this.activities.add(activity);
    }

    public String getGlobalVariable(String key) {
        return (String) this.mGlobalVariables.get(key);
    }

    public String removeGlobalVariable(String key) {
        String value = (String) this.mGlobalVariables.remove(key);
        notifyListeners(key, null);
        return value;
    }

    public void putGlobalVariable(String key, String value) {
        this.mGlobalVariables.put(key, value);
        notifyListeners(key, value);
    }

    public void addAppStateListener(AppStateListener appStateListener) {
        this.mAppStateListeners.add(appStateListener);
    }

    public void removeAppStateListener(AppStateListener appStateListener) {
        this.mAppStateListeners.remove(appStateListener);
    }

    private void notifyListeners(String key, String value) {
        for (AppStateListener appStateListener : this.mAppStateListeners) {
            appStateListener.onStateChanged(key, value);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
    }

    public static void initImageLoader(Context context) {
        ImageLoader.getInstance().init(new Builder(context).threadPriority(3).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).imageDownloader(new BaseImageDownloader(context)).build());
    }

    public void onTerminate() {
        super.onTerminate();
        removeActivities();
    }

    public void removeActivities() {
        for (Activity activity : this.activities) {
            if (activity != null) {
                activity.finish();
            }
        }
    }

    public DaoSession getDaoSession() {
        return this.daoSession;
    }

}
