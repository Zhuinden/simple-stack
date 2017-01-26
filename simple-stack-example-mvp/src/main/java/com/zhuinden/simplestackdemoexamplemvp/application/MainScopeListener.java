package com.zhuinden.simplestackdemoexamplemvp.application;

import android.os.Bundle;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.zhuinden.simplestackdemoexamplemvp.data.manager.DatabaseManager;
import com.zhuinden.simplestackdemoexamplemvp.util.SchedulerHolder;

import javax.inject.Inject;
import javax.inject.Named;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Owner on 2017. 01. 26..
 */

public class MainScopeListener extends Fragment {
    HandlerThread handlerThread;

    @Inject
    @Named("LOOPER_SCHEDULER")
    SchedulerHolder looperScheduler;

    @Inject
    DatabaseManager databaseManager;

    public MainScopeListener() {
        setRetainInstance(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handlerThread = new HandlerThread("LOOPER_SCHEDULER");
        handlerThread.start();
        synchronized(handlerThread) {
            looperScheduler.setScheduler(AndroidSchedulers.from(handlerThread.getLooper()));
        }
        databaseManager.openDatabase();
    }

    @Override
    public void onDestroy() {
        databaseManager.closeDatabase();
        super.onDestroy();
    }
}
