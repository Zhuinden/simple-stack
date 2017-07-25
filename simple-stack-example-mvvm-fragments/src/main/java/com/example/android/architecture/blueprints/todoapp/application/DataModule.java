package com.example.android.architecture.blueprints.todoapp.application;

import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Zhuinden on 2017.07.26..
 */

@Module
public class DataModule {
    @Provides
    @Named("LOCAL")
    TasksDataSource localDataSource(TasksLocalDataSource tasksLocalDataSource) {
        return tasksLocalDataSource;
    }

    @Provides
    @Named("REMOTE")
    TasksDataSource remoteDataSource(TasksRemoteDataSource tasksRemoteDataSource) {
        return tasksRemoteDataSource;
    }
}
