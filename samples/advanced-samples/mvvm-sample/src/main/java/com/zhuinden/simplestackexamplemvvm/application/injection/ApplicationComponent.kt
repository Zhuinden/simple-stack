package com.zhuinden.simplestackexamplemvvm.application.injection


import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.zhuinden.simplestackexamplemvvm.core.scheduler.Scheduler
import com.zhuinden.simplestackexamplemvvm.data.source.TasksDataSource
import com.zhuinden.simplestackexamplemvvm.util.MessageQueue
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Zhuinden on 2017.07.25..
 */
@Singleton
@Component(modules = [AndroidModule::class, SchedulerModule::class, TableModule::class])
interface ApplicationComponent {
    @Named("MAIN_THREAD")
    fun mainThreadScheduler(): Scheduler

    @Named("BACKGROUND")
    fun backgroundScheduler(): Scheduler

    @Named("NETWORK")
    fun networkScheduler(): Scheduler
    fun context(): Context
    fun application(): Application
    fun resources(): Resources
    fun tasksDataSource(): TasksDataSource
    fun messageQueue(): MessageQueue

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }
}