package com.zhuinden.simplestackdemoexamplemvp.application.injection

import android.content.Context
import android.content.res.Resources
import com.zhuinden.simplestackdemoexamplemvp.data.manager.DatabaseManager
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplemvp.features.addoredittask.AddOrEditTaskPresenter
import com.zhuinden.simplestackdemoexamplemvp.features.statistics.StatisticsPresenter
import com.zhuinden.simplestackdemoexamplemvp.features.taskdetail.TaskDetailPresenter
import com.zhuinden.simplestackdemoexamplemvp.features.tasks.TasksPresenter
import com.zhuinden.simplestackdemoexamplemvp.util.BackstackHolder
import com.zhuinden.simplestackdemoexamplemvp.util.SchedulerHolder
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Owner on 2017. 01. 26..
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

    @Named("applicationContext")
    fun applicationContext(): Context

    fun resources(): Resources

    fun addOrEditTaskPresenter(): AddOrEditTaskPresenter

    fun statisticsPresenter(): StatisticsPresenter

    fun tasksPresenter(): TasksPresenter

    fun taskDetailPresenter(): TaskDetailPresenter
}
