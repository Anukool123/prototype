package com.mlizhi.base.imageloader.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IoUtils {
    public static final int CONTINUE_LOADING_PERCENTAGE = 75;
    public static final int DEFAULT_BUFFER_SIZE = 32768;
    public static final int DEFAULT_IMAGE_TOTAL_SIZE = 512000;

    public interface CopyListener {
        boolean onBytesCopied(int i, int i2);
    }

    private IoUtils() {
    }

    public static boolean copyStream(InputStream is, OutputStream os, CopyListener listener) throws IOException {
        return copyStream(is, os, listener, DEFAULT_BUFFER_SIZE);
    }

    public static boolean copyStream(InputStream is, OutputStream os, CopyListener listener, int bufferSize) throws IOException {
        int current = 0;
        int total = is.available();
        if (total <= 0) {
            total = DEFAULT_IMAGE_TOTAL_SIZE;
        }
        byte[] bytes = new byte[bufferSize];
        if (shouldStopLoading(listener, 0, total)) {
            return false;
        }
        do {
            int count = is.read(bytes, 0, bufferSize);
            if (count == -1) {
                os.flush();
                return true;
            }
            os.write(bytes, 0, count);
            current += count;
        } while (!shouldStopLoading(listener, current, total));
        return false;
    }

    private static boolean shouldStopLoading(CopyListener listener, int current, int total) {
        if (listener == null || listener.onBytesCopied(current, total) || (current * 100) / total >= CONTINUE_LOADING_PERCENTAGE) {
            return false;
        }
        return true;
    }

    public static void readAndCloseStream(InputStream is) {
        while (true) {
            try {
                if (is.read(new byte[DEFAULT_BUFFER_SIZE], 0, DEFAULT_BUFFER_SIZE) == -1) {
                    break;
                }
            } catch (IOException e) {
            } finally {
                closeSilently(is);
            }
        }
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
            }
        }
    }
}
