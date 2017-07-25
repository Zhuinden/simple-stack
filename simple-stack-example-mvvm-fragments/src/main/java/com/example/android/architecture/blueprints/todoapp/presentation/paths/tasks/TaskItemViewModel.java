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

package com.example.android.architecture.blueprints.todoapp.presentation.paths.tasks;

import android.content.Context;
import android.support.annotation.Nullable;

import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository;
import com.example.android.architecture.blueprints.todoapp.presentation.common.SingleTaskViewModel;

import java.lang.ref.WeakReference;

import javax.inject.Inject;


/**
 * Listens to user actions from the list item in ({@link TasksFragment}) and redirects them to the
 * Fragment's actions listener.
 */
// UNSCOPED
public class TaskItemViewModel
        extends SingleTaskViewModel {

    // TODO: replace this with an @Inject-ed backstack
    // This navigator is s wrapped in a WeakReference to avoid leaks because it has references to an
    // activity. There's no straightforward way to clear it for each item in a list adapter.
    @Nullable
    private WeakReference<TaskItemNavigator> navigator;

    @Inject
    TaskItemViewModel(Context context, TasksRepository tasksRepository) {
        super(context, tasksRepository);
    }

    public void setNavigator(TaskItemNavigator navigator) {
        this.navigator = new WeakReference<>(navigator);
    }

    /**
     * Called by the Data Binding library when the row is clicked.
     */
    public void taskClicked() {
        String taskId = getTaskId();
        if(taskId == null) {
            // Click happened before task was loaded, no-op.
            return;
        }
        if(navigator != null && navigator.get() != null) {
            navigator.get().openTaskDetails(taskId);
        }
    }
}
