package com.zhuinden.simplestackdemoexamplemvp.util

import io.reactivex.Scheduler
import javax.inject.Inject


/**
 * Created by Owner on 2017. 01. 26..
 */
// NOT SCOPED!
class SchedulerHolder @Inject constructor() {
    lateinit var scheduler: Scheduler
}
