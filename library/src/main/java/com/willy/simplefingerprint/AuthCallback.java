package com.willy.simplefingerprint;

import android.hardware.fingerprint.FingerprintManager;

/**
 * Created by willy on 2017/6/1.
 */

public interface AuthCallback {

    void onNotInitialize();

    void onNoHardwareDetected();

    void onScreenLockNotSetUp();

    void onNoFingerprintRegistered();

    void onSuccess(FingerprintManager.CryptoObject cryptoObject);

    void onFailed(AuthFailure authFailure);
}
