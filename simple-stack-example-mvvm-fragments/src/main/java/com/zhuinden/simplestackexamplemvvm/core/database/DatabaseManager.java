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
package com.zhuinden.simplestackexamplemvvm.core.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zhuinden.simplestackexamplemvvm.core.scheduler.BackgroundScheduler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class DatabaseManager
        extends SQLiteOpenHelper {
    public interface Table {
        String getId(Object element);

        String getIdFieldName();

        String getTableName();

        Fields[] getFields();

        String[] getAllQueryFields();

        List<List<Fields>> getIndexedFields();

        void onCreate(SQLiteDatabase database);

        void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion);
    }

    public static final class Fields {
        private String fieldName;
        private String fieldType;
        private String fieldAdditional;

        public Fields(String fieldName, String fieldType, String fieldAdditional) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.fieldAdditional = fieldAdditional;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getFieldType() {
            return fieldType;
        }

        public String getFieldAdditional() {
            return fieldAdditional;
        }

        @Override
        public String toString() {
            return fieldName;
        }
    }

    public interface QueryDefinition {
        Cursor query(SQLiteDatabase database, Table table);
    }

    private static final String DATABASE_NAME = "tasks.db";

    private static final int DATABASE_VERSION = 1;

    private final List<Table> tables;

    private SQLiteDatabase database;

    @Inject
    public DatabaseManager(Context appContext, List<Table> tables) {
        super(appContext, DATABASE_NAME, null, DATABASE_VERSION);
        this.tables = tables;
        this.database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        for(Table table : tables) {
            table.onCreate(database);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        for(Table table : tables) {
            table.onUpgrade(database, oldVersion, newVersion);
        }
    }

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    public interface Transaction {
        void execute(SQLiteDatabase sqLiteDatabase);
    }

    public interface Mapper<T> {
        T from(Cursor cursor);

        ContentValues from(ContentValues contentValues, T t);
    }

    public void executeTransaction(Transaction transaction) {
        try {
            database.beginTransaction();
            transaction.execute(database);
            database.setTransactionSuccessful();
        } finally {
            if(database.inTransaction()) {
                database.endTransaction();
            }
        }
    }

    private <T> List<T> collectObjectFromCursor(Mapper<T> mapper, Cursor cursor) {
        List<T> list = new LinkedList<>();
        if(cursor.moveToFirst()) {
            do {
                T object = mapper.from(cursor);
                list.add(object);
            } while(cursor.moveToNext());
        }
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    public <T> List<T> findAll(Table table, Mapper<T> mapper) {
        return findAll(table,
                mapper,
                QueryBuilder.of(table).buildDefinition()
        );
    }

    public <T> List<T> findAll(Table table, Mapper<T> mapper, QueryDefinition queryDefinition) {
        Cursor cursor = queryDefinition.query(database, table);
        List<T> list = collectObjectFromCursor(mapper, cursor);
        cursor.close();
        return list;
    }

    public <T> T findOne(Table table, Mapper<T> mapper, QueryDefinition queryDefinition) {
        List<T> list = findAll(table, mapper, queryDefinition);
        return list.isEmpty() ? null : list.get(0);
    }

    public <T> void insert(Table table, Mapper<T> mapper, T element) {
        insert(table, mapper, Collections.singletonList(element));
        // calls to other insert
    }

    public <T> void insert(Table table, Mapper<T> mapper, List<T> elements) {
        executeTransaction(sqLiteDatabase -> {
            ContentValues contentValues = new ContentValues();
            for(T t : elements) {
                contentValues = mapper.from(contentValues, t);
                sqLiteDatabase.insertWithOnConflict(table.getTableName(),
                        null,
                        contentValues,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
        });
        refresh(table);
    }

    public <T> void delete(Table table, T element) {
        delete(table, Collections.singletonList(element));;
        // calls to other delete
    }

    public <T> void delete(Table table, List<T> elements) {
        executeTransaction(sqLiteDatabase -> {
            for(T t : elements) {
                sqLiteDatabase.delete(table.getTableName(),
                        table.getIdFieldName() + " = ?",
                        new String[]{table.getId(t)});
            }
        });
        refresh(table);
    }

    public void deleteAll(Table table) {
        executeTransaction(sqLiteDatabase -> sqLiteDatabase.delete(table.getTableName(), null, null));
        refresh(table);
    }

    // Experimental. This allows for reactivity.
    private List<WeakReference<LiveResults<?>>> liveDatas = Collections.synchronizedList(new LinkedList<>());

    public <T> LiveResults<T> findAllWithChanges(BackgroundScheduler backgroundScheduler, Table table, Mapper<T> mapper, QueryDefinition queryDefinition) {
        return new LiveResults<>(backgroundScheduler, this, table, mapper, queryDefinition);
    }

    void addLiveResults(LiveResults<?> liveResults) {
        synchronized(this) {
            this.liveDatas.add(new WeakReference<>(liveResults));
        }
    }

    public void refresh() {
        synchronized(this) {
            Iterator<WeakReference<LiveResults<?>>> iterator = liveDatas.iterator();
            while(iterator.hasNext()) {
                WeakReference<LiveResults<?>> weakReference = iterator.next();
                LiveResults<?> liveData = weakReference.get();
                if(liveData == null) {
                    iterator.remove();
                } else {
                    liveData.refresh();
                }
            }
        }
    }

    public void refresh(Table... tables) {
        synchronized(this) {
            Iterator<WeakReference<LiveResults<?>>> iterator = liveDatas.iterator();
            while(iterator.hasNext()) {
                WeakReference<LiveResults<?>> weakReference = iterator.next();
                LiveResults<?> liveData = weakReference.get();
                if(liveData == null) {
                    iterator.remove();
                } else {
                    boolean isInTables = false;
                    Table table = liveData.table;
                    for(Table currentTable: tables) {
                        if(currentTable == table) {
                            isInTables = true;
                            break;
                        }
                    }
                    if(isInTables) {
                        liveData.refresh();
                    }
                }
            }
        }
    }
}