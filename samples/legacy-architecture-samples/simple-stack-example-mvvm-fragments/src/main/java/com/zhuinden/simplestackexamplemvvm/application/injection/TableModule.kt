package com.zhuinden.simplestackexamplemvvm.application.injection


import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager
import com.zhuinden.simplestackexamplemvvm.data.tables.TaskTable
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Singleton

/**
 * Created by Zhuinden on 2017.07.26..
 */
@Module
class TableModule {
    @Provides
    @Singleton
    fun tables(taskTable: TaskTable): List<@JvmSuppressWildcards DatabaseManager.Table> =
        Collections.unmodifiableList(listOf<DatabaseManager.Table>(taskTable))
}