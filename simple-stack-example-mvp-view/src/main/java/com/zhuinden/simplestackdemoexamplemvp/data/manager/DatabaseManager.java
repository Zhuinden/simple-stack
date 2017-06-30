package com.zhuinden.simplestackdemoexamplemvp.data.manager;

import android.content.Context;

import com.zhuinden.simplestackdemoexamplemvp.util.SchedulerHolder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;

/**
 * Created by Owner on 2017. 01. 26..
 */
@Singleton
public class DatabaseManager {
    @Inject
    public DatabaseManager() {
    }

    @Inject
    @Named("LOOPER_SCHEDULER")
    SchedulerHolder looperScheduler;

    private static final String TAG = "DatabaseManager";

    public void init(Context context) {
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    Disposable disposable;

    public void openDatabase() {
        disposable = Observable.create((ObservableOnSubscribe<Realm>) emitter -> {
            final Realm observableRealm = Realm.getDefaultInstance();
            final RealmChangeListener<Realm> listener = realm -> {
                if(!emitter.isDisposed()) {
                    emitter.onNext(observableRealm);
                }
            };
            observableRealm.addChangeListener(listener);
            emitter.setDisposable(Disposables.fromAction(() -> {
                observableRealm.removeChangeListener(listener);
                observableRealm.close();
            }));
            emitter.onNext(observableRealm);
        }).subscribeOn(looperScheduler.getScheduler()).unsubscribeOn(looperScheduler.getScheduler()).subscribe();
    }

    public void closeDatabase() {
        if(disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }
}
