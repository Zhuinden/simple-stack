package com.zhuinden.simplestackexamplemvvm.application.injection;

import com.zhuinden.simplestackexamplemvvm.core.scheduler.BackgroundScheduler;
import com.zhuinden.simplestackexamplemvvm.core.scheduler.MainThreadScheduler;
import com.zhuinden.simplestackexamplemvvm.core.scheduler.NetworkScheduler;
import com.zhuinden.simplestackexamplemvvm.core.scheduler.Scheduler;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Owner on 2017. 07. 27..
 */

@Module
public class SchedulerModule {
    @Provides
    @Named("NETWORK")
    Scheduler networkScheduler(NetworkScheduler networkScheduler) {
        return networkScheduler;
    }

    @Provides
    @Named("BACKGROUND")
    Scheduler backgroundScheduler(BackgroundScheduler backgroundScheduler) {
        return backgroundScheduler;
    }

    @Provides
    @Named("MAIN_THREAD")
    Scheduler mainThreadScheduler(MainThreadScheduler mainThreadScheduler) {
        return mainThreadScheduler;
    }
}
