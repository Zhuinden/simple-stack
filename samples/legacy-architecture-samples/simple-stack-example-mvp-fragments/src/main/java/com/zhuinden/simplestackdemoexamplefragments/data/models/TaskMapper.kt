package com.zhuinden.simplestackdemoexamplefragments.data.models

import com.zhuinden.simplestackdemoexamplefragments.data.entity.DbTask

/**
 * Created by Zhuinden on 2018. 08. 20.
 */
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
