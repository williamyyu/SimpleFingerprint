package com.willy.example;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.willy.simplefingerprint.AuthCallback;
import com.willy.simplefingerprint.AuthFailure;
import com.willy.simplefingerprint.SimpleFingerprint;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tvStatus = (TextView) findViewById(R.id.tvStatus);
        Button btnStartAuth = (Button) findViewById(R.id.btnAuth);
        Button btnStop = (Button) findViewById(R.id.btnStop);

        btnStartAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStatus.setText("Start Auth ...");

                SimpleFingerprint.authenticate(new AuthCallback() {
                    @Override
                    public void onNotInitialize() {
                        tvStatus.setText("onNotInitialize");
                    }

                    @Override
                    public void onNoHardwareDetected() {
                        tvStatus.setText("onNoHardwareDetected");
                    }

                    @Override
                    public void onScreenLockNotSetUp() {
                        tvStatus.setText("onScreenLockNotSetUp");
                    }

                    @Override
                    public void onNoFingerprintRegistered() {
                        tvStatus.setText("onNoFingerprintRegistered");
                    }

                    @Override
                    public void onSucceeded(FingerprintManager.CryptoObject cryptoObject) {
                        tvStatus.setText("onSuccess");
                    }

                    @Override
                    public void onFailed(AuthFailure authFailure) {
                        switch (authFailure.getErrorCode()) {
                            case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
                                break;
                            case AuthFailure.AUTHENTICATE_FAILED:
                                break;

                        }
                        tvStatus.setText("onFailed:" + authFailure.getErrorMessage());
                    }
                });

//                RxFingerprint.authenticate()
//                        .subscribe(new Observer<String>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//
//                            }
//
//                            @Override
//                            public void onNext(String s) {
//                                Log.e("test", s);
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleFingerprint.stopAuthenticate();
                tvStatus.setText("Auth stop.");
            }
        });
    }
}
