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

package com.zhuinden.simplestackexamplemvvm.features.tasks;

import androidx.lifecycle.Observer;
import android.content.res.Resources;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestackexamplemvvm.BR;
import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.core.database.LiveResults;
import com.zhuinden.simplestackexamplemvvm.data.Task;
import com.zhuinden.simplestackexamplemvvm.data.source.TasksRepository;
import com.zhuinden.simplestackexamplemvvm.features.addedittask.AddEditTaskKey;
import com.zhuinden.simplestackexamplemvvm.util.MessageQueue;
import com.zhuinden.statebundle.StateBundle;

import java.util.List;

import javax.inject.Inject;

/**
 * Exposes the data to be used in the task list screen.
 * <p>
 * {@link BaseObservable} implements a listener registration mechanism which is notified when a
 * property changes. This is done by assigning a {@link Bindable} annotation to the property's
 * getter method.
 */
// UNSCOPED
public class TasksViewModel
        extends BaseObservable
        implements Bundleable, Observer<List<Task>>, MessageQueue.Receiver {
    // These observable fields will update Views automatically
    public final ObservableList<Task> items = new ObservableArrayList<>();
    public final ObservableBoolean dataLoading = new ObservableBoolean(false);
    public final ObservableField<String> currentFilteringLabel = new ObservableField<>();
    public final ObservableField<String> noTasksLabel = new ObservableField<>();
    public final ObservableField<Drawable> noTaskIconRes = new ObservableField<>();
    public final ObservableBoolean tasksAddViewVisible = new ObservableBoolean();
    final ObservableField<String> snackbarText = new ObservableField<>();
    private final ObservableBoolean isDataLoadingError = new ObservableBoolean(false);

    private LiveResults<Task> liveResults;

    private TasksFilterType selectedFilter = TasksFilterType.ALL_TASKS;
    private final Resources resources;
    private final TasksRepository tasksRepository;
    private final Backstack backstack;
    private final MessageQueue messageQueue;

    @Inject
    public TasksViewModel(TasksRepository tasksRepository, Resources resources, Backstack backstack, MessageQueue messageQueue) {
        this.resources = resources;
        this.tasksRepository = tasksRepository;
        this.backstack = backstack;
        this.messageQueue = messageQueue;

        tasksRepository.refreshTasks(); // force a download when this scope is created.

        // Set initial state
        setFiltering(TasksFilterType.ALL_TASKS);
    }

    public void start() {
        reloadTasks();
        messageQueue.requestMessages(TasksKey.create(), this);
    }

    public void stop() {
        liveResults.removeObserver(this);
    }

    @Bindable
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Sets the current task filtering type.
     *
     * @param requestType Can be {@link TasksFilterType#ALL_TASKS},
     *                    {@link TasksFilterType#COMPLETED_TASKS}, or
     *                    {@link TasksFilterType#ACTIVE_TASKS}
     */
    public void setFiltering(TasksFilterType requestType) {
        selectedFilter = requestType;

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        switch(requestType) {
            case ALL_TASKS:
                currentFilteringLabel.set(resources.getString(R.string.label_all));
                noTasksLabel.set(resources.getString(R.string.no_tasks_all));
                noTaskIconRes.set(resources.getDrawable(R.drawable.ic_assignment_turned_in_24dp));
                tasksAddViewVisible.set(true);
                break;
            case ACTIVE_TASKS:
                currentFilteringLabel.set(resources.getString(R.string.label_active));
                noTasksLabel.set(resources.getString(R.string.no_tasks_active));
                noTaskIconRes.set(resources.getDrawable(R.drawable.ic_check_circle_24dp));
                tasksAddViewVisible.set(false);
                break;
            case COMPLETED_TASKS:
                currentFilteringLabel.set(resources.getString(R.string.label_completed));
                noTasksLabel.set(resources.getString(R.string.no_tasks_completed));
                noTaskIconRes.set(resources.getDrawable(R.drawable.ic_verified_user_24dp));
                tasksAddViewVisible.set(false);
                break;
        }
    }

    public void clearCompletedTasks() {
        tasksRepository.clearCompletedTasks();
        snackbarText.set(resources.getString(R.string.completed_tasks_cleared));
    }

    public String getSnackbarText() {
        return snackbarText.get();
    }

    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    public void addNewTask() {
        backstack.goTo(AddEditTaskKey.create());
    }

    private LiveResults<Task> getFilteredResults() {
        switch(selectedFilter) {
            case ALL_TASKS:
                return tasksRepository.getTasksWithChanges();
            case ACTIVE_TASKS:
                return tasksRepository.getActiveTasksWithChanges();
            case COMPLETED_TASKS:
                return tasksRepository.getCompletedTasksWithChanges();
            default:
                throw new IllegalArgumentException("Invalid filter type [" + selectedFilter + "]");
        }
    }

    /**
     */
    public void reloadTasks() {
        if(liveResults != null) {
            liveResults.removeObserver(this);
        }
        dataLoading.set(true);
        liveResults = getFilteredResults();
        liveResults.observeForever(this);
    }

    @NonNull
    @Override
    public StateBundle toBundle() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putString("filterType", selectedFilter.name());
        return stateBundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            setFiltering(TasksFilterType.valueOf(bundle.getString("filterType")));
        }
    }

    @Override
    public void onChanged(@Nullable List<Task> tasks) {
        if(tasks == null) {
            return; // loading...
        }
        items.clear();
        items.addAll(tasks);
        notifyPropertyChanged(BR.empty); // It's a @Bindable so update manually
        dataLoading.set(false);
    }

    public void refresh() {
        tasksRepository.refreshTasks();
    }

    public static class SavedTaskMessage {
    }

    public static class AddedTaskMessage {
    }

    public static class DeletedTaskMessage {
    }

    @Override
    public void receiveMessage(Object message) {
        if(message instanceof DeletedTaskMessage) {
            snackbarText.set(resources.getString(R.string.successfully_deleted_task_message));
        } else if(message instanceof AddedTaskMessage) {
            snackbarText.set(resources.getString(R.string.successfully_added_task_message));
        } else if(message instanceof SavedTaskMessage) {
            snackbarText.set(resources.getString(R.string.successfully_saved_task_message));
        }
        snackbarText.set("");
    }
}
