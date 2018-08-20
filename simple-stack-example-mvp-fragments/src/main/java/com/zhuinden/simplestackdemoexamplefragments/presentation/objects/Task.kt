package com.zhuinden.simplestackdemoexamplefragments.presentation.objects

/**
 * Created by Owner on 2017. 01. 25..
 */


import com.zhuinden.simplestackdemoexamplefragments.util.Strings
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
        get() = if (!Strings.isNullOrEmpty(title)) {
            title
        } else {
            description
        }

    val isCompleted: Boolean
        get() = completed

    val isActive: Boolean
        get() = !completed

    val isEmpty: Boolean
        get() = Strings.isNullOrEmpty(title) && Strings.isNullOrEmpty(description)

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
