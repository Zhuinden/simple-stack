package com.zhuinden.simplestackexamplemvvm.features.statistics

import com.zhuinden.simplestackexamplemvvm.data.source.TasksDataSource

class StatisticsViewModel(tasksDataSource: TasksDataSource) {
    val activeTasks = tasksDataSource.activeTasksWithChanges
    val completedTasks = tasksDataSource.completedTasksWithChanges
}