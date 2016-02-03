package com.mlizhi.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.support.v4.media.TransportMediator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;

import com.tencent.connect.common.Constants;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Observable;

public class Session extends Observable {
    private static final String TAG = "Session";
    private static Session mInstance;
    private String account;
    private String appName;
    private String buildVersion;
    private String debugType;
    private String deviceId;
    private String imei;
    private boolean isAutoClearCache;
    public boolean isDebug;
    private boolean isDeviceBinded;
    private boolean isUpdateAvailable;
    private int lastVersion;
    private String loginType;
    private Context mContext;
    private SessionManager mSessionManager;
    private String macAddress;
    private String model;
    private int osVersion;
    private String packageName;
    private String password;
    private String screenSize;
    private String sim;
    private long splashId;
    private long splashTime;
    private TelephonyManager telMgr;
    private String uid;
    private long updataCheckTime;
    private long updateId;
    private int updateLevel;
    private String updateUri;
    private int updateVersionCode;
    private String updateVersionDesc;
    private String updateVersionName;
    private int upgradeNumber;
    private String userAddress;
    private String userBriday;
    private String userIcon;
    private String userName;
    private String userSex;
    private String userSkinType;
    private int versionCode;
    private String versionName;

    private Session(Context context) {
        this.telMgr = null;
        synchronized (this) {
            this.mContext = context;
            this.osVersion = VERSION.SDK_INT;
            this.buildVersion = VERSION.RELEASE;
            try {
                this.model = URLEncoder.encode(Build.MODEL, Hex.DEFAULT_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
            }
            readSettings();
        }
    }

    private void readSettings() {
        this.mSessionManager = SessionManager.get(this.mContext);
        addObserver(this.mSessionManager);
        HashMap<String, Object> preference = this.mSessionManager.readPreference();
        this.uid = (String) preference.get(SessionManager.P_USER_UID);
        this.userIcon = (String) preference.get(SessionManager.P_USER_USERICON);
        this.userName = (String) preference.get(SessionManager.P_USER_USERNAME);
        this.loginType = (String) preference.get(SessionManager.P_USER_LOGINTYPE);
        this.account = (String) preference.get(SessionManager.P_USER_ACCOUNT);
        this.password = (String) preference.get(SessionManager.P_USER_PASSWORD);
        this.screenSize = (String) preference.get(SessionManager.P_SCREEN_SIZE);
        this.isAutoClearCache = ((Boolean) preference.get(SessionManager.P_CLEAR_CACHE)).booleanValue();
        this.upgradeNumber = ((Integer) preference.get(SessionManager.P_UPGRADE_NUM)).intValue();
        this.updataCheckTime = ((Long) preference.get(SessionManager.P_PRODUCT_UPDATE_CHECK_TIMESTAMP)).longValue();
        this.updateId = ((Long) preference.get(SessionManager.P_UPDATE_ID)).longValue();
        this.lastVersion = ((Integer) preference.get(SessionManager.P_CURRENT_VERSION)).intValue();
        this.isUpdateAvailable = ((Boolean) preference.get(SessionManager.P_UPDATE_AVAILABIE)).booleanValue();
        this.updateVersionName = (String) preference.get(SessionManager.P_UPDATE_VERSION_NAME);
        this.updateVersionCode = ((Integer) preference.get(SessionManager.P_UPDATE_VERSION_CODE)).intValue();
        this.updateVersionDesc = (String) preference.get(SessionManager.P_UPDATE_DESC);
        this.updateUri = (String) preference.get(SessionManager.P_UPDATE_URI);
        this.updateLevel = ((Integer) preference.get(SessionManager.P_UPDATE_LEVEL)).intValue();
        this.splashId = ((Long) preference.get(SessionManager.P_SPLASH_ID)).longValue();
        this.splashTime = ((Long) preference.get(SessionManager.P_SPLASH_TIME)).longValue();
        this.userSex = (String) preference.get(SessionManager.P_USER_SEX);
        this.userBriday = (String) preference.get(SessionManager.P_USER_BRIDAY);
        this.userSkinType = (String) preference.get(SessionManager.P_USER_SKIN_TYPE);
        getApplicationInfo();
    }

    public static Session get(Context context) {
        if (mInstance == null) {
            mInstance = new Session(context);
        }
        return mInstance;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_USER_UID, uid));
    }

    public String getScreenSize() {
        return this.screenSize;
    }

    public void setScreenSize(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.screenSize = dm.widthPixels < dm.heightPixels ? dm.widthPixels + "#" + dm.heightPixels : dm.heightPixels + "#" + dm.widthPixels;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_SCREEN_SIZE, this.screenSize));
    }

    public int getOsVersion() {
        return this.osVersion;
    }

    private void getApplicationInfo() {
        PackageManager pm = this.mContext.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(this.mContext.getPackageName(), 0);
            this.versionName = pi.versionName;
            this.versionCode = pi.versionCode;
            ApplicationInfo ai = pm.getApplicationInfo(this.mContext.getPackageName(), TransportMediator.FLAG_KEY_MEDIA_NEXT);
            this.debugType = ai.metaData.get("mlizhi_debug").toString();
            if (Constants.VIA_TO_TYPE_QQ_GROUP.equals(this.debugType)) {
                this.isDebug = true;
            } else if (Constants.VIA_RESULT_SUCCESS.equals(this.debugType)) {
                this.isDebug = false;
            }
            Utils.sDebug = this.isDebug;
            this.appName = String.valueOf(ai.loadLabel(pm));
            Utils.sLogTag = this.appName;
            this.packageName = this.mContext.getPackageName();
            this.telMgr = (TelephonyManager) this.mContext.getSystemService(com.mlizhi.utils.Constants.LOGIN_TYPE_PHONE);
            this.imei = this.telMgr.getDeviceId();
            this.sim = this.telMgr.getSimSerialNumber();
        } catch (NameNotFoundException e) {
            Log.e(TAG, "met some error when get application info");
        }
    }

    public String getVersionName() {
        if (TextUtils.isEmpty(this.versionName)) {
            getApplicationInfo();
        }
        return this.versionName;
    }

    public int getVersionCode() {
        if (this.versionCode <= 0) {
            getApplicationInfo();
        }
        return this.versionCode;
    }

    public String getIMEI() {
        if (TextUtils.isEmpty(this.imei)) {
            getApplicationInfo();
        }
        return this.imei;
    }

    public String getPackageName() {
        if (TextUtils.isEmpty(this.packageName)) {
            getApplicationInfo();
        }
        return this.packageName;
    }

    public String getSim() {
        if (TextUtils.isEmpty(this.sim)) {
            getApplicationInfo();
        }
        return this.sim;
    }

    public String getMac() {
        if (TextUtils.isEmpty(this.macAddress)) {
            this.macAddress = ((WifiManager) this.mContext.getSystemService("wifi")).getConnectionInfo().getMacAddress();
        }
        return this.macAddress;
    }

    public String getUserIcon() {
        return this.userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_USER_USERICON, userIcon));
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_USER_USERNAME, userName));
    }

    public String getLoginType() {
        return this.loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_USER_LOGINTYPE, loginType));
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_USER_ACCOUNT, account));
    }

    public String getUserSex() {
        return this.userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_USER_SEX, userSex));
    }

    public String getUserBriday() {
        return this.userBriday;
    }

    public void setUserBriday(String userBriday) {
        this.userBriday = userBriday;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_USER_BRIDAY, userBriday));
    }

    public String getUserSkinType() {
        return this.userSkinType;
    }

    public void setUserSkinType(String userSkinType) {
        this.userSkinType = userSkinType;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_USER_SKIN_TYPE, userSkinType));
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_USER_PASSWORD, password));
    }

    public String getAppName() {
        return this.appName;
    }

    public boolean isUpdateAvailable() {
        return this.isUpdateAvailable;
    }

    public void setUpdateAvailable(boolean flag) {
        this.isUpdateAvailable = flag;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_UPDATE_AVAILABIE, Boolean.valueOf(flag)));
    }

    public String getUpdateVersionName() {
        return this.updateVersionName;
    }

    public int getUpdateVersionCode() {
        return this.updateVersionCode;
    }

    public String getUpdateVersionDesc() {
        return this.updateVersionDesc;
    }

    public String getUpdateUri() {
        return this.updateUri;
    }

    public int getUpdateLevel() {
        return this.updateLevel;
    }

    public void setUpdateInfo(String versionName, int versionCode, String description, String url, int level) {
        this.isUpdateAvailable = true;
        this.updateVersionName = versionName;
        this.updateVersionCode = versionCode;
        this.updateVersionDesc = description;
        this.updateUri = url;
        this.updateLevel = level;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_UPDATE_AVAILABIE, Boolean.valueOf(true)));
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_UPDATE_VERSION_CODE, Integer.valueOf(versionCode)));
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_UPDATE_DESC, description));
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_UPDATE_URI, url));
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_UPDATE_VERSION_NAME, versionName));
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_UPDATE_LEVEL, Integer.valueOf(level)));
    }

    public long getUpdateId() {
        return this.updateId;
    }

    public void setUpdateID(long updateId) {
        if (this.updateId != updateId) {
            this.updateId = updateId;
            super.setChanged();
            super.notifyObservers(new Pair(SessionManager.P_UPDATE_ID, Long.valueOf(updateId)));
        }
    }

    public boolean isAutoClearCache() {
        return this.isAutoClearCache;
    }

    public String getModel() {
        return this.model;
    }

    public String getBuildVersion() {
        return this.buildVersion;
    }

    public boolean isDeviceBinded() {
        return this.isDeviceBinded;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public int getUpgradeNumber() {
        return this.upgradeNumber;
    }

    public void setUpgradeNumber(int upgradeNumber) {
        if (this.upgradeNumber != upgradeNumber) {
            this.upgradeNumber = upgradeNumber;
            super.setChanged();
            super.notifyObservers(new Pair(SessionManager.P_UPGRADE_NUM, Integer.valueOf(upgradeNumber)));
        }
    }

    public long getUpdataCheckTime() {
        return this.updataCheckTime;
    }

    public void setUpdataCheckTime(long updataCheckTime) {
        if (this.updataCheckTime != updataCheckTime) {
            this.updataCheckTime = updataCheckTime;
            super.setChanged();
            super.notifyObservers(new Pair(SessionManager.P_PRODUCT_UPDATE_CHECK_TIMESTAMP, Long.valueOf(updataCheckTime)));
        }
    }

    public String getDebugType() {
        return this.debugType;
    }

    public void close() {
        this.mSessionManager.writePreferenceQuickly();
        mInstance = null;
    }

    public long getSplashTime() {
        return this.splashTime;
    }

    public void setSplashTime(long splashTime) {
        this.splashTime = splashTime;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_SPLASH_TIME, Long.valueOf(splashTime)));
    }

    public int getLastVersion() {
        return this.lastVersion;
    }

    public void setLastVersion(int currentVersion) {
        if (currentVersion != this.lastVersion) {
            clearData();
            this.lastVersion = currentVersion;
            super.setChanged();
            super.notifyObservers(new Pair(SessionManager.P_CURRENT_VERSION, Integer.valueOf(currentVersion)));
        }
    }

    public void clearData() {
        PreferenceManager.getDefaultSharedPreferences(this.mContext).edit().clear().commit();
    }

    public long getSplashId() {
        return this.splashId;
    }

    public void setSplashId(long splashId) {
        this.splashId = splashId;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_SPLASH_ID, Long.valueOf(splashId)));
    }

    public String getUserAddress() {
        return this.userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
        super.setChanged();
        super.notifyObservers(new Pair(SessionManager.P_USER_ADDRESS, userAddress));
    }
}
