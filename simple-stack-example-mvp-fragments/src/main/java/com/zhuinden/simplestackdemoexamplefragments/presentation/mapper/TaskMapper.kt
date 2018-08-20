package com.zhuinden.simplestackdemoexamplefragments.presentation.mapper

import com.zhuinden.simplestackdemoexamplefragments.data.entity.DbTask
import com.zhuinden.simplestackdemoexamplefragments.presentation.objects.Task

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Owner on 2017. 01. 26..
 */

@Singleton
class TaskMapper @Inject constructor() {
    fun fromRealm(dbTask: DbTask): Task =
        Task.createCompletedTaskWithId(dbTask.title ?: "", dbTask.description ?: "", dbTask.id!!, dbTask.completed)

    fun toRealm(task: Task): DbTask = DbTask().apply {
        id = task.id
        completed = task.completed
        description = task.description
        title = task.title
    }
}
