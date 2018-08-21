package com.zhuinden.simplestackdemoexamplemvp.presentation.mapper

import com.zhuinden.simplestackdemoexamplemvp.data.entity.DbTask
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task

import javax.inject.Inject
import javax.inject.Singleton


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
