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

package com.zhuinden.simplestackdemoexamplemvp.features.tasks


import android.os.Parcelable
import com.zhuinden.simplestackdemoexamplemvp.R
import com.zhuinden.simplestackdemoexamplemvp.data.models.Task
import com.zhuinden.simplestackdemoexamplemvp.data.repository.TaskRepository
import io.reactivex.Observable
import kotlinx.android.parcel.Parcelize

/**
 * Used with the filter spinner in the tasks list.
 */
sealed class TasksFilterType: Parcelable {
    /**
     * Do not filter tasks.
     */
    @Parcelize
    data class AllTasks(val placeholder: String = ""): TasksFilterType() {
        constructor() : this("")

        override val filterText: Int
            get() = R.string.label_all

        override fun filterTask(taskRepository: TaskRepository): Observable<List<Task>> =
            taskRepository.tasksWithChanges

        override fun showEmptyViews(tasksView: TasksView) {
            tasksView.showNoTasks()
        }
    }

    /**
     * Filters only the active (not completed yet) tasks.
     */
    @Parcelize
    data class ActiveTasks(val placeholder: String = ""): TasksFilterType() {
        constructor() : this("")

        override val filterText: Int
            get() = R.string.label_active

        override fun filterTask(taskRepository: TaskRepository): Observable<List<Task>> =
            taskRepository.activeTasksWithChanges


        override fun showEmptyViews(tasksView: TasksView) {
            tasksView.showNoActiveTasks()
        }
    }

    /**
     * Filters only the completed tasks.
     */
    @Parcelize
    data class CompletedTasks(val placeholder: String = ""): TasksFilterType() {
        constructor() : this("")

        override val filterText: Int
            get() = R.string.label_completed

        override fun filterTask(taskRepository: TaskRepository): Observable<List<Task>> =
            taskRepository.completedTasksWithChanges

        override fun showEmptyViews(tasksView: TasksView) {
            tasksView.showNoCompletedTasks()
        }
    }

    abstract val filterText: Int

    abstract fun filterTask(taskRepository: TaskRepository): Observable<List<Task>>

    abstract fun showEmptyViews(tasksView: TasksView)
}

