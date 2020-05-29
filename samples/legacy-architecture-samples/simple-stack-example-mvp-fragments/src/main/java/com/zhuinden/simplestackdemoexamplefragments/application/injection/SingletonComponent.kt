package com.zhuinden.simplestackdemoexamplefragments.application.injection

import android.content.Context
import android.content.res.Resources
import com.zhuinden.simplestackdemoexamplefragments.data.manager.DatabaseManager
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplefragments.features.addoredittask.AddOrEditTaskPresenter
import com.zhuinden.simplestackdemoexamplefragments.features.statistics.StatisticsPresenter
import com.zhuinden.simplestackdemoexamplefragments.features.taskdetail.TaskDetailPresenter
import com.zhuinden.simplestackdemoexamplefragments.features.tasks.TasksPresenter
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
@Component(modules = [SchedulerModule::class, AndroidModule::class])
interface SingletonComponent {
    fun databaseManager(): DatabaseManager

    @Named("LOOPER_SCHEDULER")
    fun looperScheduler(): SchedulerHolder

    @Named("WRITE_SCHEDULER")
    fun writeScheduler(): SchedulerHolder

    fun taskRepository(): TaskRepository

    fun backstackHolder(): BackstackHolder

    fun messageQueue(): MessageQueue

    @Named("applicationContext")
    fun applicationContext(): Context

    fun resources(): Resources

    fun addOrEditTaskPresenter(): AddOrEditTaskPresenter

    fun tasksPresenter(): TasksPresenter

    fun taskDetailPresenter(): TaskDetailPresenter

    fun statisticsPresenter(): StatisticsPresenter
}
