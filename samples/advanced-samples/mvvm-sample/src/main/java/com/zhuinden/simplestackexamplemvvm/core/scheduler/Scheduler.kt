package com.zhuinden.simplestackexamplemvvm.core.scheduler

interface Scheduler {
    fun execute(runnable: Runnable)
}