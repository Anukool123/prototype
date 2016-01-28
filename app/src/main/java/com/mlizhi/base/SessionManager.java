package com.mlizhi.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Pair;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import p016u.aly.bq;

public class SessionManager implements Observer {
    public static final String P_CLEAR_CACHE = "auto_clear_cache";
    public static final String P_CURRENT_VERSION = "pref.current.version";
    public static final String P_OS_VERSION = "pref.os.version";
    public static final String P_PRODUCT_UPDATE_CHECK_TIMESTAMP = "pref.product.update.timestamp";
    public static final String P_SCREEN_SIZE = "pref.screen.size";
    public static final String P_SPLASH_ID = "pref.splash.id";
    public static final String P_SPLASH_TIME = "pref.splash.time";
    public static final String P_UPDATE_AVAILABIE = "pref.update.available";
    public static final String P_UPDATE_DESC = "pref.update.desc";
    public static final String P_UPDATE_ID = "pref.update.id";
    public static final String P_UPDATE_LEVEL = "pref.update.level";
    public static final String P_UPDATE_URI = "pref.update.uri";
    public static final String P_UPDATE_VERSION_CODE = "pref.update.version.code";
    public static final String P_UPDATE_VERSION_NAME = "pref.update.version.name";
    public static final String P_UPGRADE_NUM = "pref.upgrade.num";
    public static final String P_USER_ACCOUNT = "pref.user.account";
    public static final String P_USER_ADDRESS = "pref.user.address";
    public static final String P_USER_BRIDAY = "pref.user.briday";
    public static final String P_USER_COOKIES = "pref.cookies";
    public static final String P_USER_LOGINTYPE = "pref.user.loginType";
    public static final String P_USER_PASSWORD = "pref.user.password";
    public static final String P_USER_SEX = "pref.user.sex";
    public static final String P_USER_SKIN_TYPE = "pref.user.skinType";
    public static final String P_USER_UID = "pref.user.uid";
    public static final String P_USER_USERICON = "pref.user.usericon";
    public static final String P_USER_USERNAME = "pref.user.username";
    private static SessionManager mInstance;
    private static final Method sApplyMethod;
    private Context mContext;
    private Thread mCurrentUpdateThread;
    private SharedPreferences mPreference;
    private LinkedList<Pair<String, Object>> mUpdateQueue;

    /* renamed from: com.mlizhi.base.SessionManager.1 */
    class C01121 extends Thread {
        C01121() {
        }

        public void run() {
            try {
                C01121.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SessionManager.this.writePreference();
        }
    }

    /* renamed from: com.mlizhi.base.SessionManager.2 */
    class C01132 extends Thread {
        C01132() {
        }

        public void run() {
            SessionManager.this.writePreference();
        }
    }

    private SessionManager(Context context) {
        this.mUpdateQueue = new LinkedList();
        synchronized (this) {
            this.mContext = context;
            if (this.mPreference == null) {
                this.mPreference = PreferenceManager.getDefaultSharedPreferences(this.mContext);
            }
        }
    }

    public static SessionManager get(Context context) {
        if (mInstance == null) {
            mInstance = new SessionManager(context);
        }
        return mInstance;
    }

    static {
        sApplyMethod = findApplyMethod();
    }

    private static Method findApplyMethod() {
        try {
            return Editor.class.getMethod("apply", new Class[0]);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static void apply(Editor editor) {
        if (sApplyMethod != null) {
            try {
                sApplyMethod.invoke(editor, new Object[0]);
                return;
            } catch (InvocationTargetException e) {
            } catch (IllegalAccessException e2) {
            }
        }
        editor.commit();
    }

    public void close() {
        this.mPreference = null;
        mInstance = null;
    }

    private boolean isPreferenceNull() {
        if (this.mPreference == null) {
            return true;
        }
        return false;
    }

    public void update(Observable observable, Object data) {
        if (data instanceof Pair) {
            synchronized (this.mUpdateQueue) {
                if (data != null) {
                    this.mUpdateQueue.add((Pair) data);
                }
            }
            writePreferenceSlowly();
        }
    }

    private void writePreferenceSlowly() {
        if (this.mCurrentUpdateThread == null || !this.mCurrentUpdateThread.isAlive()) {
            this.mCurrentUpdateThread = new C01121();
            this.mCurrentUpdateThread.setPriority(10);
            this.mCurrentUpdateThread.start();
        }
    }

    public void writePreferenceQuickly() {
        this.mCurrentUpdateThread = new C01132();
        this.mCurrentUpdateThread.setPriority(10);
        this.mCurrentUpdateThread.start();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void writePreference() {
        /*
        r7 = this;
        r3 = r7.mPreference;
        r0 = r3.edit();
        r4 = r7.mUpdateQueue;
        monitor-enter(r4);
    L_0x0009:
        r3 = r7.mUpdateQueue;	 Catch:{ all -> 0x0050 }
        r3 = r3.isEmpty();	 Catch:{ all -> 0x0050 }
        if (r3 == 0) goto L_0x0016;
    L_0x0011:
        monitor-exit(r4);	 Catch:{ all -> 0x0050 }
        apply(r0);
        return;
    L_0x0016:
        r3 = r7.mUpdateQueue;	 Catch:{ all -> 0x0050 }
        r2 = r3.remove();	 Catch:{ all -> 0x0050 }
        r2 = (android.util.Pair) r2;	 Catch:{ all -> 0x0050 }
        r1 = r2.first;	 Catch:{ all -> 0x0050 }
        r1 = (java.lang.String) r1;	 Catch:{ all -> 0x0050 }
        r3 = "pref.user.uid";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x0042;
    L_0x002a:
        r3 = "pref.user.account";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x0042;
    L_0x0032:
        r3 = "pref.user.username";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x0042;
    L_0x003a:
        r3 = "pref.user.password";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 == 0) goto L_0x0053;
    L_0x0042:
        r3 = r2.second;	 Catch:{ all -> 0x0050 }
        r3 = java.lang.String.valueOf(r3);	 Catch:{ all -> 0x0050 }
        r3 = com.mlizhi.base.SecurityUtil.encrypt(r3);	 Catch:{ all -> 0x0050 }
        r0.putString(r1, r3);	 Catch:{ all -> 0x0050 }
        goto L_0x0009;
    L_0x0050:
        r3 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x0050 }
        throw r3;
    L_0x0053:
        r3 = "pref.update.available";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 == 0) goto L_0x0067;
    L_0x005b:
        r3 = r2.second;	 Catch:{ all -> 0x0050 }
        r3 = (java.lang.Boolean) r3;	 Catch:{ all -> 0x0050 }
        r3 = r3.booleanValue();	 Catch:{ all -> 0x0050 }
        r0.putBoolean(r1, r3);	 Catch:{ all -> 0x0050 }
        goto L_0x0009;
    L_0x0067:
        r3 = "pref.screen.size";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x00bf;
    L_0x006f:
        r3 = "pref.os.version";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x00bf;
    L_0x0077:
        r3 = "pref.update.desc";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x00bf;
    L_0x007f:
        r3 = "pref.update.uri";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x00bf;
    L_0x0087:
        r3 = "pref.user.usericon";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x00bf;
    L_0x008f:
        r3 = "pref.update.version.name";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x00bf;
    L_0x0097:
        r3 = "pref.user.sex";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x00bf;
    L_0x009f:
        r3 = "pref.user.briday";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x00bf;
    L_0x00a7:
        r3 = "pref.user.skinType";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x00bf;
    L_0x00af:
        r3 = "pref.user.address";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x00bf;
    L_0x00b7:
        r3 = "pref.user.loginType";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 == 0) goto L_0x00c8;
    L_0x00bf:
        r3 = r2.second;	 Catch:{ all -> 0x0050 }
        r3 = (java.lang.String) r3;	 Catch:{ all -> 0x0050 }
        r0.putString(r1, r3);	 Catch:{ all -> 0x0050 }
        goto L_0x0009;
    L_0x00c8:
        r3 = "pref.update.version.code";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x00e8;
    L_0x00d0:
        r3 = "pref.update.level";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x00e8;
    L_0x00d8:
        r3 = "pref.upgrade.num";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x00e8;
    L_0x00e0:
        r3 = "pref.current.version";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 == 0) goto L_0x00f5;
    L_0x00e8:
        r3 = r2.second;	 Catch:{ all -> 0x0050 }
        r3 = (java.lang.Integer) r3;	 Catch:{ all -> 0x0050 }
        r3 = r3.intValue();	 Catch:{ all -> 0x0050 }
        r0.putInt(r1, r3);	 Catch:{ all -> 0x0050 }
        goto L_0x0009;
    L_0x00f5:
        r3 = "pref.product.update.timestamp";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x0115;
    L_0x00fd:
        r3 = "pref.splash.time";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x0115;
    L_0x0105:
        r3 = "pref.splash.id";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 != 0) goto L_0x0115;
    L_0x010d:
        r3 = "pref.update.id";
        r3 = r3.equals(r1);	 Catch:{ all -> 0x0050 }
        if (r3 == 0) goto L_0x0009;
    L_0x0115:
        r3 = r2.second;	 Catch:{ all -> 0x0050 }
        r3 = (java.lang.Long) r3;	 Catch:{ all -> 0x0050 }
        r5 = r3.longValue();	 Catch:{ all -> 0x0050 }
        r0.putLong(r1, r5);	 Catch:{ all -> 0x0050 }
        goto L_0x0009;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mlizhi.base.SessionManager.writePreference():void");
    }

    public HashMap<String, Object> readPreference() {
        if (isPreferenceNull()) {
            return null;
        }
        String uid;
        HashMap<String, Object> data = new HashMap();
        String uidString = this.mPreference.getString(P_USER_UID, null);
        if (uidString == null) {
            uid = bq.f888b;
        } else {
            uid = SecurityUtil.decrypt(uidString);
        }
        data.put(P_USER_UID, uid);
        data.put(P_SCREEN_SIZE, this.mPreference.getString(P_SCREEN_SIZE, "320#480"));
        data.put(P_OS_VERSION, Integer.valueOf(this.mPreference.getInt(P_OS_VERSION, 0)));
        String username = this.mPreference.getString(P_USER_USERNAME, bq.f888b);
        if (username == null) {
            username = bq.f888b;
        } else {
            username = SecurityUtil.decrypt(username);
        }
        data.put(P_USER_USERNAME, username);
        data.put(P_USER_USERICON, this.mPreference.getString(P_USER_USERICON, bq.f888b));
        String account = this.mPreference.getString(P_USER_ACCOUNT, bq.f888b);
        if (account == null) {
            account = bq.f888b;
        } else {
            account = SecurityUtil.decrypt(account);
        }
        data.put(P_USER_ACCOUNT, account);
        String password = this.mPreference.getString(P_USER_PASSWORD, null);
        if (password == null) {
            password = bq.f888b;
        } else {
            password = SecurityUtil.decrypt(password);
        }
        data.put(P_USER_PASSWORD, password);
        data.put(P_CLEAR_CACHE, Boolean.valueOf(this.mPreference.getBoolean(P_CLEAR_CACHE, false)));
        data.put(P_UPDATE_AVAILABIE, Boolean.valueOf(this.mPreference.getBoolean(P_UPDATE_AVAILABIE, false)));
        data.put(P_UPDATE_VERSION_CODE, Integer.valueOf(this.mPreference.getInt(P_UPDATE_VERSION_CODE, -1)));
        data.put(P_UPDATE_LEVEL, Integer.valueOf(this.mPreference.getInt(P_UPDATE_LEVEL, -1)));
        data.put(P_UPGRADE_NUM, Integer.valueOf(this.mPreference.getInt(P_UPGRADE_NUM, 0)));
        data.put(P_PRODUCT_UPDATE_CHECK_TIMESTAMP, Long.valueOf(this.mPreference.getLong(P_PRODUCT_UPDATE_CHECK_TIMESTAMP, -1)));
        data.put(P_UPDATE_DESC, this.mPreference.getString(P_UPDATE_DESC, bq.f888b));
        data.put(P_UPDATE_URI, this.mPreference.getString(P_UPDATE_URI, bq.f888b));
        data.put(P_UPDATE_VERSION_NAME, this.mPreference.getString(P_UPDATE_VERSION_NAME, bq.f888b));
        data.put(P_UPDATE_ID, Long.valueOf(this.mPreference.getLong(P_UPDATE_ID, -1)));
        data.put(P_SPLASH_ID, Long.valueOf(this.mPreference.getLong(P_SPLASH_ID, -1)));
        data.put(P_SPLASH_TIME, Long.valueOf(this.mPreference.getLong(P_SPLASH_TIME, 0)));
        data.put(P_CURRENT_VERSION, Integer.valueOf(this.mPreference.getInt(P_CURRENT_VERSION, -1)));
        data.put(P_USER_SEX, this.mPreference.getString(P_USER_SEX, bq.f888b));
        data.put(P_USER_BRIDAY, this.mPreference.getString(P_USER_BRIDAY, bq.f888b));
        data.put(P_USER_ADDRESS, this.mPreference.getString(P_USER_ADDRESS, bq.f888b));
        data.put(P_USER_SKIN_TYPE, this.mPreference.getString(P_USER_SKIN_TYPE, bq.f888b));
        data.put(P_USER_LOGINTYPE, this.mPreference.getString(P_USER_LOGINTYPE, bq.f888b));
        return data;
    }
}
