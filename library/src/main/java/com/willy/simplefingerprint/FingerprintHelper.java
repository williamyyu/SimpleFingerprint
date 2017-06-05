package com.willy.simplefingerprint;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static com.willy.simplefingerprint.AuthError.AUTHENTICATE_FAILED;

/**
 * Created by willy on 2017/5/31.
 */

class FingerprintHelper extends FingerprintManager.AuthenticationCallback {

    private static final String KEY_NAME = "default_key";

    private KeyguardManager mKeyguardManager;
    private FingerprintManager mFingerprintManager;

    private KeyStore mKeyStore;

    private Cipher mCipher;
    private CancellationSignal mCancellationSignal;

    private AuthCallback mAuthCallback;

    private boolean isScanning = false;

    FingerprintHelper(Context context) {
        mKeyguardManager = context.getApplicationContext().getSystemService(KeyguardManager.class);
        mFingerprintManager = context.getApplicationContext().getSystemService(FingerprintManager.class);

        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            LogUtils.log("Failed to get an instance of KeyStore", e);
        }

        createKey(KEY_NAME, true);
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     *
     * @param keyName                          the name of the key to be created
     * @param invalidatedByBiometricEnrollment if {@code false} is passed, the created key will not
     *                                         be invalidated even if a new fingerprint is enrolled.
     *                                         The default value is {@code true}, so passing
     *                                         {@code true} doesn't change the behavior
     *                                         (the key will be invalidated if a new fingerprint is
     *                                         enrolled.). Note that this parameter is only valid if
     *                                         the app works on Android N developer preview.
     */
    private void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            if (mKeyStore.containsAlias(keyName)) {
                return;
            }

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                    keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            /*
                Sets whether this key should be invalidated on fingerprint enrollment.
                Only available in API level >= 24
                Default is true.
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }

            KeyGenerator keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException | KeyStoreException e) {
            LogUtils.log("Failed to createKey", e);
        } catch (NoSuchProviderException e) {
            LogUtils.log("Failed to get an instance of KeyGenerator", e);
        }
    }

    /**
     * Initialize the {@link Cipher} instance with the created key in the
     * {@link #createKey(String, boolean)} method.
     * <p>
     * keyName is the key name to init the cipher
     *
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    boolean initCipher() {
        mCipher = createCipher();
        if (mCipher == null) {
            return false;
        }

        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            LogUtils.log("Failed to init Cipher", e);
            return false;
        }
    }

    private Cipher createCipher() {
        try {
            return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            LogUtils.log("Failed to get an instance of Cipher", e);
            return null;
        }
    }

    void startAuth(AuthCallback authCallback) {
        if (isScanning) {
            return;
        }

        LogUtils.log("startAuth ...");

        isScanning = true;

        mAuthCallback = authCallback;
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
        }
        mCancellationSignal = new CancellationSignal();
        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(mCipher);

        //noinspection MissingPermission
        mFingerprintManager.authenticate(cryptoObject, mCancellationSignal, 0, this, null);
    }

    void stopAuth() {
        if (!isScanning) {
            return;
        }

        LogUtils.log("stopAuth ...");

        isScanning = false;

        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }

        if (mAuthCallback != null) {
            mAuthCallback = null;
        }
    }

    boolean isHardwareDetected() {
        // noinspection ResourceType
        return mFingerprintManager.isHardwareDetected();
    }

    boolean isKeyguardSecure() {
        return mKeyguardManager.isKeyguardSecure();
    }

    boolean hasEnrolledFingerprints() {
        // noinspection ResourceType
        return mFingerprintManager.hasEnrolledFingerprints();
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        LogUtils.log("onAuthenticationError: " + errString + "(" + errorCode + ")");
        if (mAuthCallback != null) {
            mAuthCallback.onFailed(errorCode, errString.toString());
            stopAuth();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        LogUtils.log("onAuthenticationHelp: " + helpString + "(" + helpCode + ")");
        if (mAuthCallback != null) {
            mAuthCallback.onFailed(helpCode, helpString.toString());
            stopAuth();
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        LogUtils.log("onAuthenticationSucceeded!");
        if (mAuthCallback != null) {
            mAuthCallback.onSuccess(result.getCryptoObject());
            stopAuth();
        }
    }

    /**
     * We have 5 times to retry,
     * after failed 5 times, will call {@link #onAuthenticationError(int, CharSequence)}
     * and disable authenticate for a while (depends on mobile).
     */
    @Override
    public void onAuthenticationFailed() {
        LogUtils.log("onAuthenticationFailed: A fingerprint was read successfully, but that fingerprint was not registered on the device.");
        if (mAuthCallback != null) {
            mAuthCallback.onFailed(AUTHENTICATE_FAILED.getErrorCode(), AUTHENTICATE_FAILED.getErrorMessage());
        }
    }
}
