package com.zhuinden.simplestackdemoexamplemvp.data.manager;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Owner on 2017. 01. 26..
 */
@Singleton
public class DatabaseManager {
    @Inject
    public DatabaseManager() {
    }

    private static final String TAG = "DatabaseManager";

    ThreadLocal<Realm> realms = new ThreadLocal<>();
    ThreadLocal<Integer> instanceCount = new ThreadLocal<>();

    public void init(Context context) {
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public Realm openDatabase() {
        Realm realm = realms.get();
        if(realm == null) {
            realm = Realm.getDefaultInstance();
            realms.set(realm);
            instanceCount.set(1);
            Log.i(TAG, "Opening Realm with instance count [" + instanceCount.get() + "]");
        } else {
            Integer count = instanceCount.get();
            count = ++count;
            instanceCount.set(count);
            Log.i(TAG, "Incrementing Realm instance count to [" + count + "]");
        }
        return realm;
    }

    public Realm getDatabase() {
        Realm realm = realms.get();
        if(realm == null) {
            throw new IllegalStateException("Realm was not opened on this thread!");
        }
        Log.i(TAG, "Getting Realm with instance count [" + instanceCount.get() + "]");
        return realm;
    }

    public void closeDatabase() {
        Realm realm = realms.get();
        if(realm == null) {
            throw new IllegalStateException("There is no open Realm on this thread!");
        }
        Integer count = instanceCount.get();
        if(count == null) {
            throw new IllegalStateException("Instance count should exist but doesn't!");
        }
        count = --count;
        if(count <= 0) {
            if(!realm.isClosed()) {
                realm.close();
            }
            realms.remove();
            instanceCount.remove();
            Log.i(TAG, "Closing Realm.");
        }
    }
}
