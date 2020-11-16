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
package com.zhuinden.simplestackexamplemvvm.data


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Immutable model class for a Task.
 */
@Parcelize
data class Task(
    val id: String?,
    val title: String?,
    val description: String?,
    val completed: Boolean,
) : Parcelable {
    val titleForList: String
        get() = if (!title.isNullOrEmpty()) {
            title
        } else {
            description
        } ?: ""

    val isCompleted: Boolean
        get() = completed
    val isActive: Boolean
        get() = !completed
    val isEmpty: Boolean
        get() = title.isNullOrEmpty() && description.isNullOrEmpty()

    companion object {
        fun createNewActiveTask(title: String?, description: String?): Task {
            return createTaskWithId(title, description, UUID.randomUUID().toString(), false)
        }

        fun createActiveTaskWithId(title: String?, description: String?, id: String?): Task {
            return createTaskWithId(title, description, id, false)
        }

        fun createTaskWithId(title: String?, description: String?, id: String?, completed: Boolean): Task {
            return Task(
                id = id,
                title = title,
                description = description,
                completed = completed
            )
        }
    }
}