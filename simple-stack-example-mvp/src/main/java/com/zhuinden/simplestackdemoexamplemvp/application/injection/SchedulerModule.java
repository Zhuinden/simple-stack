package com.zhuinden.simplestackdemoexamplemvp.application.injection;

import com.zhuinden.simplestackdemoexamplemvp.util.SchedulerHolder;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Owner on 2017. 01. 26..
 */

@Module
public class SchedulerModule {
    @Provides
    @Named("LOOPER_SCHEDULER")
    @Singleton
    SchedulerHolder looperScheduler(SchedulerHolder schedulerHolder) {
        return schedulerHolder;
    }
}
