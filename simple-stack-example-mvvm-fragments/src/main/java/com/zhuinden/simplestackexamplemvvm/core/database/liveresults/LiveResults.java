/*
 * Copyright 2017 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.simplestackexamplemvvm.core.database.liveresults;

import android.arch.lifecycle.MutableLiveData;

import com.zhuinden.simplestackexamplemvvm.core.database.DatabaseManager;
import com.zhuinden.simplestackexamplemvvm.core.scheduler.Scheduler;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Experimental.
 */
public class LiveResults<T>
        extends MutableLiveData<List<T>> {
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private final DatabaseManager databaseManager;
    private final DatabaseManager.Table table;
    private final DatabaseManager.Mapper<T> mapper;
    private final DatabaseManager.QueryDefinition queryDefinition;

    private final Scheduler backgroundScheduler;

    public LiveResults(Scheduler backgroundScheduler, DatabaseManager databaseManager, DatabaseManager.Table table, DatabaseManager.Mapper<T> mapper, DatabaseManager.QueryDefinition queryDefinition) {
        this.databaseManager = databaseManager;
        this.table = table;
        this.mapper = mapper;
        this.queryDefinition = queryDefinition;
        this.backgroundScheduler = backgroundScheduler;
    }

    public void refresh() {
        backgroundScheduler.execute(() -> {
            postValue(databaseManager.findAll(table, mapper, queryDefinition));
        });
    }

    @Override
    public void setValue(List<T> value) {
        initialized.set(true); // there is a small window here that can cause problems...
        super.setValue(value);
    }

    public boolean isLoaded() {
        return initialized.get();
    }

    @Override
    protected void onActive() {
        refresh();
    }
}
