package com.zhuinden.simplestackdemoexamplefragments.application.injection

import com.zhuinden.simplestackdemoexamplefragments.util.SchedulerHolder
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

@Module
object SchedulerModule {
    @Provides
    @Named("LOOPER_SCHEDULER")
    @Singleton
    @JvmStatic
    fun looperScheduler(schedulerHolder: SchedulerHolder): SchedulerHolder = schedulerHolder

    @Provides
    @Named("WRITE_SCHEDULER")
    @Singleton
    @JvmStatic
    fun writeScheduler(schedulerHolder: SchedulerHolder): SchedulerHolder = schedulerHolder.also { holder ->
        holder.scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
    }
}
