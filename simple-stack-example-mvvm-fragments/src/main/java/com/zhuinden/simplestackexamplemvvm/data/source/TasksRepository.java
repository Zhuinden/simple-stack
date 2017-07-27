/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhuinden.simplestackexamplemvvm.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.simplestackexamplemvvm.core.database.liveresults.LiveResults;
import com.zhuinden.simplestackexamplemvvm.data.Task;
import com.zhuinden.simplestackexamplemvvm.data.source.local.TasksLocalDataSource;
import com.zhuinden.simplestackexamplemvvm.data.source.remote.TasksRemoteDataService;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.zhuinden.simplestackexamplemvvm.util.Preconditions.checkNotNull;

/**
 * Experimental.
 */
@Singleton
public class TasksRepository {
    private final TasksDataSource tasksLocalDataSource;
    private final TasksRemoteDataService tasksRemoteDataService;

    @Inject
    TasksRepository(@NonNull TasksRemoteDataService tasksRemoteDataService, TasksLocalDataSource tasksLocalDataSource) {
        this.tasksLocalDataSource = checkNotNull(tasksLocalDataSource);
        this.tasksRemoteDataService = checkNotNull(tasksRemoteDataService);
    }

    public LiveResults<Task> getTasks() {
        return tasksLocalDataSource.getTasksWithChanges();
    }

    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        tasksLocalDataSource.saveTask(task);
        tasksRemoteDataService.saveTask(task);
    }

    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
        tasksLocalDataSource.completeTask(task);
        tasksRemoteDataService.completeTask(task);
    }

    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        tasksLocalDataSource.activateTask(task);
        tasksRemoteDataService.activateTask(task);
    }

    public void clearCompletedTasks() {
        tasksLocalDataSource.clearCompletedTasks();
        tasksRemoteDataService.clearCompletedTasks();
    }

    public LiveResults<Task> getTask(@Nullable final String taskId) {
        return tasksLocalDataSource.getTaskWithChanges(taskId);
    }

    public void refreshTasks() {
        tasksRemoteDataService.getTasks();
    }

    public void deleteAllTasks() {
        tasksRemoteDataService.deleteAllTasks();
        tasksLocalDataSource.deleteAllTasks();
    }

    public void deleteTask(@NonNull String taskId) {
        tasksRemoteDataService.deleteTask(checkNotNull(taskId));
        tasksLocalDataSource.deleteTask(checkNotNull(taskId));
    }

    public LiveResults<Task> getActiveTasks() {
        return tasksLocalDataSource.getActiveTasksWithChanges();
    }

    public LiveResults<Task> getCompletedTasks() {
        return tasksLocalDataSource.getCompletedTasksWithChanges();
    }
}
