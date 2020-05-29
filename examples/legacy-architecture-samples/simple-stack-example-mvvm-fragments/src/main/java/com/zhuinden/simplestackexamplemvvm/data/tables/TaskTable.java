package com.zhuinden.simplestackexamplemvvm.data.tables;

import android.content.ContentValues;
import android.database.Cursor;

import com.zhuinden.simplestackexamplemvvm.core.database.BaseTable;
import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager;
import com.zhuinden.simplestackexamplemvvm.data.Task;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Zhuinden on 2017.07.26..
 */

@Singleton
public class TaskTable
        extends BaseTable
        implements DatabaseManager.Mapper<Task> {
    public static final String NAME = "task";

    public static final DatabaseManager.Fields ENTRY_ID = new DatabaseManager.Fields("entry_id", "text", "primary key");
    public static final DatabaseManager.Fields TITLE = new DatabaseManager.Fields("title", "text", "");
    public static final DatabaseManager.Fields DESCRIPTION = new DatabaseManager.Fields("description", "text", "");
    // @formatter:off
    public static final DatabaseManager.Fields COMPLETED = new DatabaseManager.Fields("completed", "integer", ""); // boolean
    // @formatter:on

    public static final String $ENTRY_ID = NAME + "." + ENTRY_ID.getFieldName();
    public static final String $TITLE = NAME + "." + TITLE.getFieldName();
    public static final String $DESCRIPTION = NAME + "." + DESCRIPTION.getFieldName();
    public static final String $COMPLETED = NAME + "." + COMPLETED.getFieldName();

    @Inject
    public TaskTable() {
    }

    @Override
    public String getTableName() {
        return NAME;
    }

    @Override
    public DatabaseManager.Fields[] getFields() {
        return new DatabaseManager.Fields[]{ENTRY_ID, TITLE, DESCRIPTION, COMPLETED};
    }

    @Override
    public String getId(Object element) {
        return ((Task)element).id();
    }

    @Override
    public String getIdFieldName() {
        return ENTRY_ID.getFieldName();
    }

    @Override
    public Task from(Cursor cursor) {
        return Task.newBuilder()
                .setId(cursor.getString(cursor.getColumnIndexOrThrow(ENTRY_ID.getFieldName())))
                .setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE.getFieldName())))
                .setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION.getFieldName())))
                .setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COMPLETED.getFieldName())) > 0)
                .build();
    }

    @Override
    public ContentValues from(ContentValues contentValues, Task task) {
        contentValues.put(ENTRY_ID.getFieldName(), task.id());
        contentValues.put(TITLE.getFieldName(), task.title());
        contentValues.put(DESCRIPTION.getFieldName(), task.description());
        contentValues.put(COMPLETED.getFieldName(), task.completed());
        return contentValues;
    }
}
