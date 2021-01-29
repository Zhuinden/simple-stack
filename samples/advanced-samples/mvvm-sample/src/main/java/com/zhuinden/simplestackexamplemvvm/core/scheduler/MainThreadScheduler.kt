package com.zhuinden.simplestackexamplemvvm.core.scheduler


import android.os.Handler
import android.os.Looper

class MainThreadScheduler : Scheduler {
    private val handler = Handler(Looper.getMainLooper())

    override fun execute(runnable: Runnable) {
        handler.post(runnable)
    }
}