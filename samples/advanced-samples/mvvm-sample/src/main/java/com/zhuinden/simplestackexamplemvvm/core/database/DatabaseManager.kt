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


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.zhuinden.simplestackexamplemvvm.core.scheduler.BackgroundScheduler
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseManager @Inject constructor(
    appContext: Context,
    private val tables: List<@JvmSuppressWildcards Table>,
    private val backgroundScheduler: BackgroundScheduler
) : SQLiteOpenHelper(appContext, DATABASE_NAME, null, DATABASE_VERSION) {
    interface Table {
        fun getId(element: Any): String
        val idFieldName: String
        val tableName: String
        val fields: Array<Fields>
        val allQueryFields: Array<String>
        val indexedFields: List<List<Fields>>
        fun onCreate(database: SQLiteDatabase)
        fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    }

    class Fields(
        val fieldName: String,
        val fieldType: String,
        val fieldAdditional: String
    ) {
        override fun toString(): String = fieldName
    }

    fun interface QueryDefinition {
        fun query(database: SQLiteDatabase, table: Table): Cursor
    }

    val database: SQLiteDatabase = writableDatabase

    override fun onCreate(database: SQLiteDatabase) {
        for (table in tables) {
            table.onCreate(database)
        }
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        for (table in tables) {
            table.onUpgrade(database, oldVersion, newVersion)
        }
    }

    fun interface Transaction {
        fun execute(sqLiteDatabase: SQLiteDatabase)
    }

    interface Mapper<T> {
        fun from(cursor: Cursor): T
        fun from(contentValues: ContentValues, t: T): ContentValues
    }

    fun executeTransaction(transaction: Transaction) {
        try {
            database.beginTransaction()
            transaction.execute(database)
            database.setTransactionSuccessful()
        } finally {
            if (database.inTransaction()) {
                database.endTransaction()
            }
        }
    }

    private fun <T> collectObjectFromCursor(mapper: Mapper<T>, cursor: Cursor): List<T> {
        val list: MutableList<T> = LinkedList()
        if (cursor.moveToFirst()) {
            do {
                val `object` = mapper.from(cursor)
                list.add(`object`)
            } while (cursor.moveToNext())
        }
        return Collections.unmodifiableList(ArrayList(list))
    }

    fun <T> findAll(table: Table, mapper: Mapper<T>): List<T> {
        return findAll(table,
            mapper,
            QueryBuilder.of(table).buildDefinition()
        )
    }

    fun <T> findAll(table: Table, mapper: Mapper<T>, queryDefinition: QueryDefinition): List<T> {
        val cursor = queryDefinition.query(database, table)
        val list: List<T> = collectObjectFromCursor(mapper, cursor)
        cursor.close()
        return list
    }

    fun <T> findOne(table: Table, mapper: Mapper<T>, queryDefinition: QueryDefinition): T? {
        val list: List<T> = findAll(table, mapper, queryDefinition)
        return if (list.isEmpty()) null else list[0]
    }

    fun <T> insert(table: Table, mapper: Mapper<T>, element: T) {
        insert(table, mapper, listOf(element))
        // calls to other insert
    }

    fun <T> insert(table: Table, mapper: Mapper<T>, elements: List<T>) {
        executeTransaction { sqLiteDatabase: SQLiteDatabase ->
            var contentValues = ContentValues()
            for (t in elements) {
                contentValues = mapper.from(contentValues, t)
                sqLiteDatabase.insertWithOnConflict(table.tableName,
                    null,
                    contentValues,
                    SQLiteDatabase.CONFLICT_REPLACE)
            }
        }
        refresh(table)
    }

    fun <T> delete(table: Table, element: T) {
        element ?: return

        executeTransaction { sqLiteDatabase: SQLiteDatabase ->
            sqLiteDatabase.delete(table.tableName,
                table.idFieldName + " = ?", arrayOf(table.getId(element)))
        }
        refresh(table)
    }

    fun <T> deleteList(table: Table, elements: List<T>) {
        executeTransaction { sqLiteDatabase: SQLiteDatabase ->
            for (t in elements) {
                t ?: continue

                sqLiteDatabase.delete(table.tableName,
                    table.idFieldName + " = ?", arrayOf(table.getId(t)))
            }
        }
        refresh(table)
    }

    fun deleteAll(table: Table) {
        executeTransaction { sqLiteDatabase: SQLiteDatabase ->
            sqLiteDatabase.delete(table.tableName, null, null)
        }
        refresh(table)
    }

    // Experimental. This allows for reactivity.
    private val liveDatas = Collections.synchronizedList(LinkedList<WeakReference<LiveResults<*>>>())

    fun <T> findAllWithChanges(table: Table, mapper: Mapper<T>, queryDefinition: QueryDefinition): LiveResults<T> {
        return LiveResults(backgroundScheduler, this, table, mapper, queryDefinition)
    }

    fun addLiveResults(liveResults: LiveResults<*>) {
        synchronized(this) {
            liveDatas.add(WeakReference(liveResults))
        }
    }

    fun refresh() {
        synchronized(this) {
            val iterator = liveDatas.iterator()
            while (iterator.hasNext()) {
                val weakReference = iterator.next()
                val liveData = weakReference.get()
                if (liveData == null) {
                    iterator.remove()
                } else {
                    liveData.refresh()
                }
            }
        }
    }

    fun refresh(vararg tables: Table) {
        synchronized(this) {
            val iterator = liveDatas.iterator()
            while (iterator.hasNext()) {
                val weakReference = iterator.next()
                val liveData = weakReference.get()
                if (liveData == null) {
                    iterator.remove()
                } else {
                    var isInTables = false
                    val table = liveData.table
                    for (currentTable in tables) {
                        if (currentTable === table) {
                            isInTables = true
                            break
                        }
                    }
                    if (isInTables) {
                        liveData.refresh()
                    }
                }
            }
        }
    }

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 1
    }
}