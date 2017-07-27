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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager;
import com.zhuinden.simplestackexamplemvvm.core.database.liveresults.LiveResults;
import com.zhuinden.simplestackexamplemvvm.core.scheduler.BackgroundScheduler;
import com.zhuinden.simplestackexamplemvvm.data.Task;
import com.zhuinden.simplestackexamplemvvm.data.source.TasksDataSource;
import com.zhuinden.simplestackexamplemvvm.data.tables.TaskTable;
import com.zhuinden.simplestackexamplemvvm.util.Strings;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.zhuinden.simplestackexamplemvvm.util.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
public class TasksLocalDataSource
        implements TasksDataSource {
    private final DatabaseManager databaseManager;
    private final TaskTable taskTable;
    private final DatabaseManager.Mapper<Task> taskMapper;

    // Experimental.
    private List<WeakReference<LiveResults<Task>>> liveDatas = Collections.synchronizedList(new LinkedList<>());

    private final BackgroundScheduler backgroundScheduler;

    @Inject
    TasksLocalDataSource(BackgroundScheduler backgroundScheduler, DatabaseManager databaseManager, TaskTable taskTable) {
        this.backgroundScheduler = backgroundScheduler;
        this.databaseManager = databaseManager;
        this.taskTable = taskTable;
        this.taskMapper = taskTable;
    }

    private void addLiveResults(LiveResults<Task> liveResults) {
        synchronized(this) {
            this.liveDatas.add(new WeakReference<>(liveResults));
        }
    }

    private LiveResults<Task> createResults(DatabaseManager.QueryDefinition queryDefinition) {
        LiveResults<Task> results = new LiveResults<>(backgroundScheduler, databaseManager, taskTable, taskMapper, queryDefinition);
        addLiveResults(results);
        return results;
    }

    public Task getTask(String taskId) {
        return databaseManager.findOne(taskTable, taskMapper, (database, table) -> database.rawQuery( //
                " SELECT " + Strings.join(taskTable.getAllQueryFields()) + " FROM " + table.getTableName() + //
                        " WHERE " + TaskTable.$ENTRY_ID + " LIKE ? " + " LIMIT 1 ", //
                new String[]{taskId}));
    }

    @Override
    public LiveResults<Task> getTasksWithChanges() {
        return createResults((database, table) -> database.query(table.getTableName(),
                table.getAllQueryFields(),
                null,
                null,
                null,
                null,
                TaskTable.$ENTRY_ID + " ASC"));
    }

    @Override
    public LiveResults<Task> getTaskWithChanges(@Nullable String taskId) {
        return createResults((database, table) -> {
            if(taskId != null) {
                return database.rawQuery( //
                        " SELECT " + Strings.join(taskTable.getAllQueryFields()) + " FROM " + table.getTableName() + //
                                " WHERE " + TaskTable.$ENTRY_ID + " LIKE ? " + " LIMIT 1 ", //
                        new String[]{taskId});
            } else {
                return database.rawQuery( //
                        " SELECT " + Strings.join(taskTable.getAllQueryFields()) + " FROM " + table.getTableName() + " WHERE 1 = 0 ",
                        new String[0]);
            }
        });
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        backgroundScheduler.execute(() -> {
            databaseManager.insert(taskTable, taskMapper, task);
            refreshTasks();
        });
    }

    public void saveTasks(List<Task> tasks) {
        checkNotNull(tasks);
        backgroundScheduler.execute(() -> {
            databaseManager.insert(taskTable, taskMapper, tasks);
            refreshTasks();
        });
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
        backgroundScheduler.execute(() -> {
            databaseManager.insert(taskTable, taskMapper, task.toBuilder().setCompleted(true).build());
            refreshTasks();
        });
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        backgroundScheduler.execute(() -> {
            databaseManager.insert(taskTable, taskMapper, task.toBuilder().setCompleted(false).build());
            refreshTasks();
        });
    }

    @Override
    public void clearCompletedTasks() {
        backgroundScheduler.execute(() -> {
            List<Task> list = databaseManager.findAll(taskTable,
                    taskMapper,
                    (database, table) -> database.rawQuery(" SELECT " + Strings.join(table.getAllQueryFields()) + //
                                    " FROM " + table.getTableName() + " WHERE " + TaskTable.$COMPLETED + " LIKE ?",
                            new String[]{String.valueOf(1)}));
            databaseManager.delete(taskTable, list);
            refreshTasks();
        });
    }

    @Override
    public void refreshTasks() {
        synchronized(this) {
            Iterator<WeakReference<LiveResults<Task>>> iterator = liveDatas.iterator();
            while(iterator.hasNext()) {
                WeakReference<LiveResults<Task>> weakReference = iterator.next();
                LiveResults<Task> taskLiveData = weakReference.get();
                if(taskLiveData == null) {
                    iterator.remove();
                } else {
                    taskLiveData.refresh();
                }
            }
        }
    }

    @Override
    public void deleteAllTasks() {
        backgroundScheduler.execute(() -> {
            databaseManager.deleteAll(taskTable);
            refreshTasks();
        });
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        backgroundScheduler.execute(() -> {
            Task task = getTask(taskId);
            databaseManager.delete(taskTable, task);
            refreshTasks();
        });
    }

    @Override
    public LiveResults<Task> getActiveTasksWithChanges() {
        return createResults((database, table) -> database.query(false,
                table.getTableName(),
                table.getAllQueryFields(),
                TaskTable.$COMPLETED + " = ?",
                new String[]{String.valueOf(0)},
                null,
                null,
                TaskTable.$ENTRY_ID + " ASC",
                null));
    }

    @Override
    public LiveResults<Task> getCompletedTasksWithChanges() {
        return createResults((database, table) -> database.query(false,
                table.getTableName(),
                table.getAllQueryFields(),
                TaskTable.$COMPLETED + " = ?",
                new String[]{String.valueOf(1)},
                null,
                null,
                TaskTable.$ENTRY_ID + " ASC",
                null));
    }
}