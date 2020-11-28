package com.zhuinden.simplestackdemoexamplemvp.application

import android.app.Application
import com.zhuinden.simplestack.GlobalServices

import com.zhuinden.simplestackdemoexamplemvp.data.manager.DatabaseManager
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplemvp.util.MessageQueue
import com.zhuinden.simplestackdemoexamplemvp.util.SchedulerHolder
import com.zhuinden.simplestackextensions.servicesktx.add
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

/**
 * Created by Owner on 2017. 01. 26..
 */

class CustomApplication : Application() {
    lateinit var globalServices: GlobalServices
        private set

    override fun onCreate() {
        super.onCreate()

        val looperScheduler = SchedulerHolder()
        val databaseManager = DatabaseManager(looperScheduler)
        val writeScheduler = SchedulerHolder().also { holder ->
            holder.scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
        }
        val taskRepository = TaskRepository(looperScheduler, writeScheduler)
        val messageQueue = MessageQueue()

        globalServices = GlobalServices.builder()
            .add(looperScheduler, "LOOPER_SCHEDULER")
            .add(writeScheduler, "WRITE_SCHEDULER")
            .add(databaseManager)
            .add(taskRepository)
            .add(messageQueue)
            .build()
    }
}
