package com.zhuinden.simplestackexamplemvvm.core.database;

import com.zhuinden.simplestackexamplemvvm.core.database.liveresults.LiveResults;

import java.util.List;

/**
 * If I want to finish the merge of the old Dao system, then I'll have to remove the queries from DatabaseManager first.
 *
 * Created by Owner on 2017. 09. 03..
 */
public interface Dao<T> {
    DatabaseManager.Table getTable();

    //T findOne(final Long id);

    //T findOne(final Long id, boolean withRelations);

    //T findOne(DatabaseManager.QueryDefinition queryDefinition);

    List<T> findAll();

    List<T> findAll(boolean withRelations);

    List<T> findAll(DatabaseManager.QueryDefinition queryDefinition);

    List<T> findAll(DatabaseManager.QueryDefinition queryDefinition, boolean withRelations);

    void insert(final List<T> list);

    void delete(final List<T> list);

    LiveResults<T> findAllWithChanges();

    LiveResults<T> findAllWithChanges(boolean withRelations);

    LiveResults<T> findAllWithChanges(DatabaseManager.QueryDefinition queryDefinition);

    LiveResults<T> findAllWithChanges(DatabaseManager.QueryDefinition queryDefinition, boolean withRelations);
}
