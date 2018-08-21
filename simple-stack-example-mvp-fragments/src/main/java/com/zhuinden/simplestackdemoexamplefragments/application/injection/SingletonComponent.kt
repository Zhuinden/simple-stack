package com.zhuinden.simplestackdemoexamplefragments.application.injection

import android.content.Context
import android.content.res.Resources
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackdemoexamplefragments.data.manager.DatabaseManager
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplefragments.presentation.mapper.TaskMapper
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask.AddOrEditTaskPresenter
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics.StatisticsPresenter
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.taskdetail.TaskDetailPresenter
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks.TasksPresenter
import com.zhuinden.simplestackdemoexamplefragments.util.BackstackHolder
import com.zhuinden.simplestackdemoexamplefragments.util.MessageQueue
import com.zhuinden.simplestackdemoexamplefragments.util.SchedulerHolder
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

@Singleton
@Component(modules = [SchedulerModule::class, NavigationModule::class, AndroidModule::class])
interface SingletonComponent {
    fun taskMapper(): TaskMapper

    fun databaseManager(): DatabaseManager

    @Named("LOOPER_SCHEDULER")
    fun looperScheduler(): SchedulerHolder

    @Named("WRITE_SCHEDULER")
    fun writeScheduler(): SchedulerHolder

    fun taskRepository(): TaskRepository

    fun backstackHolder(): BackstackHolder

    fun backstack(): Backstack

    fun messageQueue(): MessageQueue

    @Named("applicationContext")
    fun applicationContext(): Context

    fun resources(): Resources

    fun addOrEditTaskPresenter(): AddOrEditTaskPresenter

    fun tasksPresenter(): TasksPresenter

    fun taskDetailPresenter(): TaskDetailPresenter

    fun statisticsPresenter(): StatisticsPresenter
}
