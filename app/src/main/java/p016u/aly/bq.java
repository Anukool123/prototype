package p016u.aly;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.loopj.android.http.AsyncHttpClient;
import com.mlizhi.utils.Constants;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.microedition.khronos.opengles.GL10;

/* compiled from: DeviceConfig */
/* renamed from: u.aly.bq */
public class bq {
    protected static final String className;
    public static final String f888b = "";
    public static final String dataNetwork = "2G/3G";
    public static final String wifiNetwork = "Wi-Fi";
    public static final int f891e = 8;

    static {
        className = bq.class.getName();
    }

    public static boolean packageName(String str, Context context) {
        try {
            context.getPackageManager().getPackageInfo(str, 1);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isChineseLocale(Context context) {
        return context.getResources().getConfiguration().locale.toString().equals(Locale.CHINA.toString());
    }

    public static boolean isPortrait(Context context) {
        if (context.getResources().getConfiguration().orientation == 1) {
            return true;
        }
        return false;
    }

    public static String versionCode(Context context) {
        try {
            return String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
        } catch (NameNotFoundException e) {
            return f888b;
        }
    }

    public static String versionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return f888b;
        }
    }

    public static boolean isPermissionGranted(Context context, String str) {
        if (context.getPackageManager().checkPermission(str, context.getPackageName()) != 0) {
            return false;
        }
        return true;
    }

    public static String applicationInfo(Context context) {
        ApplicationInfo applicationInfo;
        PackageManager packageManager = context.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            applicationInfo = null;
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : f888b);
    }

    public static String[] gpuInformation(GL10 gl10) {
        try {
            String[] strArr = new String[2];
            String glGetString = gl10.glGetString(7936);
            String glGetString2 = gl10.glGetString(7937);
            strArr[0] = glGetString;
            strArr[1] = glGetString2;
            return strArr;
        } catch (Exception e) {
            br.m1020b(className, "Could not read gpu infor:", e);
            return new String[0];
        }
    }

    public static String cpuInformation() {
        String str = null;
        try {
            Reader fileReader = new FileReader("/proc/cpuinfo");
            if (fileReader != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(fileReader, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
                    str = bufferedReader.readLine();
                    bufferedReader.close();
                    fileReader.close();
                } catch (Exception e) {
                    br.m1020b(className, "Could not read from file /proc/cpuinfo", e);
                }
            }
        } catch (Exception e2) {
            br.m1020b(className, "Could not open file /proc/cpuinfo", e2);
        }
        if (str != null) {
            return str.substring(str.indexOf(58) + 1).trim();
        }
        return f888b;
    }

    public static String deviceId(Context context) {
        String deviceId;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Constants.LOGIN_TYPE_PHONE);
        if (telephonyManager == null) {
            br.m1025e(className, "No IMEI.");
        }
        String str = f888b;
        try {
            if (bq.isPermissionGranted(context, "android.permission.READ_PHONE_STATE")) {
                deviceId = telephonyManager.getDeviceId();
                if (TextUtils.isEmpty(deviceId)) {
                    return deviceId;
                }
                br.m1025e(className, "No IMEI.");
                deviceId = bq.m1008p(context);
                if (TextUtils.isEmpty(deviceId)) {
                    return deviceId;
                }
                br.m1025e(className, "Failed to take mac as IMEI. Try to use Secure.ANDROID_ID instead.");
                deviceId = Secure.getString(context.getContentResolver(), "android_id");
                br.m1017a(className, "getDeviceId: Secure.ANDROID_ID: " + deviceId);
                return deviceId;
            }
        } catch (Exception e) {
            br.m1026e(className, "No IMEI.", e);
        }
        deviceId = str;
        if (TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        br.m1025e(className, "No IMEI.");
        deviceId = bq.m1008p(context);
        if (TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        br.m1025e(className, "Failed to take mac as IMEI. Try to use Secure.ANDROID_ID instead.");
        deviceId = Secure.getString(context.getContentResolver(), "android_id");
        br.m1017a(className, "getDeviceId: Secure.ANDROID_ID: " + deviceId);
        return deviceId;
    }

    public static String m999g(Context context) {
        return cd.m1102b(bq.deviceId(context));
    }

    public static String m1000h(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Constants.LOGIN_TYPE_PHONE);
            if (telephonyManager == null) {
                return f888b;
            }
            return telephonyManager.getNetworkOperatorName();
        } catch (Exception e) {
            e.printStackTrace();
            return f888b;
        }
    }

    public static String height_width(Context context) {
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
            return new StringBuilder(String.valueOf(String.valueOf(displayMetrics.heightPixels))).append("*").append(String.valueOf(displayMetrics.widthPixels)).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return f888b;
        }
    }

    public static String[] m1002j(Context context) {
        String[] strArr = new String[]{f888b, f888b};
        try {
            if (context.getPackageManager().checkPermission("android.permission.ACCESS_NETWORK_STATE", context.getPackageName()) != 0) {
                strArr[0] = f888b;
                return strArr;
            }
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            if (connectivityManager == null) {
                strArr[0] = f888b;
                return strArr;
            } else if (connectivityManager.getNetworkInfo(1).getState() == State.CONNECTED) {
                strArr[0] = wifiNetwork;
                return strArr;
            } else {
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(0);
                if (networkInfo.getState() == State.CONNECTED) {
                    strArr[0] = dataNetwork;
                    strArr[1] = networkInfo.getSubtypeName();
                    return strArr;
                }
                return strArr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean m1003k(Context context) {
        return wifiNetwork.equals(bq.m1002j(context)[0]);
    }

    public static boolean m1004l(Context context) {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isConnectedOrConnecting();
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean m992b() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return true;
        }
        return false;
    }

    public static int m1005m(Context context) {
        try {
            Calendar instance = Calendar.getInstance(bq.m1016x(context));
            if (instance != null) {
                return instance.getTimeZone().getRawOffset() / 3600000;
            }
        } catch (Exception e) {
            br.m1018a(className, "error in getTimeZone", e);
        }
        return f891e;
    }

    public static String[] m1006n(Context context) {
        String[] strArr = new String[2];
        try {
            Locale x = bq.m1016x(context);
            if (x != null) {
                strArr[0] = x.getCountry();
                strArr[1] = x.getLanguage();
            }
            if (TextUtils.isEmpty(strArr[0])) {
                strArr[0] = "Unknown";
            }
            if (TextUtils.isEmpty(strArr[1])) {
                strArr[1] = "Unknown";
            }
        } catch (Exception e) {
            br.m1020b(className, "error in getLocaleInfo", e);
        }
        return strArr;
    }

    private static Locale m1016x(Context context) {
        Locale locale = null;
        try {
            Configuration configuration = new Configuration();
            configuration.setToDefaults();
            System.getConfiguration(context.getContentResolver(), configuration);
            if (configuration != null) {
                locale = configuration.locale;
            }
        } catch (Exception e) {
            br.m1019b(className, "fail to read user config locale");
        }
        if (locale == null) {
            return Locale.getDefault();
        }
        return locale;
    }

    public static String m1007o(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), TransportMediator.FLAG_KEY_MEDIA_NEXT);
            if (applicationInfo != null) {
                String string = applicationInfo.metaData.getString("UMENG_APPKEY");
                if (string != null) {
                    return string.trim();
                }
                br.m1019b(className, "Could not read UMENG_APPKEY meta-data from AndroidManifest.xml.");
            }
        } catch (Exception e) {
            br.m1020b(className, "Could not read UMENG_APPKEY meta-data from AndroidManifest.xml.", e);
        }
        return null;
    }

    public static String m1008p(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            if (bq.isPermissionGranted(context, "android.permission.ACCESS_WIFI_STATE")) {
                return wifiManager.getConnectionInfo().getMacAddress();
            }
            br.m1025e(className, "Could not get mac address.[no permission android.permission.ACCESS_WIFI_STATE");
            return f888b;
        } catch (Exception e) {
            br.m1025e(className, "Could not get mac address." + e.toString());
        }
    }

    public static String m1009q(Context context) {
        int[] r = bq.m1010r(context);
        if (r == null) {
            return "Unknown";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(r[0]);
        stringBuffer.append("*");
        stringBuffer.append(r[1]);
        return stringBuffer.toString();
    }

    public static int[] m1010r(Context context) {
        try {
            int a;
            int a2;
            int i;
            Object displayMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
            if ((context.getApplicationInfo().flags & AsyncHttpClient.DEFAULT_SOCKET_BUFFER_SIZE) == 0) {
                a = bq.m983a(displayMetrics, "noncompatWidthPixels");
                a2 = bq.m983a(displayMetrics, "noncompatHeightPixels");
            } else {
                a2 = -1;
                a = -1;
            }
            if (a == -1 || a2 == -1) {
                i = displayMetrics.widthPixels;
                a = displayMetrics.heightPixels;
            } else {
                i = a;
                a = a2;
            }
            int[] iArr = new int[2];
            if (i > a) {
                iArr[0] = a;
                iArr[1] = i;
                return iArr;
            }
            iArr[0] = i;
            iArr[1] = a;
            return iArr;
        } catch (Exception e) {
            br.m1020b(className, "read resolution fail", e);
            return null;
        }
    }

    private static int m983a(Object obj, String str) {
        try {
            Field declaredField = DisplayMetrics.class.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField.getInt(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String m1011s(Context context) {
        try {
            return ((TelephonyManager) context.getSystemService(Constants.LOGIN_TYPE_PHONE)).getNetworkOperatorName();
        } catch (Exception e) {
            br.m1018a(className, "read carrier fail", e);
            return "Unknown";
        }
    }

    public static String m986a(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
    }

    public static String m994c() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    public static Date m987a(String str) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static int m984a(Date date, Date date2) {
        if (!date.after(date2)) {
            Date date3 = date2;
            date2 = date;
            date = date3;
        }
        return (int) ((date.getTime() - date2.getTime()) / 1000);
    }

    public static String m1012t(Context context) {
        String str = "Unknown";
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), TransportMediator.FLAG_KEY_MEDIA_NEXT);
            if (!(applicationInfo == null || applicationInfo.metaData == null)) {
                Object obj = applicationInfo.metaData.get("UMENG_CHANNEL");
                if (obj != null) {
                    String obj2 = obj.toString();
                    if (obj2 != null) {
                        return obj2;
                    }
                    br.m1017a(className, "Could not read UMENG_CHANNEL meta-data from AndroidManifest.xml.");
                    return str;
                }
            }
        } catch (Exception e) {
            br.m1017a(className, "Could not read UMENG_CHANNEL meta-data from AndroidManifest.xml.");
            e.printStackTrace();
        }
        return str;
    }

    public static String m1013u(Context context) {
        return context.getPackageName();
    }

    public static String m1014v(Context context) {
        return context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
    }

    public static boolean m1015w(Context context) {
        try {
            return (context.getApplicationInfo().flags & 2) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}
