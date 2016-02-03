package com.mlizhi.base.imageloader.utils;

import android.util.Log;
import com.mlizhi.base.imageloader.core.ImageLoader;

/* renamed from: com.mlizhi.base.imageloader.utils.L */
public final class C0126L {
    private static final String LOG_FORMAT = "%1$s\n%2$s";
    private static volatile boolean writeDebugLogs;
    private static volatile boolean writeLogs;

    static {
        writeDebugLogs = false;
        writeLogs = true;
    }

    private C0126L() {
    }

    @Deprecated
    public static void enableLogging() {
        C0126L.writeLogs(true);
    }

    @Deprecated
    public static void disableLogging() {
        C0126L.writeLogs(false);
    }

    public static void writeDebugLogs(boolean writeDebugLogs) {
        writeDebugLogs = writeDebugLogs;
    }

    public static void writeLogs(boolean writeLogs) {
        writeLogs = writeLogs;
    }

    public static void m33d(String message, Object... args) {
        if (writeDebugLogs) {
            C0126L.log(3, null, message, args);
        }
    }

    public static void m37i(String message, Object... args) {
        C0126L.log(4, null, message, args);
    }

    public static void m38w(String message, Object... args) {
        C0126L.log(5, null, message, args);
    }

    public static void m35e(Throwable ex) {
        C0126L.log(6, ex, null, new Object[0]);
    }

    public static void m34e(String message, Object... args) {
        C0126L.log(6, null, message, args);
    }

    public static void m36e(Throwable ex, String message, Object... args) {
        C0126L.log(6, ex, message, args);
    }

    private static void log(int priority, Throwable ex, String message, Object... args) {
        if (writeLogs) {
            String log;
            if (args.length > 0) {
                message = String.format(message, args);
            }
            if (ex == null) {
                log = message;
            } else {
                String logMessage;
                if (message == null) {
                    logMessage = ex.getMessage();
                } else {
                    logMessage = message;
                }
                String logBody = Log.getStackTraceString(ex);
                log = String.format(LOG_FORMAT, new Object[]{logMessage, logBody});
            }
            Log.println(priority, ImageLoader.TAG, log);
        }
    }
}
