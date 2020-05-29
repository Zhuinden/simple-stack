package com.zhuinden.simplestackdemoexamplemvp.application

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import androidx.fragment.app.Fragment

import com.zhuinden.simplestackdemoexamplemvp.data.manager.DatabaseManager
import com.zhuinden.simplestackdemoexamplemvp.util.SchedulerHolder

import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by Owner on 2017. 01. 26..
 */

class MainScopeListener : Fragment() {
    lateinit var handlerThread: HandlerThread

    val looperScheduler: SchedulerHolder = Injector.get().looperScheduler()
    val databaseManager: DatabaseManager = Injector.get().databaseManager()

    init {
        retainInstance = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handlerThread = HandlerThread("LOOPER_SCHEDULER")
        handlerThread.start()
        synchronized(handlerThread) {
            looperScheduler.scheduler = AndroidSchedulers.from(handlerThread.looper)
        }
        databaseManager.openDatabase()
    }

    override fun onDestroy() {
        databaseManager.closeDatabase()
        Handler().postDelayed({ handlerThread.quit() }, 300)
        super.onDestroy()
    }
}
