package com.zhuinden.simplestackdemoexamplemvp.application.injection;

import com.zhuinden.simplestackdemoexamplemvp.application.MainActivity;
import com.zhuinden.simplestackdemoexamplemvp.application.MainScopeListener;
import com.zhuinden.simplestackdemoexamplemvp.data.manager.DatabaseManager;
import com.zhuinden.simplestackdemoexamplemvp.presentation.mapper.TaskMapper;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskCoordinator;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksView;
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
    SchedulerHolder looperScheduler();

    @Named("WRITE_SCHEDULER")
    SchedulerHolder writeScheduler();

    void inject(MainActivity mainActivity);

    void inject(MainScopeListener mainScopeListener);

    void inject(AddOrEditTaskCoordinator addOrEditTaskCoordinator);

    void inject(TasksView tasksView);
}
