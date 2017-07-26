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

package com.zhuinden.simplestackexamplemvvm.presentation.common;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;

import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.data.Task;
import com.zhuinden.simplestackexamplemvvm.data.source.TasksDataSource;
import com.zhuinden.simplestackexamplemvvm.data.source.TasksRepository;


/**
 * Abstract class for View Models that expose a single {@link Task}.
 */
public abstract class SingleTaskViewModel
        extends BaseObservable
        implements TasksDataSource.GetTaskCallback {
    public final ObservableField<String> snackbarText = new ObservableField<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> description = new ObservableField<>();
    final ObservableField<Task> taskObservable = new ObservableField<>();

    private final TasksRepository tasksRepository;
    private final Context context;

    private boolean isDataLoading;

    public SingleTaskViewModel(Context context, TasksRepository tasksRepository) {
        this.context = context.getApplicationContext(); // Force use of Application Context.
        this.tasksRepository = tasksRepository;

        // Exposed observables depend on the taskObservable observable:
        taskObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                Task task = taskObservable.get();
                if(task != null) {
                    title.set(task.getTitle());
                    description.set(task.getDescription());
                } else {
                    title.set(SingleTaskViewModel.this.context.getString(R.string.no_data));
                    description.set(SingleTaskViewModel.this.context.getString(R.string.no_data_description));
                }
            }
        });
    }

    public void start(String taskId) {
        if(taskId != null) {
            isDataLoading = true;
            tasksRepository.getTask(taskId, this);
        }
    }

    public void setTask(Task task) {
        taskObservable.set(task);
    }

    // "completed" is two-way bound, so in order to intercept the new value, use a @Bindable
    // annotation and process it in the setter.
    @Bindable
    public boolean getCompleted() {
        return taskObservable.get().isCompleted();
    }

    public void checkBoxClicked() {
        setTaskCompleted(!getCompleted());
    }

    private void setTaskCompleted(boolean completed) {
        if(isDataLoading) {
            return;
        }
        Task task = taskObservable.get();
        // Update the entity
        task.setCompleted(completed);

        // Notify repository and user
        if(completed) {
            tasksRepository.completeTask(task);
            snackbarText.set(context.getResources().getString(R.string.task_marked_complete));
        } else {
            tasksRepository.activateTask(task);
            snackbarText.set(context.getResources().getString(R.string.task_marked_active));
        }
        taskObservable.notifyChange();
    }

    @Bindable
    public boolean isDataAvailable() {
        return taskObservable.get() != null;
    }

    @Bindable
    public boolean isDataLoading() {
        return isDataLoading;
    }

    // This could be an observable, but we save a call to Task.getTitleForList() if not needed.
    @Bindable
    public String getTitleForList() {
        if(taskObservable.get() == null) {
            return "No data";
        }
        return taskObservable.get().getTitleForList();
    }

    @Override
    public void onTaskLoaded(Task task) {
        taskObservable.set(task);
        isDataLoading = false;
        notifyChange(); // For the @Bindable properties
    }

    @Override
    public void onDataNotAvailable() {
        taskObservable.set(null);
        isDataLoading = false;
    }

    public void deleteTask() {
        if(taskObservable.get() != null) {
            tasksRepository.deleteTask(taskObservable.get().getId());
        }
    }

    public void onRefresh() {
        if(taskObservable.get() != null) {
            start(taskObservable.get().getId());
        }
    }

    public String getSnackbarText() {
        return snackbarText.get();
    }

    @Nullable
    protected String getTaskId() {
        return taskObservable.get().getId();
    }
}
