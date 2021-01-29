package com.zhuinden.simplestackexamplemvvm.application


import android.app.Application
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager
import com.zhuinden.simplestackexamplemvvm.core.scheduler.BackgroundScheduler
import com.zhuinden.simplestackexamplemvvm.data.tasks.TaskDao
import com.zhuinden.simplestackexamplemvvm.data.tasks.TaskTable
import com.zhuinden.simplestackexamplemvvm.data.tasks.TasksDataSource
import com.zhuinden.simplestackextensions.servicesktx.add

class CustomApplication : Application() {
    lateinit var globalServices: GlobalServices
        private set

    override fun onCreate() {
        super.onCreate()

        val snackbarTextEmitter = SnackbarTextEmitter()

        val backgroundScheduler = BackgroundScheduler()

        val taskTable = TaskTable()

        val databaseManager = DatabaseManager(this, listOf(taskTable), backgroundScheduler)

        val taskDao = TaskDao(databaseManager, taskTable)

        val tasksDataSource = TasksDataSource(backgroundScheduler, taskDao)

        globalServices = GlobalServices.builder()
            .add(snackbarTextEmitter)
            .add(backgroundScheduler)
            .add(databaseManager)
            .add(taskDao)
            .add(tasksDataSource)
            .build()
    }
}