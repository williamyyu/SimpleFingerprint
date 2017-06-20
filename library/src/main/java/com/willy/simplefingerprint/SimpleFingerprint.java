package com.willy.simplefingerprint;

import android.content.Context;
import android.support.annotation.NonNull;

import static com.willy.simplefingerprint.AuthFailure.CIPHER_INIT_ERROR;

/**
 * Created by willy on 2017/6/1.
 */

public class SimpleFingerprint {

    static final String TAG = "SimpleFingerprint";

    public static FingerprintHelper sFingerprintHelper;

    private static boolean useFingerprintInFuture = true;

    public static void init(@NonNull Context context) {
        init(context, false);
    }

    /**
     * @param context
     * @param isDebugMode To determine whether to print the log message.
     */
    public static void init(@NonNull Context context, boolean isDebugMode) {
        sFingerprintHelper = new FingerprintHelper(context);
        LogUtils.setIsDebugMode(isDebugMode);
    }

    public static boolean authenticate(AuthCallback authCallback) {
        if (sFingerprintHelper == null) {
            LogUtils.log("Need to call SimpleFingerprint.init() to initialize Library before you start authenticate.");
            authCallback.onNotInitialize();
            return false;
        }
        if (!sFingerprintHelper.isHardwareDetected()) {
            LogUtils.log("There is no hardware detected on this device.");
            authCallback.onNoHardwareDetected();
            return false;
        }

        if (!sFingerprintHelper.isKeyguardSecure()) {
            LogUtils.log("Need to set up keyguard secure.");
            authCallback.onScreenLockNotSetUp();
            return false;
        }

        if (!sFingerprintHelper.hasEnrolledFingerprints()) {
            LogUtils.log("There is no registered fingerprints.");
            authCallback.onNoFingerprintRegistered();
            return false;
        }

        if (!sFingerprintHelper.initCipher()) {
            // The lock screen has been disabled or or a fingerprint got enrolled.
            // so need to use password or other backup way to authenticate.
            LogUtils.log("Cipher init error");
            authCallback.onFailed(new AuthFailure(CIPHER_INIT_ERROR));

            // Need to ask user if want to user fingerprint to authenticate next time.
            if (useFingerprintInFuture) {
                // Re-create the key so that fingerprints including new ones are validated.
                sFingerprintHelper.createKey(true, true);
            }

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
