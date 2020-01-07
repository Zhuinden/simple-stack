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

import com.zhuinden.simplestackexamplemvvm.core.database.LiveResults;
import com.zhuinden.simplestackexamplemvvm.core.database.QueryBuilder;
import com.zhuinden.simplestackexamplemvvm.core.scheduler.BackgroundScheduler;
import com.zhuinden.simplestackexamplemvvm.data.Task;
import com.zhuinden.simplestackexamplemvvm.data.dao.TaskDao;
import com.zhuinden.simplestackexamplemvvm.data.tables.TaskTable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.zhuinden.simplestackexamplemvvm.util.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
public class TasksRepository
        implements TasksDataSource {
    private final BackgroundScheduler backgroundScheduler;
    private final TaskDao taskDao;

    @Inject
    TasksRepository(BackgroundScheduler backgroundScheduler, TaskDao taskDao) {
        this.backgroundScheduler = backgroundScheduler;
        this.taskDao = taskDao;
    }

    public Task getTask(String taskId) {
        return taskDao.findOne(taskId);
    }

    @Override
    public LiveResults<Task> getTasksWithChanges() {
        return taskDao.findAllWithChanges((database, table) -> QueryBuilder.of(table)
                .orderBy(TaskTable.$ENTRY_ID, QueryBuilder.Sort.ASC)
                .executeQuery(database)
        );
    }

    @Override
    public LiveResults<Task> getTaskWithChanges(@Nullable String taskId) {
        return taskDao.findAllWithChanges((database, table) -> {
            QueryBuilder queryBuilder = QueryBuilder.of(table);
            if(taskId == null) {
                queryBuilder.where("1 = 0"); // empty results
            } else {
                queryBuilder.where(TaskTable.$ENTRY_ID + " = ? ", taskId);
            }
            return queryBuilder.executeQuery(database);
        });
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        backgroundScheduler.execute(() -> {
            taskDao.insert(task);
        });
    }

    public void saveTasks(List<Task> tasks) {
        checkNotNull(tasks);
        backgroundScheduler.execute(() -> {
            taskDao.insert(tasks);
        });
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
        backgroundScheduler.execute(() -> {
            taskDao.insert(task.toBuilder().setCompleted(true).build());
        });
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        backgroundScheduler.execute(() -> {
            taskDao.insert(task.toBuilder().setCompleted(false).build());
        });
    }

    @Override
    public void clearCompletedTasks() {
        backgroundScheduler.execute(() -> {
            List<Task> list = taskDao.findAll(
                    (database, table) -> QueryBuilder.of(table).where(TaskTable.$COMPLETED + " LIKE ?", 1).executeQuery(database));
            taskDao.delete(list);
        });
    }

    @Override
    public void refreshTasks() {
        taskDao.refresh();
    }

    @Override
    public void deleteAllTasks() {
        backgroundScheduler.execute(() -> {
            taskDao.deleteAll();
        });
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        backgroundScheduler.execute(() -> {
            Task task = getTask(taskId);
            taskDao.delete(task);
        });
    }

    @Override
    public LiveResults<Task> getActiveTasksWithChanges() {
        return taskDao.findAllWithChanges((database, table) -> QueryBuilder.of(table)
                .where(TaskTable.$COMPLETED + " = ?", 0)
                .orderBy(TaskTable.$ENTRY_ID, QueryBuilder.Sort.ASC)
                .executeQuery(database));
    }

    @Override
    public LiveResults<Task> getCompletedTasksWithChanges() {
        return taskDao.findAllWithChanges((database, table) -> QueryBuilder.of(table)
                .where(TaskTable.$COMPLETED + " = ?", 1)
                .orderBy(TaskTable.$ENTRY_ID, QueryBuilder.Sort.ASC)
                .executeQuery(database));
    }
}