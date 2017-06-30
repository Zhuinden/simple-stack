package com.zhuinden.simplestackdemoexamplemvp.application.injection;

import android.content.Context;
import android.content.res.Resources;

import com.zhuinden.simplestackdemoexamplemvp.application.CustomApplication;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Owner on 2017. 01. 27..
 */

@Module
public class AndroidModule {
    @Provides
    @Named("applicationContext")
    public Context applicationContext() {
        return CustomApplication.get();
    }

    @Provides
    public Resources resources(@Named("applicationContext") Context context) {
        return context.getResources();
    }
}
