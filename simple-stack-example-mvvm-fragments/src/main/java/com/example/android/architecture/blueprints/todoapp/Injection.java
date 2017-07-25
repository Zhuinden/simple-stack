package com.example.android.architecture.blueprints.todoapp;

import android.content.Context;

import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository;
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource;

/**
 * Created by Zhuinden on 2017.07.25..
 */

public class Injection {
    public static TasksRepository provideTasksRepository(Context applicationContext) {
        return TasksRepository.getInstance(TasksLocalDataSource.getInstance(applicationContext), TasksRemoteDataSource.getInstance());
    }
}
