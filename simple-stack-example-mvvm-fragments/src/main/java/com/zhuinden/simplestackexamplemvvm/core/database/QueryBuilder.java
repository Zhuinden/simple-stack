package com.zhuinden.simplestackexamplemvvm.core.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QueryBuilder {
    private QueryBuilder() {
    }

    public enum Sort {
        NONE(""),
        ASC("ASC"),
        DESC("DESC");

        private final String value;

        private Sort(String value) {
            this.value = value;
        }
    }

    private boolean distinct = false;

    private String tableName;

    private String[] columns;

    private String whereCondition;

    private String[] whereArgs;

    private String groupBy;

    private String having;

    private String orderBy;

    private Sort sortOrder;

    private String limit = null;

    public QueryBuilder distinct(boolean distinct) {
        this.distinct = true;
        return this;
    }

    public QueryBuilder tableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public QueryBuilder columns(String[] columns) {
        this.columns = columns;
        return this;
    }

    public QueryBuilder where(String condition, Object... args) {
        this.whereCondition = condition;
        int length = args == null ? 0 : args.length;
        String[] selectionArgs = new String[length];
        for(int i = 0; i < length; i++) {
            Object arg = args[i];
            if(arg instanceof String) {
                selectionArgs[i] = (String) arg;
            } else {
                selectionArgs[i] = arg.toString();
            }
        }
        this.whereArgs = selectionArgs;
        return this;
    }

    public QueryBuilder groupBy(String groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public QueryBuilder having(String having) {
        this.having = having;
        return this;
    }

    public QueryBuilder orderBy(String orderBy, Sort sortOrder) {
        this.orderBy = orderBy;
        this.sortOrder = sortOrder;
        return this;
    }

    public QueryBuilder limit(Integer limit) {
        if(limit == null) {
            this.limit = null;
        } else {
            this.limit = String.valueOf(limit);
        }
        return this;
    }

    public static QueryBuilder of(DatabaseManager.Table table) {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.tableName = table.getTableName();
        queryBuilder.columns = table.getAllQueryFields();
        return queryBuilder;
    }

    public DatabaseManager.QueryDefinition buildDefinition() {
        return (database, table) -> executeQuery(database);
    }

    public Cursor executeQuery(SQLiteDatabase database) {
        return database.query(
                distinct,
                tableName,
                columns,
                whereCondition,
                whereArgs,
                groupBy,
                having,
                orderBy == null ? null : orderBy + " " + sortOrder.value,
                limit
        );
    }
}
