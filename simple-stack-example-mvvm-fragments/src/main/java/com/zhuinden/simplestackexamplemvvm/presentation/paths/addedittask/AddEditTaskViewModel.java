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

package com.zhuinden.simplestackexamplemvvm.presentation.paths.addedittask;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.data.Task;
import com.zhuinden.simplestackexamplemvvm.data.source.TasksDataSource;
import com.zhuinden.simplestackexamplemvvm.data.source.TasksRepository;
import com.zhuinden.simplestackexamplemvvm.presentation.paths.tasks.TasksKey;
import com.zhuinden.statebundle.StateBundle;

import javax.inject.Inject;

/**
 * ViewModel for the Add/Edit screen.
 * <p>
 * This ViewModel only exposes {@link ObservableField}s, so it doesn't need to extend
 * {@link android.databinding.BaseObservable} and updates are notified automatically. See
 * {@link com.zhuinden.simplestackexamplemvvm.presentation.paths.statistics.StatisticsViewModel} for
 * how to deal with more complex scenarios.
 */
public class AddEditTaskViewModel
        implements TasksDataSource.GetTaskCallback, Bundleable {

    public final ObservableField<String> title = new ObservableField<>();

    public final ObservableField<String> description = new ObservableField<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> snackbarText = new ObservableField<>();

    private final Backstack backstack;

    private final TasksRepository tasksRepository;

    private final Context context;  // To avoid leaks, this must be an Application Context.

    @Nullable
    private String taskId;

    private boolean isNewTask;

    private boolean isDataLoaded = false;

    @Inject
    AddEditTaskViewModel(Context context, TasksRepository tasksRepository, Backstack backstack) {
        this.context = context.getApplicationContext(); // Force use of Application Context.
        this.tasksRepository = tasksRepository;
        this.backstack = backstack;
    }

    public void start(String taskId) {
        if(dataLoading.get()) {
            // Already loading, ignore.
            return;
        }
        this.taskId = taskId;
        if(taskId == null) {
            // No need to populate, it's a new task
            isNewTask = true;
            return;
        }
        if(isDataLoaded) {
            // No need to populate, already have data.
            return;
        }
        isNewTask = false;
        dataLoading.set(true);
        tasksRepository.getTask(taskId, this);
    }

    @Override
    public void onTaskLoaded(Task task) {
        title.set(task.title());
        description.set(task.description());
        dataLoading.set(false);
        isDataLoaded = true;

        // Note that there's no need to notify that the values changed because we're using
        // ObservableFields.
    }

    @Override
    public void onDataNotAvailable() {
        dataLoading.set(false);
    }

    // Called when clicking on fab.
    public void saveTask() {
        if(isNewTask()) {
            createTask(title.get(), description.get());
        } else {
            updateTask(title.get(), description.get());
        }
    }

    @Nullable
    public String getSnackbarText() {
        return snackbarText.get();
    }

    private boolean isNewTask() {
        return isNewTask;
    }

    private void createTask(String title, String description) {
        Task newTask = Task.createNewActiveTask(title, description);
        if(newTask.isEmpty()) {
            snackbarText.set(context.getString(R.string.empty_task_message));
        } else {
            tasksRepository.saveTask(newTask);
            navigateOnTaskSaved();
        }
    }

    private void updateTask(String title, String description) {
        if(isNewTask()) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        tasksRepository.saveTask(Task.createActiveTaskWithId(title, description, taskId));
        navigateOnTaskSaved(); // After an edit, go back to the list.
    }

    private void navigateOnTaskSaved() {
        backstack.setHistory(HistoryBuilder.single(TasksKey.create()), StateChange.BACKWARD);
    }

    @NonNull
    @Override
    public StateBundle toBundle() {
        StateBundle bundle = new StateBundle();
        bundle.putString("title", title.get());
        bundle.putString("description", description.get());
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            title.set(bundle.getString("title"));
            description.set(bundle.getString("description"));
            isDataLoaded = true;
        }
    }
}
