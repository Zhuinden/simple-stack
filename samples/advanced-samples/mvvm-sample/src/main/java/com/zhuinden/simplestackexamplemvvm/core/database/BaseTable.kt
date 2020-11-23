/*
 * Copyright 2017 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.simplestackexamplemvvm.core.database


import android.database.sqlite.SQLiteDatabase
import android.util.Log

/**
 * Created by Zhuinden on 2017.07.26..
 */
abstract class BaseTable : DatabaseManager.Table {
    override fun onCreate(database: SQLiteDatabase) {
        val stringBuilder = StringBuilder()
        stringBuilder.append("CREATE TABLE ")
        stringBuilder.append(tableName)
        stringBuilder.append("(")
        val fields = fields
        val size = fields.size
        var i = 0
        for (field in fields) {
            stringBuilder.append(field.fieldName)
            stringBuilder.append(" ")
            stringBuilder.append(field.fieldType)
            stringBuilder.append(" ")
            if (field.fieldAdditional != null) {
                stringBuilder.append(field.fieldAdditional)
            }
            if (i < size - 1) {
                stringBuilder.append(",")
            }
            i++
        }
        stringBuilder.append(");")
        database.execSQL(stringBuilder.toString())
        buildIndexes(database)
    }

    override val indexedFields: List<List<DatabaseManager.Fields>>
        get() = emptyList()

    private fun buildIndexes(database: SQLiteDatabase) {
        for (indexedFields in indexedFields) {
            val indexBuilder = StringBuilder()
            indexBuilder.append("CREATE INDEX index_")
            indexBuilder.append(tableName)
            indexBuilder.append("_")
            run {
                var i = 0
                val size = indexedFields.size
                while (i < size) {
                    val indexedField = indexedFields[i]
                    indexBuilder.append(indexedField.fieldName)
                    if (i < size - 1) {
                        indexBuilder.append("_")
                    }
                    i++
                }
            }
            indexBuilder.append(" ON ")
            indexBuilder.append(tableName)
            indexBuilder.append(" (")
            var i = 0
            val size = indexedFields.size
            while (i < size) {
                val indexedField = indexedFields[i]
                indexBuilder.append(indexedField.fieldName)
                if (i < size - 1) {
                    indexBuilder.append(", ")
                }
                i++
            }
            indexBuilder.append(");")
            val indexSql = indexBuilder.toString()
            database.execSQL(indexSql)
        }
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.w("Table[$tableName]",
            "Upgrading from version [$oldVersion] to [$newVersion], drop & recreate")
        database.execSQL("DROP TABLE IF EXISTS $tableName")
        onCreate(database)
    }

    override val allQueryFields: Array<String>
        get() = extractFieldsFromTable(this)

    private fun extractFieldsFromTable(table: DatabaseManager.Table): Array<String> {
        val _fields = table.fields
        val fields = arrayOfNulls<String>(_fields.size)
        var i = 0
        for (field in _fields) {
            fields[i++] = tableName + "." + field.fieldName
        }
        return fields.mapNotNull { it }.toTypedArray()
    }

    override fun toString(): String {
        return tableName
    }
}