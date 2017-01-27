package com.zhuinden.simplestackdemoexamplemvp.application.injection;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.util.BackstackHolder;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Owner on 2017. 01. 27..
 */

@Module
public class NavigationModule {
    @Provides
    public Backstack backstack(BackstackHolder backstackHolder) {
        return backstackHolder.getBackstack();
    }
}
