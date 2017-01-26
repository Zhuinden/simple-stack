package com.zhuinden.simplestackdemoexamplemvp.util;

import javax.inject.Inject;

import rx.Scheduler;

/**
 * Created by Owner on 2017. 01. 26..
 */
public class SchedulerHolder {
    private Scheduler scheduler;

    @Inject
    public SchedulerHolder() {
    }

    public Scheduler getScheduler() {
        if(scheduler == null) {
            throw new IllegalStateException("Scheduler should not be null!");
        }
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
