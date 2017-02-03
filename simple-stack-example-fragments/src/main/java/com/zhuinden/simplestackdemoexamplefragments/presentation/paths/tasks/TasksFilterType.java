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

package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks;


import com.zhuinden.simplestackdemoexamplefragments.R;
import com.zhuinden.simplestackdemoexamplefragments.data.repository.TaskRepository;
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task;

import java.util.List;

import rx.Observable;

/**
 * Used with the filter spinner in the tasks list.
 */
public enum TasksFilterType {
    /**
     * Do not filter tasks.
     */
    ALL_TASKS {
        @Override
        public Observable<List<Task>> filterTask(TaskRepository taskRepository) {
            return taskRepository.getTasks();
        }

        @Override
        public int getFilterText() {
            return R.string.label_all;
        }

        @Override
        public void showEmptyViews(TasksFragment tasksFragment) {
            tasksFragment.showNoTasks();
        }
    },

    /**
     * Filters only the active (not completed yet) tasks.
     */
    ACTIVE_TASKS {
        @Override
        public Observable<List<Task>> filterTask(TaskRepository taskRepository) {
            return taskRepository.getActiveTasks();
        }

        @Override
        public int getFilterText() {
            return R.string.label_active;
        }

        @Override
        public void showEmptyViews(TasksFragment tasksFragment) {
            tasksFragment.showNoActiveTasks();
        }
    },

    /**
     * Filters only the completed tasks.
     */
    COMPLETED_TASKS {
        @Override
        public Observable<List<Task>> filterTask(TaskRepository taskRepository) {
            return taskRepository.getCompletedTasks();
        }

        @Override
        public int getFilterText() {
            return R.string.label_completed;
        }

        @Override
        public void showEmptyViews(TasksFragment tasksFragment) {
            tasksFragment.showNoCompletedTasks();
        }
    };

    public abstract Observable<List<Task>> filterTask(TaskRepository taskRepository);

    public abstract int getFilterText();

    public abstract void showEmptyViews(TasksFragment tasksFragment);
}
