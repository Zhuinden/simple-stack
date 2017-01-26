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

    @Override
    public void onCreate() {
        super.onCreate();
        singletonComponent = DaggerSingletonComponent.create();
    }

    public static CustomApplication get(Context context) {
        return (CustomApplication)context.getApplicationContext();
    }

    public SingletonComponent getComponent() {
        return singletonComponent;
    }
}
