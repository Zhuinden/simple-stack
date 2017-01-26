package com.zhuinden.simplestackdemoexamplemvp.application.injection;

import com.zhuinden.simplestackdemoexamplemvp.application.MainActivity;
import com.zhuinden.simplestackdemoexamplemvp.application.MainScopeListener;
import com.zhuinden.simplestackdemoexamplemvp.data.manager.DatabaseManager;
import com.zhuinden.simplestackdemoexamplemvp.presentation.mapper.TaskMapper;
import com.zhuinden.simplestackdemoexamplemvp.util.SchedulerHolder;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Owner on 2017. 01. 26..
 */

@Singleton
@Component(modules = {SchedulerModule.class})
public interface SingletonComponent {
    TaskMapper taskMapper();
    DatabaseManager databaseManager();

    @Named("LOOPER_SCHEDULER")
    SchedulerHolder schedulerHolder();

    void inject(MainActivity mainActivity);

    void inject(MainScopeListener mainScopeListener);
}
