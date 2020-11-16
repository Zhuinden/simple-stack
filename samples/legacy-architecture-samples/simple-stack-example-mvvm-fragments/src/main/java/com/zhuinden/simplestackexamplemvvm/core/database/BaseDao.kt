package com.zhuinden.simplestackexamplemvvm.core.database


import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager.QueryDefinition

abstract class BaseDao<T>(
    private val databaseManager: DatabaseManager,
    override val table: DatabaseManager.Table,
    private val mapper: DatabaseManager.Mapper<T>
) : Dao<T> {
    override fun refresh() {
        databaseManager.refresh(table)
    }

    override fun findOne(id: String): T? {
        return if (id == null) {
            databaseManager.findOne(table, mapper, QueryBuilder.of(table)
                .where("1 = 0")
                .buildDefinition())
        } else {
            databaseManager.findOne(table, mapper, QueryBuilder.of(table)
                .where(table.idFieldName + " = ?", id)
                .limit(1)
                .buildDefinition())
        }
    }

    override fun findOne(queryDefinition: QueryDefinition): T? {
        return databaseManager.findOne(table, mapper, queryDefinition)
    }

    override fun findAll(): List<T> {
        return databaseManager.findAll(table, mapper)
    }

    override fun findAll(queryDefinition: QueryDefinition): List<T> {
        return databaseManager.findAll(table, mapper, queryDefinition)
    }

    override fun insert(element: T) {
        databaseManager.insert(table, mapper, listOf(element))
    }

    override fun insert(list: List<T>) {
        databaseManager.insert(table, mapper, list)
    }

    override fun delete(element: T) {
        databaseManager.delete(table, element)
    }

    override fun deleteList(list: List<T>) {
        databaseManager.deleteList(table, list)
    }

    override fun deleteAll() {
        databaseManager.deleteAll(table)
    }

    override fun findAllWithChanges(): LiveResults<T> {
        return databaseManager.findAllWithChanges(table, mapper, QueryBuilder.of(table).buildDefinition())
    }

    override fun findAllWithChanges(queryDefinition: QueryDefinition): LiveResults<T> {
        return databaseManager.findAllWithChanges(table, mapper, queryDefinition)
    }
}