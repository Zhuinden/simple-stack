package com.zhuinden.simplestackdemoexamplemvp.application.injection

import android.content.Context
import android.content.res.Resources
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackdemoexamplemvp.data.manager.DatabaseManager
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplemvp.presentation.mapper.TaskMapper
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskPresenter
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics.StatisticsPresenter
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail.TaskDetailPresenter
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksPresenter
import com.zhuinden.simplestackdemoexamplemvp.util.BackstackHolder
import com.zhuinden.simplestackdemoexamplemvp.util.SchedulerHolder
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Owner on 2017. 01. 26..
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

    @Named("applicationContext")
    fun applicationContext(): Context

    fun resources(): Resources

    fun addOrEditTaskPresenter(): AddOrEditTaskPresenter

    fun statisticsPresenter(): StatisticsPresenter

    fun tasksPresenter(): TasksPresenter

    fun taskDetailPresenter(): TaskDetailPresenter
}
