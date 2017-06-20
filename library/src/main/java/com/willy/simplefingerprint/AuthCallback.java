package com.willy.simplefingerprint;

import android.hardware.fingerprint.FingerprintManager;

/**
 * Created by willy on 2017/6/1.
 */

public abstract class AuthCallback {

    /**
     * Need to call SimpleFingerprint.init() to initialize Library before you start authenticate.
     */
    protected void onNotInitialize() {

    }

    /**
     * There is no fingerprint sensor hardware detected on this device.
     */
    protected void onNoHardwareDetected() {

    }

    /**
     * Need to set up keyguard secure.
     */
    protected void onScreenLockNotSetUp() {

    }

    /**
     * There is no registered fingerprints in this device.
     */
    protected void onNoFingerprintRegistered() {

    }

    /**
     * The operation was canceled because the API is locked out due to too many attempts.
     */
    protected void onLockOut() {

    }

    protected abstract void onSucceeded(FingerprintManager.CryptoObject cryptoObject);

    protected abstract void onFailed(AuthFailure authFailure);
}
