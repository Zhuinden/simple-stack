package com.zhuinden.simplestackexamplemvvm.data.tasks

import com.zhuinden.simplestackexamplemvvm.core.database.BaseDao
import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager
import com.zhuinden.simplestackexamplemvvm.data.Task

class TaskDao(
    databaseManager: DatabaseManager,
    table: TaskTable
) : BaseDao<Task>(databaseManager, table, table)