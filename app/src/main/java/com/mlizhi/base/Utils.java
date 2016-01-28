package com.mlizhi.base;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import com.android.volley.DefaultRetryPolicy;
import com.umeng.analytics.AnalyticsConstants;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import p016u.aly.bq;

public class Utils {
    private static final String ENCODING_UTF8 = "UTF-8";
    public static boolean sDebug;
    public static String sLogTag;

    public static byte[] getUTF8Bytes(String string) {
        if (string == null) {
            return new byte[0];
        }
        try {
            return string.getBytes(ENCODING_UTF8);
        } catch (UnsupportedEncodingException e) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                dos.writeUTF(string);
                byte[] jdata = bos.toByteArray();
                bos.close();
                dos.close();
                byte[] buff = new byte[(jdata.length - 2)];
                System.arraycopy(jdata, 2, buff, 0, buff.length);
                return buff;
            } catch (IOException e2) {
                return new byte[0];
            }
        }
    }

    public static String getUTF8String(byte[] b) {
        if (b == null) {
            return bq.f888b;
        }
        return getUTF8String(b, 0, b.length);
    }

    public static String getUTF8String(byte[] b, int start, int length) {
        if (b == null) {
            return bq.f888b;
        }
        try {
            return new String(b, start, length, ENCODING_UTF8);
        } catch (UnsupportedEncodingException e) {
            return bq.f888b;
        }
    }

    public static int getInt(String value) {
        int i = 0;
        if (!TextUtils.isEmpty(value)) {
            try {
                i = Integer.parseInt(value.trim(), 10);
            } catch (NumberFormatException e) {
            }
        }
        return i;
    }

    public static float getFloat(String value) {
        float f = 0.0f;
        if (value != null) {
            try {
                f = Float.parseFloat(value.trim());
            } catch (NumberFormatException e) {
            }
        }
        return f;
    }

    public static long getLong(String value) {
        long j = 0;
        if (value != null) {
            try {
                j = Long.parseLong(value.trim());
            } catch (NumberFormatException e) {
            }
        }
        return j;
    }

    public static void m16V(String msg) {
        if (sDebug) {
            Log.v(sLogTag, msg);
        }
    }

    public static void m17V(String msg, Throwable e) {
        if (sDebug) {
            Log.v(sLogTag, msg, e);
        }
    }

    public static void m10D(String msg) {
        if (sDebug) {
            Log.d(sLogTag, msg);
        }
    }

    public static void m11D(String msg, Throwable e) {
        if (sDebug) {
            Log.d(sLogTag, msg, e);
        }
    }

    public static void m14I(String msg) {
        if (sDebug) {
            Log.i(sLogTag, msg);
        }
    }

    public static void m15I(String msg, Throwable e) {
        if (sDebug) {
            Log.i(sLogTag, msg, e);
        }
    }

    public static void m18W(String msg) {
        if (sDebug) {
            Log.w(sLogTag, msg);
        }
    }

    public static void m19W(String msg, Throwable e) {
        if (sDebug) {
            Log.w(sLogTag, msg, e);
        }
    }

    public static void m12E(String msg) {
        if (sDebug) {
            Log.e(sLogTag, msg);
        }
    }

    public static void m13E(String msg, Throwable e) {
        if (sDebug) {
            Log.e(sLogTag, msg, e);
        }
    }

    public static LayoutAnimationController getLayoutAnimation() {
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        animation.setDuration(50);
        set.addAnimation(animation);
        animation = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, -1.0f, 1, 0.0f);
        animation.setDuration(100);
        set.addAnimation(animation);
        return new LayoutAnimationController(set, 0.5f);
    }

    public static String getMD5(String text) {
        try {
            byte[] byteArray = text.getBytes("utf8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(byteArray, 0, byteArray.length);
            return convertToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return bq.f888b;
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
            return bq.f888b;
        }
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 15;
            int two_halfs = 0;
            while (true) {
                if (halfbyte < 0 || halfbyte > 9) {
                    buf.append((char) ((halfbyte - 10) + 97));
                } else {
                    buf.append((char) (halfbyte + 48));
                }
                halfbyte = data[i] & 15;
                int two_halfs2 = two_halfs + 1;
                if (two_halfs >= 1) {
                    break;
                }
                two_halfs = two_halfs2;
            }
        }
        return buf.toString();
    }

    public static boolean isSdcardReadable() {
        String state = Environment.getExternalStorageState();
        if ("mounted_ro".equals(state) || "mounted".equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isSdcardWritable() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    public static boolean isNeedCheckUpgrade(Context context) {
        if (System.currentTimeMillis() - Session.get(context).getUpdataCheckTime() > AnalyticsConstants.f551m) {
            return true;
        }
        return false;
    }

    public static void clearCache(Context context) {
        int length;
        int i = 0;
        File[] files = Environment.getDownloadCacheDirectory().listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
        files = context.getCacheDir().listFiles();
        if (files != null) {
            length = files.length;
            while (i < length) {
                files[i].delete();
                i++;
            }
        }
    }

    public static int dip2px(Context context, float dpValue) {
        return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        return (int) ((pxValue / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(Context context) {
        return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getHeight();
    }
}
