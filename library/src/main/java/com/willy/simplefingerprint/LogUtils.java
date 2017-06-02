package com.willy.simplefingerprint;

import android.util.Log;

/**
 * Created by willy on 2017/6/1.
 */

public class LogUtils {

    private static boolean sIsDebugMode = false;

    public static void setIsDebugMode(boolean isDebugMode) {
        sIsDebugMode = isDebugMode;
    }

    public static void log(String message) {
        if (sIsDebugMode) {
            Log.d(SimpleFingerprint.TAG, message);
        }
    }

    public static void log(String message, Exception exception) {
        if (sIsDebugMode) {
            Log.e(SimpleFingerprint.TAG, message, exception);
        }
    }
}

