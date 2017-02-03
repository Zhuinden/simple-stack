package com.zhuinden.simplestackdemoexamplefragments.application.injection;

import android.content.Context;
import android.content.res.Resources;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackdemoexamplefragments.application.MainActivity;
import com.zhuinden.simplestackdemoexamplefragments.application.MainScopeListener;
import com.zhuinden.simplestackdemoexamplefragments.application.MainView;
import com.zhuinden.simplestackdemoexamplefragments.data.manager.DatabaseManager;
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplefragments.presentation.mapper.TaskMapper;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask.AddOrEditTaskFragment;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.first.FirstFragment;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.second.SecondFragment;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.statistics.StatisticsFragment;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.taskdetail.TaskDetailFragment;
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks.TasksFragment;
import com.zhuinden.simplestackdemoexamplefragments.util.BackstackHolder;
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

    @Named("applicationContext")
    Context applicationContext();

    Resources resources();

    void inject(MainActivity mainActivity);

    void inject(MainScopeListener mainScopeListener);

    void inject(MainView mainView);

    void inject(AddOrEditTaskFragment addOrEditTaskFragment);

    void inject(FirstFragment firstFragment);

    void inject(SecondFragment secondFragment);

    void inject(StatisticsFragment statisticsFragment);

    void inject(TaskDetailFragment taskDetailFragment);

    void inject(TasksFragment tasksFragment);
}
