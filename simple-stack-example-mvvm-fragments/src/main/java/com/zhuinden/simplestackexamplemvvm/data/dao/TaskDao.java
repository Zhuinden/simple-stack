package com.zhuinden.simplestackexamplemvvm.data.dao;

import com.zhuinden.simplestackexamplemvvm.core.database.BaseDao;
import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager;
import com.zhuinden.simplestackexamplemvvm.core.scheduler.BackgroundScheduler;
import com.zhuinden.simplestackexamplemvvm.data.Task;
import com.zhuinden.simplestackexamplemvvm.data.tables.TaskTable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TaskDao extends BaseDao<Task> {
    @Inject
    TaskDao(DatabaseManager databaseManager, BackgroundScheduler backgroundScheduler, TaskTable table) {
        super(backgroundScheduler, databaseManager, table, table);
    }
}
