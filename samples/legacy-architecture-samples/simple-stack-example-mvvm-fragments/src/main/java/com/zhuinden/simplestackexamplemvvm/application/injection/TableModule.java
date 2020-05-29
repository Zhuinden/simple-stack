package com.zhuinden.simplestackexamplemvvm.application.injection;

import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager;
import com.zhuinden.simplestackexamplemvvm.data.tables.TaskTable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Zhuinden on 2017.07.26..
 */
@Module
public class TableModule {
    @Provides
    @Singleton
    public List<DatabaseManager.Table> tables(TaskTable taskTable) {
        //noinspection ArraysAsListWithZeroOrOneArgument
        return Collections.unmodifiableList(Arrays.asList(taskTable));
    }
}
