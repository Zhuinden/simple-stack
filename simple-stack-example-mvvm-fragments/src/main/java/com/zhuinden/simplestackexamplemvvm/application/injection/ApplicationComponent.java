package com.zhuinden.simplestackexamplemvvm.application.injection;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackexamplemvvm.application.BackstackHolder;
import com.zhuinden.simplestackexamplemvvm.core.scheduler.Scheduler;
import com.zhuinden.simplestackexamplemvvm.presentation.paths.addedittask.AddEditTaskViewModel;
import com.zhuinden.simplestackexamplemvvm.presentation.paths.statistics.StatisticsViewModel;
import com.zhuinden.simplestackexamplemvvm.presentation.paths.taskdetail.TaskDetailViewModel;
import com.zhuinden.simplestackexamplemvvm.presentation.paths.tasks.TaskItemViewModel;
import com.zhuinden.simplestackexamplemvvm.presentation.paths.tasks.TasksViewModel;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
/**
 * Created by Zhuinden on 2017.07.25..
 */

@Singleton
@Component(modules = {AndroidModule.class, BackstackModule.class, SchedulerModule.class, TableModule.class})
public interface ApplicationComponent {
    Backstack backstack();

    BackstackHolder backstackHolder();

    @Named("MAIN_THREAD")
    Scheduler mainThreadScheduler();

    @Named("BACKGROUND")
    Scheduler backgroundScheduler();

    @Named("NETWORK")
    Scheduler networkScheduler();

    TasksViewModel tasksViewModel();

    StatisticsViewModel statisticsViewModel();

    AddEditTaskViewModel addEditTaskViewModel();

    TaskDetailViewModel taskDetailViewModel();

    TaskItemViewModel taskItemViewModel();

    Context context();

    Application application();

    Resources resources();
}
