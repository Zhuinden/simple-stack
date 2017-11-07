package com.zhuinden.simplestackdemoexamplefragments.application.injection;

import android.content.Context;
import android.content.res.Resources;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplefragments.data.manager.DatabaseManager;
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplefragments.presentation.mapper.TaskMapper;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask.AddOrEditTaskPresenter;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics.StatisticsPresenter;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.taskdetail.TaskDetailPresenter;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks.TasksPresenter;
import com.zhuinden.simplestackdemoexamplefragments.util.BackstackHolder;
import com.zhuinden.simplestackdemoexamplefragments.util.MessageQueue;
import com.zhuinden.simplestackdemoexamplefragments.util.SchedulerHolder;

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

    MessageQueue messageQueue();

    @Named("applicationContext")
    Context applicationContext();

    Resources resources();

    AddOrEditTaskPresenter addOrEditTaskPresenter();

    TasksPresenter tasksPresenter();

    TaskDetailPresenter taskDetailPresenter();

    StatisticsPresenter statisticsPresenter();
}
