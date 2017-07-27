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

package com.zhuinden.simplestackexamplemvvm.presentation.paths.tasks;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestackexamplemvvm.BR;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.data.Task;
import com.zhuinden.simplestackexamplemvvm.data.source.TasksDataSource;
import com.zhuinden.simplestackexamplemvvm.data.source.TasksRepository;
import com.zhuinden.simplestackexamplemvvm.presentation.paths.addedittask.AddEditTaskKey;
import com.zhuinden.simplestackexamplemvvm.util.EspressoIdlingResource;
import com.zhuinden.statebundle.StateBundle;

import java.util.ArrayList;
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
        implements Bundleable {
    // These observable fields will update Views automatically
    public final ObservableList<Task> items = new ObservableArrayList<>();
    public final ObservableBoolean dataLoading = new ObservableBoolean(false);
    public final ObservableField<String> currentFilteringLabel = new ObservableField<>();
    public final ObservableField<String> noTasksLabel = new ObservableField<>();
    public final ObservableField<Drawable> noTaskIconRes = new ObservableField<>();
    public final ObservableBoolean tasksAddViewVisible = new ObservableBoolean();
    final ObservableField<String> snackbarText = new ObservableField<>();
    private final ObservableBoolean isDataLoadingError = new ObservableBoolean(false);

    private TasksFilterType selectedFilter = TasksFilterType.ALL_TASKS;
    private final Resources resources;
    private final TasksRepository tasksRepository;
    private final Backstack backstack;

    @Inject
    public TasksViewModel(TasksRepository tasksRepository, Resources resources, Backstack backstack) {
        this.resources = resources;
        this.tasksRepository = tasksRepository;
        this.backstack = backstack;
        // Set initial state
        setFiltering(TasksFilterType.ALL_TASKS);
    }

    public void start() {
        loadTasks(false);
    }

    @Bindable
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void loadTasks(boolean forceUpdate) {
        loadTasks(forceUpdate, true);
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
        loadTasks(false, false);
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

    // TODO
    private void handleActivityResult(int requestCode, int resultCode) {
//        if(AddEditTaskActivity.REQUEST_CODE == requestCode) {
//            switch(resultCode) {
//                case TaskDetailActivity.EDIT_RESULT_OK:
//                    snackbarText.set(resources.getString(R.string.successfully_saved_task_message));
//                    break;
//                case AddEditTaskActivity.ADD_EDIT_RESULT_OK:
//                    snackbarText.set(resources.getString(R.string.successfully_added_task_message));
//                    break;
//                case TaskDetailActivity.DELETE_RESULT_OK:
//                    snackbarText.set(resources.getString(R.string.successfully_deleted_task_message));
//                    break;
//            }
//        }
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link TasksDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if(showLoadingUI) {
            dataLoading.set(true);
        }
        if(forceUpdate) {
            tasksRepository.refreshTasks();
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        tasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                List<Task> tasksToShow = new ArrayList<>();

                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if(!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement(); // Set app as idle.
                }

                // We filter the tasks based on the requestType
                for(Task task : tasks) {
                    switch(selectedFilter) {
                        case ALL_TASKS:
                            tasksToShow.add(task);
                            break;
                        case ACTIVE_TASKS:
                            if(task.isActive()) {
                                tasksToShow.add(task);
                            }
                            break;
                        case COMPLETED_TASKS:
                            if(task.isCompleted()) {
                                tasksToShow.add(task);
                            }
                            break;
                        default:
                            tasksToShow.add(task);
                            break;
                    }
                }
                if(showLoadingUI) {
                    dataLoading.set(false);
                }
                isDataLoadingError.set(false);

                items.clear();
                items.addAll(tasksToShow);
                notifyPropertyChanged(BR.empty); // It's a @Bindable so update manually
            }

            @Override
            public void onDataNotAvailable() {
                isDataLoadingError.set(true);
            }
        });
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
}
