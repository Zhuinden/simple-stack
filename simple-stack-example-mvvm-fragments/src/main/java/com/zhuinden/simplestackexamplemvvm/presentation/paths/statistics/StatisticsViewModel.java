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

package com.zhuinden.simplestackexamplemvvm.presentation.paths.statistics;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.zhuinden.simplestackexamplemvvm.R;
import com.zhuinden.simplestackexamplemvvm.core.database.liveresults.LiveResults;
import com.zhuinden.simplestackexamplemvvm.data.Task;
import com.zhuinden.simplestackexamplemvvm.data.source.TasksRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Exposes the data to be used in the statistics screen.
 * <p>
 * This ViewModel uses both {@link ObservableField}s ({@link ObservableBoolean}s in this case) and
 * {@link Bindable} getters. The values in {@link ObservableField}s are used directly in the layout,
 * whereas the {@link Bindable} getters allow us to add some logic to it. This is
 * preferable to having logic in the XML layout.
 */
public class StatisticsViewModel
        extends BaseObservable
        implements Observer<List<Task>> {

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);
    final ObservableBoolean error = new ObservableBoolean(false);

    @VisibleForTesting
    int numberOfActiveTasks = 0;

    @VisibleForTesting
    int numberOfCompletedTasks = 0;

    private Context context;

    private LiveResults<Task> liveResults;

    private final TasksRepository tasksRepository;

    @Inject
    public StatisticsViewModel(Context context, TasksRepository tasksRepository) {
        this.context = context;
        this.tasksRepository = tasksRepository;
    }

    public void start() {
        loadStatistics();
    }

    public void stop() {
        liveResults.removeObserver(this);
    }

    private void loadStatistics() {
        dataLoading.set(true);
        liveResults = tasksRepository.getTasks();
        liveResults.observeForever(this);
    }

    /**
     * Returns a String showing the number of active tasks.
     */
    @Bindable
    public String getNumberOfActiveTasks() {
        return context.getString(R.string.statistics_active_tasks, numberOfActiveTasks);
    }

    /**
     * Returns a String showing the number of completed tasks.
     */
    @Bindable
    public String getNumberOfCompletedTasks() {
        return context.getString(R.string.statistics_completed_tasks, numberOfCompletedTasks);
    }

    /**
     * Controls whether the stats are shown or a "No data" message.
     */
    @Bindable
    public boolean isEmpty() {
        return numberOfActiveTasks + numberOfCompletedTasks == 0;
    }

    /**
     * Called when new data is ready.
     */
    private void computeStats(List<Task> tasks) {
        int completed = 0;
        int active = 0;

        for(Task task : tasks) {
            if(task.isCompleted()) {
                completed += 1;
            } else {
                active += 1;
            }
        }
        numberOfActiveTasks = active;
        numberOfCompletedTasks = completed;

        // There are multiple @Bindable fields in this ViewModel, calling notifyChange() will
        // update all the UI elements that depend on them.
        notifyChange();

        // To update just one of them and avoid unnecessary UI updates,
        // use notifyPropertyChanged(BR.field)

        // Observable fields don't need to be notified. set() will trigger an update.
        dataLoading.set(false);
        error.set(false);
    }

    @Override
    public void onChanged(@Nullable List<Task> tasks) {
        if(tasks != null) {
            computeStats(tasks);
        }
    }
}
