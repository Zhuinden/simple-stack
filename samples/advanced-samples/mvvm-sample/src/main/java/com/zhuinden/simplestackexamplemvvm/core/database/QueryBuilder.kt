package com.zhuinden.simplestackexamplemvvm.core.database


import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager.QueryDefinition

/**
 * Created by Zhuinden on 2017.07.26..
 */
class QueryBuilder private constructor() {
    enum class Sort(val value: String) {
        NONE(""), ASC("ASC"), DESC("DESC");
    }

    private var distinct = false
    private var tableName: String? = null
    private var columns: Array<String>? = null
    private var whereCondition: String? = null
    private var whereArgs: Array<String>? = null
    private var groupBy: String? = null
    private var having: String? = null
    private var orderBy: String? = null
    private var sortOrder: Sort? = null
    private var limit: String? = null

    fun distinct(distinct: Boolean): QueryBuilder {
        this.distinct = distinct
        return this
    }

    fun tableName(tableName: String): QueryBuilder {
        this.tableName = tableName
        return this
    }

    fun columns(columns: Array<String>): QueryBuilder {
        this.columns = columns
        return this
    }

    fun where(condition: String, vararg args: Any): QueryBuilder {
        whereCondition = condition
        val length = args.size ?: 0
        val selectionArgs = arrayOfNulls<String>(length)
        for (i in 0 until length) {
            val arg = args[i]
            if (arg is String) {
                selectionArgs[i] = arg
            } else {
                selectionArgs[i] = arg.toString()
            }
        }
        whereArgs = selectionArgs.mapNotNull { it }.toTypedArray()
        return this
    }

    fun groupBy(groupBy: String?): QueryBuilder {
        this.groupBy = groupBy
        return this
    }

    fun having(having: String?): QueryBuilder {
        this.having = having
        return this
    }

    fun orderBy(orderBy: String?, sortOrder: Sort?): QueryBuilder {
        this.orderBy = orderBy
        this.sortOrder = sortOrder
        return this
    }

    fun limit(limit: Int?): QueryBuilder {
        if (limit == null) {
            this.limit = null
        } else {
            this.limit = limit.toString()
        }
        return this
    }

    fun buildDefinition(): QueryDefinition {
        return QueryDefinition { database: SQLiteDatabase, table: DatabaseManager.Table? -> executeQuery(database) }
    }

    fun executeQuery(database: SQLiteDatabase): Cursor {
        return database.query(
            distinct,
            tableName,
            columns,
            whereCondition,
            whereArgs,
            groupBy,
            having,
            if (orderBy == null) null else orderBy + " " + sortOrder!!.value,
            limit
        )
    }

    companion object {
        fun of(table: DatabaseManager.Table): QueryBuilder {
            val queryBuilder = QueryBuilder()
            queryBuilder.tableName = table.tableName
            queryBuilder.columns = table.allQueryFields
            return queryBuilder
        }
    }
}