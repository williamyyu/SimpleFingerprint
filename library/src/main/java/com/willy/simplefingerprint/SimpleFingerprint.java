package com.willy.simplefingerprint;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import static com.willy.simplefingerprint.AuthError.CIPHER_INIT_ERROR;

/**
 * Created by willy on 2017/6/1.
 */

public class SimpleFingerprint {

    static final String TAG = "SimpleFingerprint";

    private static FingerprintHelper sFingerprintHelper;

    public static void init(@NonNull Context context) {
        sFingerprintHelper = new FingerprintHelper(context);
    }

    public static boolean authenticate(AuthCallback authCallback) {
        if (sFingerprintHelper == null) {
            Log.d(TAG, "onNotInitialize");
            authCallback.onNotInitialize();
            return false;
        }
        if (!sFingerprintHelper.isHardwareDetected()) {
            Log.d(TAG, "onNoHardwareDetected");
            authCallback.onNoHardwareDetected();
            return false;
        }

        if (!sFingerprintHelper.isKeyguardSecure()) {
            Log.d(TAG, "onScreenLockNotSetUp");
            authCallback.onScreenLockNotSetUp();
            return false;
        }

        if (!sFingerprintHelper.hasEnrolledFingerprints()) {
            Log.d(TAG, "onNoFingerprintRegistered");
            authCallback.onNoFingerprintRegistered();
            return false;
        }

        if (!sFingerprintHelper.initCipher()) {
            Log.d(TAG, "Cipher init error");
            authCallback.onFailed(CIPHER_INIT_ERROR.getErrorCode(), CIPHER_INIT_ERROR.getErrorMessage());
            return false;
        }

        sFingerprintHelper.startAuth(authCallback);

        return true;
    }

    public static void stopAuthenticate() {
        if (sFingerprintHelper != null) {
            sFingerprintHelper.stopAuth();
        }
    }
}
