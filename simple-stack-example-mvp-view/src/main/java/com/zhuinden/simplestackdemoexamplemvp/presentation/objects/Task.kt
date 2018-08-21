package com.zhuinden.simplestackdemoexamplemvp.presentation.objects

import java.util.*

/**
 * Immutable model class for a Task.
 */
data class Task(
    val id: String,
    val title: String?,
    val description: String?,
    val completed: Boolean
) {
    val titleForList: String?
        get() = when {
            !title.isNullOrEmpty() -> title
            else -> description
        }

    val isCompleted: Boolean
        get() = completed

    val isActive: Boolean
        get() = !completed

    val isEmpty: Boolean
        get() = title.isNullOrEmpty() && description.isNullOrEmpty()

    companion object {
        @JvmStatic
        fun createNewActiveTask(title: String, description: String): Task =
            createCompletedTaskWithId(title, description, UUID.randomUUID().toString(), false)

        @JvmStatic
        fun createActiveTaskWithId(title: String, description: String, id: String): Task =
            createCompletedTaskWithId(title, description, id, false)

        @JvmStatic
        fun createCompletedTask(title: String, description: String, completed: Boolean): Task =
            createCompletedTaskWithId(title, description, UUID.randomUUID().toString(), completed)

        @JvmStatic
        fun createCompletedTaskWithId(title: String, description: String, id: String, completed: Boolean): Task =
            Task(id, title, description, completed)
    }
}
