package com.zhuinden.simplestackdemoexamplefragments.application

import android.app.Application
import com.zhuinden.simplestack.GlobalServices

import com.zhuinden.simplestackdemoexamplefragments.data.manager.DatabaseManager
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository
import com.zhuinden.simplestackdemoexamplefragments.util.MessageQueue
import com.zhuinden.simplestackdemoexamplefragments.util.SchedulerHolder
import com.zhuinden.simplestackextensions.servicesktx.add
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

/**
 * Created by Zhuinden on 2018. 08. 20.
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
