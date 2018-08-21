package com.zhuinden.simplestackdemoexamplemvp.application.injection

import com.zhuinden.simplestackdemoexamplemvp.util.SchedulerHolder
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Owner on 2017. 01. 26..
 */

@Module
class SchedulerModule {
    @Provides
    @Named("LOOPER_SCHEDULER")
    @Singleton
    fun looperScheduler(schedulerHolder: SchedulerHolder): SchedulerHolder = schedulerHolder

    @Provides
    @Named("WRITE_SCHEDULER")
    @Singleton
    fun writeScheduler(schedulerHolder: SchedulerHolder): SchedulerHolder = schedulerHolder.also { holder ->
        holder.scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
    }
}
