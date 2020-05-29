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

package com.zhuinden.simplestackexamplemvvm.features.addedittask;

import androidx.lifecycle.Observer;
import android.content.res.Resources;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.core.database.LiveResults;
import com.zhuinden.simplestackexamplemvvm.data.Task;
import com.zhuinden.simplestackexamplemvvm.data.source.TasksRepository;
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksKey;
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksViewModel;
import com.zhuinden.simplestackexamplemvvm.util.MessageQueue;
import com.zhuinden.simplestackexamplemvvm.util.Strings;
import com.zhuinden.statebundle.StateBundle;

import java.util.List;

import javax.inject.Inject;

/**
 * ViewModel for the Add/Edit screen.
 * <p>
 * This ViewModel only exposes {@link ObservableField}s, so it doesn't need to extend
 * {@link androidx.databinding.BaseObservable} and updates are notified automatically. See
 * {@link com.zhuinden.simplestackexamplemvvm.features.statistics.StatisticsViewModel} for
 * how to deal with more complex scenarios.
 */
public class AddEditTaskViewModel
        implements Bundleable, Observer<List<Task>> {

    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> description = new ObservableField<>();
    public final ObservableBoolean dataLoading = new ObservableBoolean(false);
    public final ObservableField<String> snackbarText = new ObservableField<>();

    final ObservableField<Task> taskObservable = new ObservableField<>();

    LiveResults<Task> liveTask;

    private final Resources resources;
    private final TasksRepository tasksRepository;
    private final Backstack backstack;
    private final MessageQueue messageQueue;

    @Nullable
    private String taskId;

    boolean isDataLoaded;

    @Inject
    AddEditTaskViewModel(Resources resources, TasksRepository tasksRepository, Backstack backstack, MessageQueue messageQueue) {
        this.resources = resources;
        this.tasksRepository = tasksRepository;
        this.backstack = backstack;
        this.messageQueue = messageQueue;
    }

    public void start(@Nullable String taskId) {
        this.taskId = taskId;
        liveTask = tasksRepository.getTaskWithChanges(taskId);
        liveTask.observeForever(this);
    }

    public void stop() {
        liveTask.removeObserver(this);
    }

    public void onTaskLoaded(Task task) {
        if(Strings.isNullOrEmpty(title.get())) {
            title.set(task.title());
        }
        if(Strings.isNullOrEmpty(description.get())) {
            description.set(task.description());
        }
        taskObservable.set(task);
        dataLoading.set(false);
        isDataLoaded = true;
        // Note that there's no need to notify that the values changed because we're using
        // ObservableFields.
    }

    public void onDataNotAvailable() {
        dataLoading.set(false);
    }

    // Called when clicking on fab.
    public void saveTask() {
        if(taskId == null) {
            createTask(title.get(), description.get());
        } else {
            updateTask(title.get(), description.get());
        }
    }

    @Nullable
    public String getSnackbarText() {
        return snackbarText.get();
    }

    private void createTask(String title, String description) {
        Task newTask = Task.createNewActiveTask(title, description);
        if(newTask.isEmpty()) {
            snackbarText.set(resources.getString(R.string.empty_task_message));
        } else {
            tasksRepository.saveTask(newTask);
            messageQueue.pushMessageTo(TasksKey.create(), new TasksViewModel.AddedTaskMessage());
            navigateOnTaskSaved();
        }
    }

    private void updateTask(String title, String description) {
        tasksRepository.saveTask(Task.createTaskWithId(title, description, taskId, taskObservable.get().completed()));
        messageQueue.pushMessageTo(TasksKey.create(), new TasksViewModel.SavedTaskMessage());
        navigateOnTaskSaved(); // After an edit, go back to the list.
    }

    private void navigateOnTaskSaved() {
        backstack.setHistory(History.single(TasksKey.create()), StateChange.BACKWARD);
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

    @Override
    public void onChanged(@Nullable List<Task> tasks) {
        if(tasks == null) {
            return; // loading...
        }
        if(tasks.isEmpty()) {
            onDataNotAvailable();
        } else {
            onTaskLoaded(tasks.get(0));
        }
        isDataLoaded = true;
    }
}
