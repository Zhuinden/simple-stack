package com.zhuinden.simplestackdemoexamplemvp.application.injection;

import android.content.Context;
import android.content.res.Resources;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplemvp.data.manager.DatabaseManager;
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplemvp.presentation.mapper.TaskMapper;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask.AddOrEditTaskPresenter;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics.StatisticsPresenter;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.taskdetail.TaskDetailPresenter;
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksPresenter;
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

    AddOrEditTaskPresenter addOrEditTaskPresenter();

    StatisticsPresenter statisticsPresenter();

    TasksPresenter tasksPresenter();

    TaskDetailPresenter taskDetailPresenter();
}
