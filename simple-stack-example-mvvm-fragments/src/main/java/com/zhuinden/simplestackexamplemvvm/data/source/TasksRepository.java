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

import com.zhuinden.simplestackexamplemvvm.data.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.zhuinden.simplestackexamplemvvm.util.Preconditions.checkNotNull;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
@Singleton
public class TasksRepository
        implements TasksDataSource {

    private final TasksDataSource tasksLocalDataSource;
    private final TasksDataSource tasksRemoteDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Task> cachedTask;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean cacheIsDirty = false;

    // Prevent direct instantiation.
    @Inject
    TasksRepository(@NonNull @Named("REMOTE") TasksDataSource tasksRemoteDataSource, @NonNull @Named("LOCAL") TasksDataSource tasksLocalDataSource) {
        this.tasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        this.tasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadTasksCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if(cachedTask != null && !cacheIsDirty) {
            callback.onTasksLoaded(new ArrayList<>(cachedTask.values()));
            return;
        }

        if(cacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getTasksFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            tasksLocalDataSource.getTasks(new LoadTasksCallback() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    refreshCache(tasks);
                    callback.onTasksLoaded(new ArrayList<>(cachedTask.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        tasksRemoteDataSource.saveTask(task);
        tasksLocalDataSource.saveTask(task);

        // Do in memory cache update to keep the app UI up to date
        if(cachedTask == null) {
            cachedTask = new LinkedHashMap<>();
        }
        cachedTask.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
        tasksRemoteDataSource.completeTask(task);
        tasksLocalDataSource.completeTask(task);

        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);

        // Do in memory cache update to keep the app UI up to date
        if(cachedTask == null) {
            cachedTask = new LinkedHashMap<>();
        }
        cachedTask.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        completeTask(getTaskWithId(taskId));
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        tasksRemoteDataSource.activateTask(task);
        tasksLocalDataSource.activateTask(task);

        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());

        // Do in memory cache update to keep the app UI up to date
        if(cachedTask == null) {
            cachedTask = new LinkedHashMap<>();
        }
        cachedTask.put(task.getId(), activeTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        checkNotNull(taskId);
        activateTask(getTaskWithId(taskId));
    }

    @Override
    public void clearCompletedTasks() {
        tasksRemoteDataSource.clearCompletedTasks();
        tasksLocalDataSource.clearCompletedTasks();

        // Do in memory cache update to keep the app UI up to date
        if(cachedTask == null) {
            cachedTask = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Task>> it = cachedTask.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if(entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     * <p>
     * Note: {@link GetTaskCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
     */
    @Override
    public void getTask(@NonNull final String taskId, @NonNull final GetTaskCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);

        Task cachedTask = getTaskWithId(taskId);

        // Respond immediately with cache if available
        if(cachedTask != null) {
            callback.onTaskLoaded(cachedTask);
            return;
        }

        // Load from server/persisted if needed.

        // Is the task in the local data source? If not, query the network.
        tasksLocalDataSource.getTask(taskId, new GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                // Do in memory cache update to keep the app UI up to date
                if(TasksRepository.this.cachedTask == null) {
                    TasksRepository.this.cachedTask = new LinkedHashMap<>();
                }
                TasksRepository.this.cachedTask.put(task.getId(), task);
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                tasksRemoteDataSource.getTask(taskId, new GetTaskCallback() {
                    @Override
                    public void onTaskLoaded(Task task) {
                        // Do in memory cache update to keep the app UI up to date
                        if(TasksRepository.this.cachedTask == null) {
                            TasksRepository.this.cachedTask = new LinkedHashMap<>();
                        }
                        TasksRepository.this.cachedTask.put(task.getId(), task);
                        callback.onTaskLoaded(task);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void refreshTasks() {
        cacheIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        tasksRemoteDataSource.deleteAllTasks();
        tasksLocalDataSource.deleteAllTasks();

        if(cachedTask == null) {
            cachedTask = new LinkedHashMap<>();
        }
        cachedTask.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        tasksRemoteDataSource.deleteTask(checkNotNull(taskId));
        tasksLocalDataSource.deleteTask(checkNotNull(taskId));

        cachedTask.remove(taskId);
    }

    private void getTasksFromRemoteDataSource(@NonNull final LoadTasksCallback callback) {
        tasksRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                callback.onTasksLoaded(new ArrayList<>(cachedTask.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Task> tasks) {
        if(cachedTask == null) {
            cachedTask = new LinkedHashMap<>();
        }
        cachedTask.clear();
        for(Task task : tasks) {
            cachedTask.put(task.getId(), task);
        }
        cacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Task> tasks) {
        tasksLocalDataSource.deleteAllTasks();
        for(Task task : tasks) {
            tasksLocalDataSource.saveTask(task);
        }
    }

    @Nullable
    private Task getTaskWithId(@NonNull String id) {
        checkNotNull(id);
        if(cachedTask == null || cachedTask.isEmpty()) {
            return null;
        } else {
            return cachedTask.get(id);
        }
    }
}
