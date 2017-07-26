package com.example.android.architecture.blueprints.todoapp.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource;
import com.example.android.architecture.blueprints.todoapp.presentation.paths.addedittask.AddEditTaskViewModel;
import com.example.android.architecture.blueprints.todoapp.presentation.paths.statistics.StatisticsViewModel;
import com.example.android.architecture.blueprints.todoapp.presentation.paths.taskdetail.TaskDetailViewModel;
import com.example.android.architecture.blueprints.todoapp.presentation.paths.tasks.TaskItemViewModel;
import com.example.android.architecture.blueprints.todoapp.presentation.paths.tasks.TasksViewModel;
import com.zhuinden.simplestack.Backstack;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Zhuinden on 2017.07.25..
 */

@Singleton
@Component(modules = {AndroidModule.class, BackstackModule.class, DataModule.class})
public interface ApplicationComponent {
    Backstack backstack();

    BackstackHolder backstackHolder();

    TasksViewModel tasksViewModel();

    StatisticsViewModel statisticsViewModel();

    AddEditTaskViewModel addEditTaskViewModel();

    TaskDetailViewModel taskDetailViewModel();

    TaskItemViewModel taskItemViewModel();

    @Named("LOCAL")
    TasksDataSource tasksLocalDataSource();

    @Named("REMOTE")
    TasksDataSource tasksRemoteDataSource();

    Context context();

    Application application();

    Resources resources();
}
