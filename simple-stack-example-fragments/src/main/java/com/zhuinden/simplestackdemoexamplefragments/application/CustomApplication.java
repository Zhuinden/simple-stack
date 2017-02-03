package com.zhuinden.simplestackdemoexamplefragments.application;

import android.app.Application;
import android.content.Context;

import com.zhuinden.simplestackdemoexamplefragments.application.injection.DaggerSingletonComponent;
import com.zhuinden.simplestackdemoexamplefragments.application.injection.SingletonComponent;

/**
 * Created by Owner on 2017. 01. 26..
 */

public class CustomApplication
        extends Application {
    SingletonComponent singletonComponent;

    private static CustomApplication INSTANCE;

    public void initialize() {
        if(INSTANCE == null) {
            INSTANCE = this;
            singletonComponent = DaggerSingletonComponent.create();
        }
    }

    public static CustomApplication get(Context context) {
        return (CustomApplication) context.getApplicationContext();
    }

    public SingletonComponent getComponent() {
        return singletonComponent;
    }

    public static CustomApplication get() {
        return INSTANCE;
    }
}
