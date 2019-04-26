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

package com.zhuinden.simplestackexamplemvvm.features.taskdetail;

import android.content.Context;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestackexamplemvvm.data.source.TasksRepository;
import com.zhuinden.simplestackexamplemvvm.features.addedittask.AddEditTaskKey;
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksFragment;
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksViewModel;
import com.zhuinden.simplestackexamplemvvm.util.MessageQueue;
import com.zhuinden.simplestackexamplemvvm.util.SingleTaskViewModel;

import javax.inject.Inject;


/**
 * Listens to user actions from the list item in ({@link TasksFragment}) and redirects them to the
 * Fragment's actions listener.
 */
// UNSCOPED
public class TaskDetailViewModel
        extends SingleTaskViewModel {
    private final Backstack backstack;
    private final MessageQueue messageQueue;

    @Inject
    TaskDetailViewModel(Context context, TasksRepository tasksRepository, Backstack backstack, MessageQueue messageQueue) {
        super(context, tasksRepository);
        this.backstack = backstack;
        this.messageQueue = messageQueue;
    }

    /**
     * Can be called by the Data Binding Library or the delete menu item.
     */
    public void deleteTask() {
        super.deleteTask();
        messageQueue.pushMessageTo(backstack.root(), new TasksViewModel.DeletedTaskMessage());
        backstack.jumpToRoot();
    }

    public void startEditTask() {
        backstack.goTo(AddEditTaskKey.create(getTaskId()));
    }
}
