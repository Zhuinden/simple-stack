package com.zhuinden.simplestackexamplemvvm.core.database;

import java.util.List;

/**
 * Created by Owner on 2017. 09. 03..
 */
public interface Dao<T> {
    DatabaseManager.Table getTable();

    void refresh();

    T findOne(final String id);

    //T findOne(final Long id, boolean withRelations);

    T findOne(DatabaseManager.QueryDefinition queryDefinition);

    List<T> findAll();

    // List<T> findAll(boolean withRelations); // I don't remember how this worked and I'd need a JOIN table to remember ^_^

    List<T> findAll(DatabaseManager.QueryDefinition queryDefinition);

    // List<T> findAll(DatabaseManager.QueryDefinition queryDefinition, boolean withRelations); // I don't remember how this worked and I'd need a JOIN table to remember ^_^

    void insert(final T element);

    void insert(final List<T> list);

    void delete(final T element);

    void delete(final List<T> list);

    void deleteAll();

    LiveResults<T> findAllWithChanges();

    // LiveResults<T> findAllWithChanges(boolean withRelations); // I don't remember how this worked and I'd need a JOIN table to remember ^_^

    LiveResults<T> findAllWithChanges(DatabaseManager.QueryDefinition queryDefinition);

    // LiveResults<T> findAllWithChanges(DatabaseManager.QueryDefinition queryDefinition, boolean withRelations); // I don't remember how this worked and I'd need a JOIN table to remember ^_^
}
