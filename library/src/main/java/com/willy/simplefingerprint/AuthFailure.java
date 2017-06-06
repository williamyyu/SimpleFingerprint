package com.willy.simplefingerprint;

/**
 * Created by willy on 2017/6/1.
 */
public class AuthFailure {

    /**
     * This happens if the lock screen has been disabled or or a fingerprint got
     * enrolled. Thus show the dialog to authenticate with their password first
     * and ask the user if they want to authenticate with fingerprints in the future
     */
    public static final int CIPHER_INIT_ERROR = 1001;
    public static final int AUTHENTICATE_FAILED = 1002;

    private int errorCode;
    private String errorMessage;

    AuthFailure(int errorCode, String errMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errMessage;
    }

    AuthFailure(int errorCode) {
        this.errorCode = errorCode;

        switch (errorCode) {
            case CIPHER_INIT_ERROR:
                this.errorMessage = "The lock screen has been disabled or or a fingerprint got enrolled.";
                break;
            case AUTHENTICATE_FAILED:
                this.errorMessage = "A fingerprint was read successfully, but that fingerprint was not registered on the device.";
                break;
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
