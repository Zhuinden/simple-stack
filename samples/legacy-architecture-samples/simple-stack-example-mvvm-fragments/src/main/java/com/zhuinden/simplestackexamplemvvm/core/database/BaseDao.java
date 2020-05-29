package com.zhuinden.simplestackexamplemvvm.core.database;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class BaseDao<T> implements Dao<T> {
    private final DatabaseManager databaseManager;
    private final DatabaseManager.Table table;
    private final DatabaseManager.Mapper<T> mapper;

    public BaseDao(DatabaseManager databaseManager, DatabaseManager.Table table, DatabaseManager.Mapper<T> mapper) {
        this.databaseManager = databaseManager;
        this.table = table;
        this.mapper = mapper;
    }

    @Override
    public final DatabaseManager.Table getTable() {
        return table;
    }

    @Override
    public void refresh() {
        databaseManager.refresh(table);
    }

    @Override
    public final T findOne(@Nullable String id) {
        if(id == null) {
            return databaseManager.findOne(table, mapper, QueryBuilder.of(table)
                    .where("1 = 0")
                    .buildDefinition());
        } else {
            return databaseManager.findOne(table, mapper, QueryBuilder.of(table)
                    .where(getTable().getIdFieldName() + " = ?", id)
                    .limit(1)
                    .buildDefinition());
        }
    }

    @Override
    public final T findOne(DatabaseManager.QueryDefinition queryDefinition) {
        return databaseManager.findOne(table, mapper, queryDefinition);
    }

    @Override
    public final List<T> findAll() {
        return databaseManager.findAll(table, mapper);
    }

    @Override
    public final List<T> findAll(DatabaseManager.QueryDefinition queryDefinition) {
        return databaseManager.findAll(table, mapper, queryDefinition);
    }

    @Override
    public final void insert(T element) {
        databaseManager.insert(table, mapper, Collections.singletonList(element));
    }

    @Override
    public final void insert(List<T> list) {
        databaseManager.insert(table, mapper, list);
    }

    @Override
    public final void delete(T element) {
        databaseManager.delete(table, Collections.singletonList(element));
    }

    @Override
    public final void delete(List<T> list) {
        databaseManager.delete(table, list);
    }

    @Override
    public final void deleteAll() {
        databaseManager.deleteAll(table);
    }

    @Override
    public final LiveResults<T> findAllWithChanges() {
        return databaseManager.findAllWithChanges(table, mapper, QueryBuilder.of(table).buildDefinition());
    }

    @Override
    public final LiveResults<T> findAllWithChanges(DatabaseManager.QueryDefinition queryDefinition) {
        return databaseManager.findAllWithChanges(table, mapper, queryDefinition);
    }
}
