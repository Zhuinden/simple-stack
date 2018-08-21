package com.zhuinden.simplestackexamplemvvm.core.database;

import android.support.annotation.Nullable;

import com.zhuinden.simplestackexamplemvvm.core.scheduler.BackgroundScheduler;

import java.util.Collections;
import java.util.List;

public abstract class BaseDao<T> implements Dao<T> {
    private final BackgroundScheduler backgroundScheduler;
    private final DatabaseManager databaseManager;
    private final DatabaseManager.Table table;
    private final DatabaseManager.Mapper<T> mapper;

    public BaseDao(BackgroundScheduler backgroundScheduler, DatabaseManager databaseManager, DatabaseManager.Table table, DatabaseManager.Mapper<T> mapper) {
        this.backgroundScheduler = backgroundScheduler;
        this.databaseManager = databaseManager;
        this.table = table;
        this.mapper = mapper;
    }
    @Override
    public DatabaseManager.Table getTable() {
        return table;
    }


    @Override
    public void refresh() {
        databaseManager.refresh(table);
    }

    @Override
    public T findOne(@Nullable String id) {
        if(id == null) {
            return databaseManager.findOne(table, mapper, QueryBuilder.of(table)
                    .select("1 = 0")
                    .buildDefinition());
        } else {
            return databaseManager.findOne(table, mapper, QueryBuilder.of(table)
                    .select(getTable().getIdFieldName() + " = ?", id)
                    .limit(1)
                    .buildDefinition());
        }
    }

    @Override
    public T findOne(DatabaseManager.QueryDefinition queryDefinition) {
        return databaseManager.findOne(table, mapper, queryDefinition);
    }

    @Override
    public List<T> findAll() {
        return databaseManager.findAll(table, mapper);
    }

    @Override
    public List<T> findAll(DatabaseManager.QueryDefinition queryDefinition) {
        return databaseManager.findAll(table, mapper, queryDefinition);
    }

    @Override
    public void insert(T element) {
        databaseManager.insert(table, mapper, Collections.singletonList(element));
    }

    @Override
    public void insert(List<T> list) {
        databaseManager.insert(table, mapper, list);
    }

    @Override
    public void delete(T element) {
        databaseManager.delete(table, Collections.singletonList(element));
    }

    @Override
    public void delete(List<T> list) {
        databaseManager.delete(table, list);
    }

    @Override
    public void deleteAll() {
        databaseManager.deleteAll(table);
    }

    @Override
    public LiveResults<T> findAllWithChanges() {
        return databaseManager.findAllWithChanges(backgroundScheduler, table, mapper, QueryBuilder.of(table).buildDefinition());
    }

    @Override
    public LiveResults<T> findAllWithChanges(DatabaseManager.QueryDefinition queryDefinition) {
        return databaseManager.findAllWithChanges(backgroundScheduler, table, mapper, queryDefinition);
    }
}
