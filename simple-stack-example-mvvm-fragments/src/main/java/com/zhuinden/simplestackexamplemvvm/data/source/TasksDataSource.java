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

/**
 * Main entry point for accessing tasks data.
 */
public interface TasksDataSource {
    LiveResults<Task> getTasksWithChanges();

    LiveResults<Task> getTaskWithChanges(@Nullable String taskId);

    void saveTask(@NonNull Task task);

    void completeTask(@NonNull Task task);

    void activateTask(@NonNull Task task);

    void clearCompletedTasks();

    void refreshTasks();

    void deleteAllTasks();

    void deleteTask(@NonNull String taskId);

    LiveResults<Task> getActiveTasksWithChanges();

    LiveResults<Task> getCompletedTasksWithChanges();
}
