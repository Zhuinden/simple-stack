package com.zhuinden.simplestackdemoexamplemvp.application;

import android.app.Application;
import android.content.Context;

import com.zhuinden.simplestackdemoexamplemvp.application.injection.DaggerSingletonComponent;
import com.zhuinden.simplestackdemoexamplemvp.application.injection.SingletonComponent;

/**
 * Created by Owner on 2017. 01. 26..
 */

public class CustomApplication extends Application {
    SingletonComponent singletonComponent;

    private static CustomApplication INSTANCE;

    public void initialize() {
        if(INSTANCE == null) {
            INSTANCE = this;
            singletonComponent = DaggerSingletonComponent.create();
        }
    }

    public static CustomApplication get(Context context) {
        return (CustomApplication)context.getApplicationContext();
    }

    SingletonComponent getComponent() {
        return singletonComponent;
    }

    public static CustomApplication get() {
        return INSTANCE;
    }
}
