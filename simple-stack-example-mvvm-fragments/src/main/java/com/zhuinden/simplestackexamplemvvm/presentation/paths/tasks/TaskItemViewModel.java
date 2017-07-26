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

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackexamplemvvm.data.source.TasksRepository;
import com.zhuinden.simplestackexamplemvvm.presentation.common.SingleTaskViewModel;
import com.zhuinden.simplestackexamplemvvm.presentation.paths.taskdetail.TaskDetailKey;

import javax.inject.Inject;


/**
 * Listens to user actions from the list item in ({@link TasksFragment}) and redirects them to the
 * Fragment's actions listener.
 */
// UNSCOPED
public class TaskItemViewModel
        extends SingleTaskViewModel {
    private final Backstack backstack;

    @Inject
    TaskItemViewModel(Context context, TasksRepository tasksRepository, Backstack backstack) {
        super(context, tasksRepository);
        this.backstack = backstack;
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
        backstack.goTo(TaskDetailKey.create(taskId));
    }
}
