package com.zhuinden.simplestackdemoexamplemvp.application.injection;

import android.content.Context;
import android.content.res.Resources;

import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskView;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics.StatisticsView;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail.TaskDetailView;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksView;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.application.MainActivity;
import com.zhuinden.simplestackdemoexamplemvp.application.MainScopeListener;
import com.zhuinden.simplestackdemoexamplemvp.application.MainView;
import com.zhuinden.simplestackdemoexamplemvp.data.manager.DatabaseManager;
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.mapper.TaskMapper;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.first.FirstView;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.second.SecondView;
import com.zhuinden.simplestackdemoexamplemvp.util.BackstackHolder;
import com.zhuinden.simplestackdemoexamplemvp.util.SchedulerHolder;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Owner on 2017. 01. 26..
 */

@Singleton
@Component(modules = {SchedulerModule.class, NavigationModule.class, AndroidModule.class})
public interface SingletonComponent {
    TaskMapper taskMapper();

    DatabaseManager databaseManager();

    @Named("LOOPER_SCHEDULER")
    SchedulerHolder looperScheduler();

    @Named("WRITE_SCHEDULER")
    SchedulerHolder writeScheduler();

    TaskRepository taskRepository();

    BackstackHolder backstackHolder();

    Backstack backstack();

    @Named("applicationContext")
    Context applicationContext();

    Resources resources();

    void inject(MainActivity mainActivity);

    void inject(MainScopeListener mainScopeListener);

    void inject(MainView mainView);

    void inject(FirstView firstView);

    void inject(SecondView secondView);

    void inject(TaskDetailView taskDetailView);

    void inject(AddOrEditTaskView addOrEditTaskView);

    void inject(StatisticsView statisticsView);

    void inject(TasksView tasksView);
}
