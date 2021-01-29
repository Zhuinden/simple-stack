package com.zhuinden.simplestackexamplemvvm.core.database


import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager.QueryDefinition

/**
 * Created by Zhuinden on 2017.07.26..
 */
interface Dao<T : Any> {
    val table: DatabaseManager.Table
    fun refresh()
    fun findOne(id: String?): T?

    //T findOne(final Long id, boolean withRelations);
    fun findOne(queryDefinition: QueryDefinition): T?
    fun findAll(): List<T>

    // List<T> findAll(boolean withRelations); // I don't remember how this worked and I'd need a JOIN table to remember ^_^
    fun findAll(queryDefinition: QueryDefinition): List<T>

    // List<T> findAll(DatabaseManager.QueryDefinition queryDefinition, boolean withRelations); // I don't remember how this worked and I'd need a JOIN table to remember ^_^
    fun insert(element: T)
    fun insert(list: List<T>)
    fun delete(element: T)
    fun deleteList(list: List<T>)
    fun deleteAll()
    fun findAllWithChanges(): LiveResults<T>

    // LiveResults<T> findAllWithChanges(boolean withRelations); // I don't remember how this worked and I'd need a JOIN table to remember ^_^
    fun findAllWithChanges(queryDefinition: QueryDefinition): LiveResults<T> // LiveResults<T> findAllWithChanges(DatabaseManager.QueryDefinition queryDefinition, boolean withRelations); // I don't remember how this worked and I'd need a JOIN table to remember ^_^
}