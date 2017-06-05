package com.willy.simplefingerprint;

/**
 * Created by willy on 2017/6/1.
 */
public enum AuthError {
    /**
     * This happens if the lock screen has been disabled or or a fingerprint got
     * enrolled. Thus show the dialog to authenticate with their password first
     * and ask the user if they want to authenticate with fingerprints in the future
     */
    CIPHER_INIT_ERROR(1002, "The lock screen has been disabled or or a fingerprint got enrolled."),

    AUTHENTICATE_FAILED(1003, "A fingerprint was read successfully, but that fingerprint was not registered on the device.");

    private int mErrorCode;
    private String mErrorMessage;

    AuthError(int errorCode, String errorMessage) {
        mErrorCode = errorCode;
        mErrorMessage = errorMessage;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
