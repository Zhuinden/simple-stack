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

package com.zhuinden.simplestackexamplemvvm.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager;
import com.zhuinden.simplestackexamplemvvm.data.Task;
import com.zhuinden.simplestackexamplemvvm.data.source.TasksDataSource;
import com.zhuinden.simplestackexamplemvvm.data.tables.TaskTable;
import com.zhuinden.simplestackexamplemvvm.util.Strings;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.zhuinden.simplestackexamplemvvm.util.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 *
 * TODO: destroy garbage data layer code
 */
@Singleton
public class TasksLocalDataSource
        implements TasksDataSource {
    private final DatabaseManager databaseManager;
    private final TaskTable taskTable;
    private final DatabaseManager.Mapper<Task> taskMapper;

    @Inject
    TasksLocalDataSource(@NonNull Context context, DatabaseManager databaseManager, TaskTable taskTable) {
        checkNotNull(context);
        this.databaseManager = databaseManager;
        this.taskTable = taskTable;
        this.taskMapper = taskTable;
    }

    /**
     * Note: {@link LoadTasksCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {
        List<Task> tasks = databaseManager.findAll(taskTable, taskMapper);
        if(tasks.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onTasksLoaded(tasks);
        }
    }

    private Task getTask(String taskId) {
        return databaseManager.findOne(taskTable, taskMapper, (database, table) -> database.rawQuery( //
                " SELECT " + Strings.join(taskTable.getAllQueryFields()) + " FROM " + table.getTableName() + //
                        " WHERE " + TaskTable.$ENTRY_ID + " LIKE ? " + " LIMIT 1 ", //
                new String[]{taskId}));
    }

    /**
     * Note: {@link GetTaskCallback#onDataNotAvailable()} is fired if the {@link Task} isn't
     * found.
     */
    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        Task task = getTask(taskId);
        if(task != null) {
            callback.onTaskLoaded(task);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        databaseManager.insert(taskTable, taskMapper, task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
        databaseManager.insert(taskTable, taskMapper, task.toBuilder().setCompleted(true).build());
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        Task task = getTask(taskId);
        if(task != null) {
            completeTask(task);
        }
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        databaseManager.insert(taskTable, taskMapper, task.toBuilder().setCompleted(false).build());
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        checkNotNull(taskId);
        Task task = getTask(taskId);
        if(task != null) {
            activateTask(task);
        }
    }

    @Override
    public void clearCompletedTasks() {
        List<Task> list = databaseManager.findAll(taskTable,
                taskMapper,
                (database, table) -> database.rawQuery(" SELECT " + Strings.join(table.getAllQueryFields()) + " FROM " + table
                                .getTableName() + " WHERE " + TaskTable.$COMPLETED + " LIKE ?",
                        new String[]{String.valueOf(1)}));
        databaseManager.delete(taskTable, list);
    }

    @Override
    public void refreshTasks() {
        // Not possible because Task objects are immutable and non-reactive.
    }

    @Override
    public void deleteAllTasks() {
        databaseManager.deleteAll(taskTable);
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        Task task = getTask(taskId);
        databaseManager.delete(taskTable, task);
    }
}
