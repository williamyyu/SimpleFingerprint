package com.willy.example;

import android.app.Application;

import com.willy.simplefingerprint.SimpleFingerprint;

/**
 * Created by willy on 2017/6/2.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SimpleFingerprint.init(this, true);
    }
}
