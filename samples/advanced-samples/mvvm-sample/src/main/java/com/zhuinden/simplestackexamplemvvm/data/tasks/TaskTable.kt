package com.zhuinden.simplestackexamplemvvm.data.tasks


import android.content.ContentValues
import android.database.Cursor
import com.zhuinden.simplestackexamplemvvm.core.database.BaseTable
import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager
import com.zhuinden.simplestackexamplemvvm.data.Task

/**
 * Created by Zhuinden on 2017.07.26..
 */
class TaskTable : BaseTable(), DatabaseManager.Mapper<Task> {
    override val fields: Array<DatabaseManager.Fields>
        get() = arrayOf(ENTRY_ID, TITLE, DESCRIPTION, COMPLETED)

    override fun getId(element: Any): String {
        return (element as Task).id!!
    }

    override val idFieldName: String
        get() = ENTRY_ID.fieldName

    override val tableName: String
        get() = TABLE_NAME

    override fun from(cursor: Cursor): Task {
        return Task(
            id = cursor.getString(cursor.getColumnIndexOrThrow(ENTRY_ID.fieldName)),
            title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE.fieldName)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION.fieldName)),
            completed = cursor.getInt(cursor.getColumnIndexOrThrow(COMPLETED.fieldName)) > 0
        )
    }

    override fun from(contentValues: ContentValues, task: Task): ContentValues {
        contentValues.put(ENTRY_ID.fieldName, task.id)
        contentValues.put(TITLE.fieldName, task.title)
        contentValues.put(DESCRIPTION.fieldName, task.description)
        contentValues.put(COMPLETED.fieldName, task.completed)
        return contentValues
    }

    companion object {
        const val TABLE_NAME = "task"
        val ENTRY_ID = DatabaseManager.Fields("entry_id", "text", "primary key")
        val TITLE = DatabaseManager.Fields("title", "text", "")
        val DESCRIPTION = DatabaseManager.Fields("description", "text", "")
        val COMPLETED = DatabaseManager.Fields("completed", "integer", "") // boolean

        val Q_ENTRY_ID = TABLE_NAME + "." + ENTRY_ID.fieldName
        val Q_TITLE = TABLE_NAME + "." + TITLE.fieldName
        val Q_DESCRIPTION = TABLE_NAME + "." + DESCRIPTION.fieldName
        val Q_COMPLETED = TABLE_NAME + "." + COMPLETED.fieldName
    }
}