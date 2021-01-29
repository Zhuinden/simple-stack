package com.zhuinden.simplestackexamplemvvm.data.tasks


import android.database.sqlite.SQLiteDatabase
import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager
import com.zhuinden.simplestackexamplemvvm.core.database.LiveResults
import com.zhuinden.simplestackexamplemvvm.core.database.QueryBuilder
import com.zhuinden.simplestackexamplemvvm.core.scheduler.BackgroundScheduler
import com.zhuinden.simplestackexamplemvvm.data.Task

class TasksDataSource(
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