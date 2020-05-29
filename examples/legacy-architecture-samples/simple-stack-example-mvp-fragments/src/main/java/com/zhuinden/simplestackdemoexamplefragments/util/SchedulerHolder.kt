package com.zhuinden.simplestackdemoexamplefragments.util

import io.reactivex.Scheduler
import javax.inject.Inject


/**
 * Created by Zhuinden on 2018. 08. 20.
 */
// NOT SCOPED!
class SchedulerHolder @Inject constructor() {
    lateinit var scheduler: Scheduler
}
