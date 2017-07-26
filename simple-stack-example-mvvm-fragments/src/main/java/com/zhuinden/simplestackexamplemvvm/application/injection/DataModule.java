package com.zhuinden.simplestackexamplemvvm.application.injection;

import com.zhuinden.simplestackexamplemvvm.data.source.TasksDataSource;
import com.zhuinden.simplestackexamplemvvm.data.source.local.TasksLocalDataSource;
import com.zhuinden.simplestackexamplemvvm.data.source.remote.TasksRemoteDataSource;

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
