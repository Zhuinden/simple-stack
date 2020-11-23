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
package com.zhuinden.simplestackexamplemvvm.data.source


import android.database.sqlite.SQLiteDatabase
import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager
import com.zhuinden.simplestackexamplemvvm.core.database.LiveResults
import com.zhuinden.simplestackexamplemvvm.core.database.QueryBuilder
import com.zhuinden.simplestackexamplemvvm.core.scheduler.BackgroundScheduler
import com.zhuinden.simplestackexamplemvvm.data.Task
import com.zhuinden.simplestackexamplemvvm.data.dao.TaskDao
import com.zhuinden.simplestackexamplemvvm.data.tables.TaskTable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
class TasksDataSource @Inject constructor(
    private val backgroundScheduler: BackgroundScheduler,
    private val taskDao: TaskDao
) {
    fun getTask(taskId: String): Task? {
        return taskDao.findOne(taskId)
    }

    val tasksWithChanges: LiveResults<Task>
        get() = taskDao.findAllWithChanges { database: SQLiteDatabase, table: DatabaseManager.Table ->
            QueryBuilder.of(table)
                .orderBy(TaskTable.Q_ENTRY_ID, QueryBuilder.Sort.ASC)
                .executeQuery(database)
        }

    fun getTaskWithChanges(taskId: String?): LiveResults<Task> {
        return taskDao.findAllWithChanges { database: SQLiteDatabase, table: DatabaseManager.Table ->
            val queryBuilder: QueryBuilder = QueryBuilder.of(table)
            if (taskId == null) {
                queryBuilder.where("1 = 0") // empty results
            } else {
                queryBuilder.where(TaskTable.Q_ENTRY_ID + " = ? ", taskId)
            }
            queryBuilder.executeQuery(database)
        }
    }

    fun saveTask(task: Task) {
        backgroundScheduler.execute { taskDao.insert(task) }
    }

    fun saveTasks(tasks: List<Task>) {
        backgroundScheduler.execute { taskDao.insert(tasks) }
    }

    fun completeTask(task: Task) {
        backgroundScheduler.execute {
            taskDao.insert(task.copy(completed = true))
        }
    }

    fun activateTask(task: Task) {
        backgroundScheduler.execute {
            taskDao.insert(task.copy(completed = false))
        }
    }

    fun clearCompletedTasks() {
        backgroundScheduler.execute {
            val list = taskDao.findAll { database: SQLiteDatabase, table: DatabaseManager.Table ->
                QueryBuilder.of(table).where(TaskTable.Q_COMPLETED + " LIKE ?", 1).executeQuery(database)
            }
            taskDao.deleteList(list)
        }
    }

    fun refreshTasks() {
        taskDao.refresh()
    }

    fun deleteAllTasks() {
        backgroundScheduler.execute { taskDao.deleteAll() }
    }

    fun deleteTask(taskId: String) {
        backgroundScheduler.execute {
            val task = getTask(taskId)
            if (task != null) {
                taskDao.delete(task)
            }
        }
    }

    val activeTasksWithChanges: LiveResults<Task>
        get() = taskDao.findAllWithChanges { database: SQLiteDatabase, table: DatabaseManager.Table ->
            QueryBuilder.of(table)
                .where(TaskTable.Q_COMPLETED + " = ?", 0)
                .orderBy(TaskTable.Q_ENTRY_ID, QueryBuilder.Sort.ASC)
                .executeQuery(database)
        }

    val completedTasksWithChanges: LiveResults<Task>
        get() = taskDao.findAllWithChanges { database: SQLiteDatabase, table: DatabaseManager.Table ->
            QueryBuilder.of(table)
                .where(TaskTable.Q_COMPLETED + " = ?", 1)
                .orderBy(TaskTable.Q_ENTRY_ID, QueryBuilder.Sort.ASC)
                .executeQuery(database)
        }
}