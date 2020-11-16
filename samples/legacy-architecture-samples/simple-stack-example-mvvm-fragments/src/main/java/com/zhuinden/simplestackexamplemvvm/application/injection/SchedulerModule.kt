package com.zhuinden.simplestackexamplemvvm.application.injection


import com.zhuinden.simplestackexamplemvvm.core.scheduler.BackgroundScheduler
import com.zhuinden.simplestackexamplemvvm.core.scheduler.MainThreadScheduler
import com.zhuinden.simplestackexamplemvvm.core.scheduler.NetworkScheduler
import com.zhuinden.simplestackexamplemvvm.core.scheduler.Scheduler
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * Created by Owner on 2017. 07. 27..
 */
@Module
class SchedulerModule {
    @Provides
    @Named("NETWORK")
    fun networkScheduler(networkScheduler: NetworkScheduler): Scheduler = networkScheduler

    @Provides
    @Named("BACKGROUND")
    fun backgroundScheduler(backgroundScheduler: BackgroundScheduler): Scheduler = backgroundScheduler

    @Provides
    @Named("MAIN_THREAD")
    fun mainThreadScheduler(mainThreadScheduler: MainThreadScheduler): Scheduler = mainThreadScheduler
}