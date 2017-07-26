package com.zhuinden.simplestackexamplemvvm.application.injection;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Zhuinden on 2017.07.25..
 */

@Module
public class AndroidModule {
    private final Application app;

    public AndroidModule(Application application) {
        this.app = application;
    }

    @Provides
    Application application() {
        return app;
    }

    @Provides
    Context context() {
        return app;
    }

    @Provides
    Resources resources() {
        return app.getResources();
    }
}
