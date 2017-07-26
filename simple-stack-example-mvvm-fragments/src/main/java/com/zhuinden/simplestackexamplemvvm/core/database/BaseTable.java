package com.zhuinden.simplestackexamplemvvm.core.database;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by Zhuinden on 2017.07.26..
 */

public abstract class BaseTable
        implements DatabaseManager.Table {
    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE ");
        stringBuilder.append(getTableName());
        stringBuilder.append("(");
        DatabaseManager.Fields[] fields = getFields();
        int size = fields.length;
        int i = 0;
        for(DatabaseManager.Fields field : fields) {
            stringBuilder.append(field.getFieldName());
            stringBuilder.append(" ");
            stringBuilder.append(field.getFieldType());
            stringBuilder.append(" ");
            if(field.getFieldAdditional() != null) {
                stringBuilder.append(field.getFieldAdditional());
            }
            if(i < size - 1) {
                stringBuilder.append(",");
            }
            i++;
        }
        stringBuilder.append(");");
        database.execSQL(stringBuilder.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w("Table[" + getTableName() + "]",
                "Upgrading from version [" + oldVersion + "] to [" + newVersion + "], drop & recreate");
        database.execSQL("DROP TABLE IF EXISTS " + getTableName());
        onCreate(database);
    }

    @Override
    public String[] getAllQueryFields() {
        return extractFieldsFromTable(this);
    }

    @NonNull
    private String[] extractFieldsFromTable(DatabaseManager.Table table) {
        DatabaseManager.Fields[] _fields = table.getFields();
        String[] fields = new String[_fields.length];
        int i = 0;
        for(DatabaseManager.Fields field : _fields) {
            fields[i++] = getTableName() + "." + field.getFieldName();
        }
        return fields;
    }

    @Override
    public final String toString() {
        return getTableName();
    }
}
