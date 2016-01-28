package p016u.aly;

import android.util.Log;

/* compiled from: Log */
/* renamed from: u.aly.br */
public class br {
    public static boolean f892a;

    static {
        f892a = false;
    }

    public static void m1017a(String str, String str2) {
        if (f892a) {
            Log.i(str, str2);
        }
    }

    public static void m1018a(String str, String str2, Exception exception) {
        if (f892a) {
            Log.i(str, exception.toString() + ":  [" + str2 + "]");
        }
    }

    public static void m1019b(String str, String str2) {
        if (f892a) {
            Log.e(str, str2);
        }
    }

    public static void m1020b(String str, String str2, Exception exception) {
        if (f892a) {
            Log.e(str, exception.toString() + ":  [" + str2 + "]");
            for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
                Log.e(str, "        at\t " + stackTraceElement.toString());
            }
        }
    }

    public static void m1021c(String str, String str2) {
        if (f892a) {
            Log.d(str, str2);
        }
    }

    public static void m1022c(String str, String str2, Exception exception) {
        if (f892a) {
            Log.d(str, exception.toString() + ":  [" + str2 + "]");
        }
    }

    public static void m1023d(String str, String str2) {
        if (f892a) {
            Log.v(str, str2);
        }
    }

    public static void m1024d(String str, String str2, Exception exception) {
        if (f892a) {
            Log.v(str, exception.toString() + ":  [" + str2 + "]");
        }
    }

    public static void m1025e(String str, String str2) {
        if (f892a) {
            Log.w(str, str2);
        }
    }

    public static void m1026e(String str, String str2, Exception exception) {
        if (f892a) {
            Log.w(str, exception.toString() + ":  [" + str2 + "]");
            for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
                Log.w(str, "        at\t " + stackTraceElement.toString());
            }
        }
    }
}
