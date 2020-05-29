package com.zhuinden.simplestackexamplemvvm.application.injection;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackexamplemvvm.application.BackstackHolder;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Owner on 2017. 07. 26..
 */

@Module
public class BackstackModule {
    @Provides
    Backstack backstack(BackstackHolder backstackHolder) {
        return backstackHolder.getBackstack();
    }
}
