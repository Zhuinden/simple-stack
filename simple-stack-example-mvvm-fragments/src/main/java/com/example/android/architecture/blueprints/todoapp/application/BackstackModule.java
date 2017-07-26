package com.example.android.architecture.blueprints.todoapp.application;

import com.zhuinden.simplestack.Backstack;

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
