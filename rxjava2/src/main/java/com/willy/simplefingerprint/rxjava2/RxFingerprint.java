package com.willy.simplefingerprint.rxjava2;

import android.hardware.fingerprint.FingerprintManager;

import com.willy.simplefingerprint.AuthCallback;
import com.willy.simplefingerprint.LogUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static com.willy.simplefingerprint.SimpleFingerprint.sFingerprintHelper;

/**
 * Created by willy on 2017/6/2.
 */

public class RxFingerprint {

    public static Observable<String> authenticate() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                if (sFingerprintHelper == null) {
                    LogUtils.log("onNotInitialize");
                    emitter.onNext("onNotInitialize");
                    emitter.onComplete();
                }
                if (!sFingerprintHelper.isHardwareDetected()) {
                    LogUtils.log("onNoHardwareDetected");
                    emitter.onNext("onNoHardwareDetected");
                    emitter.onComplete();
                }

                if (!sFingerprintHelper.isKeyguardSecure()) {
                    LogUtils.log("onScreenLockNotSetUp");
                    emitter.onNext("onScreenLockNotSetUp");
                    emitter.onComplete();
                }

                if (!sFingerprintHelper.hasEnrolledFingerprints()) {
                    LogUtils.log("onNoFingerprintRegistered");
                    emitter.onNext("onNoFingerprintRegistered");
                    emitter.onComplete();
                }

                if (!sFingerprintHelper.initCipher()) {
                    LogUtils.log("Cipher init error");
                    emitter.onNext("Cipher init error");
                    emitter.onComplete();
                }

                sFingerprintHelper.startAuth(new AuthCallback() {
                    @Override
                    public void onNotInitialize() {
                        emitter.onNext("onNotInitialize");
                        emitter.onComplete();
                    }

                    @Override
                    public void onNoHardwareDetected() {
                        emitter.onNext("onNoHardwareDetected");
                        emitter.onComplete();
                    }

                    @Override
                    public void onScreenLockNotSetUp() {
                        emitter.onNext("onScreenLockNotSetUp");
                        emitter.onComplete();
                    }

                    @Override
                    public void onNoFingerprintRegistered() {
                        emitter.onNext("onNoFingerprintRegistered");
                        emitter.onComplete();
                    }

                    @Override
                    public void onSuccess(FingerprintManager.CryptoObject cryptoObject) {
                        emitter.onNext("onSuccess");
                        emitter.onComplete();
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        emitter.onNext("onFailed:" + errorMessage);
//                        emitter.onComplete();
                    }
                });
            }
        });
    }
}
