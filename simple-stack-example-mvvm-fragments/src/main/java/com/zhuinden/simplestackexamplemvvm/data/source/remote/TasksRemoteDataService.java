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

package com.zhuinden.simplestackexamplemvvm.data.source.remote;

import android.support.annotation.NonNull;

import com.zhuinden.simplestackexamplemvvm.core.scheduler.NetworkScheduler;
import com.zhuinden.simplestackexamplemvvm.data.Task;
import com.zhuinden.simplestackexamplemvvm.data.source.local.TasksLocalDataSource;
import com.zhuinden.simplestackexamplemvvm.util.Lists;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implementation of the data source that adds a latency simulating network.
 *
 * TODO: destroy garbage data layer code
 */
@Singleton
public class TasksRemoteDataService {
    private static final int SERVICE_LATENCY_IN_MILLIS = 2000;

    private final static Map<String, Task> TASKS_SERVICE_DATA;

    static {
        TASKS_SERVICE_DATA = Collections.synchronizedMap(new LinkedHashMap<>());
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.", "0");
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "1");
        addTask("Begin work on course", "It's been a week.", "2");
        addTask("Do something", "Found awesome girders at half the cost!", "3");
        addTask("Do something else", "Found awesome girders at half the cost!", "4");
        addTask("Do more stuff", "Found awesome girders at half the cost!", "5");
        addTask("Do that again", "Found awesome girders at half the cost!", "6");
        addTask("Do those things", "Found awesome girders at half the cost!", "7");
        addTask("Do all the things", "Found awesome girders at half the cost!", "8");
        addTask("Do the thing", "Found awesome girders at half the cost!", "12");
        addTask("Do that thing", "Found awesome girders at half the cost!", "13");
        addTask("Do it", "Found awesome girders at half the cost!", "14");
        addTask("Do that", "Found awesome girders at half the cost!", "15");
        addTask("Do this", "Found awesome girders at half the cost!", "16");
    }

    private final NetworkScheduler networkScheduler;
    private final TasksLocalDataSource tasksLocalDataSource;

    @Inject
    TasksRemoteDataService(NetworkScheduler networkScheduler, TasksLocalDataSource tasksLocalDataSource) {
        this.networkScheduler = networkScheduler;
        this.tasksLocalDataSource = tasksLocalDataSource;
    }

    private static void addTask(String title, String description, String id) {
        Task newTask = Task.createActiveTaskWithId(title, description, id);
        TASKS_SERVICE_DATA.put(newTask.id(), newTask);
    }

    private void simulateLatency() {
        try {
            Thread.sleep(SERVICE_LATENCY_IN_MILLIS); // <-- clearly this shouldn't be on the UI thread
        } catch(InterruptedException e) {
            // Ignored
        }
    }

    public void getTasks() {
        networkScheduler.execute(() -> {
            simulateLatency();
            tasksLocalDataSource.saveTasks(Lists.newArrayList(TASKS_SERVICE_DATA.values()));
        });
    }

    public void getTask(@NonNull String taskId) {
        networkScheduler.execute(() -> {
            final Task task = TASKS_SERVICE_DATA.get(taskId);
            simulateLatency();
            tasksLocalDataSource.saveTask(task);
        });
    }

    public void saveTask(@NonNull Task task) {
        networkScheduler.execute(() -> {
            simulateLatency();
            TASKS_SERVICE_DATA.put(task.id(), task);
        });
    }


    public void completeTask(@NonNull Task task) {
        networkScheduler.execute(() -> {
            simulateLatency();
            Task completedTask = Task.createTaskWithId(task.title(), task.description(), task.id(), true);
            TASKS_SERVICE_DATA.put(task.id(), completedTask);
        });
    }

    public void activateTask(@NonNull Task task) {
        networkScheduler.execute(() -> {
            simulateLatency();
            Task activeTask = Task.createActiveTaskWithId(task.title(), task.description(), task.id());
            TASKS_SERVICE_DATA.put(task.id(), activeTask);
        });
    }


    public void clearCompletedTasks() {
        networkScheduler.execute(() -> {
            simulateLatency();
            Iterator<Map.Entry<String, Task>> it = TASKS_SERVICE_DATA.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<String, Task> entry = it.next();
                if(entry.getValue().isCompleted()) {
                    it.remove();
                }
            }
        });
    }


    public void deleteAllTasks() {
        networkScheduler.execute(() -> {
            simulateLatency();
            TASKS_SERVICE_DATA.clear();
        });
    }


    public void deleteTask(@NonNull String taskId) {
        networkScheduler.execute(() -> {
            simulateLatency();
            TASKS_SERVICE_DATA.remove(taskId);
        });
    }
}
