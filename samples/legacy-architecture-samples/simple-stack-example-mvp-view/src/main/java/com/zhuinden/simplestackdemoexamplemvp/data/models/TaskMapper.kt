package com.zhuinden.simplestackdemoexamplemvp.data.models

import com.zhuinden.simplestackdemoexamplemvp.data.entity.DbTask

fun DbTask.fromRealm(): Task = let { dbTask ->
    Task.createCompletedTaskWithId(dbTask.title ?: "", dbTask.description
        ?: "", dbTask.id!!, dbTask.completed)
}

fun Task.toRealm(): DbTask = let { task ->
    DbTask().apply {
        id = task.id
        completed = task.completed
        description = task.description
        title = task.title
    }
}
