package com.loopj.android.http;

class AssertUtils {
    private AssertUtils() {
    }

    public static void asserts(boolean expression, String failedMessage) {
        if (!expression) {
            throw new AssertionError(failedMessage);
        }
    }
}
