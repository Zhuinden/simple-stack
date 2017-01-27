package com.zhuinden.simplestackdemoexamplemvp.data.manager;

import android.content.Context;

import com.zhuinden.simplestackdemoexamplemvp.util.SchedulerHolder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

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

    Subscription subscription;

    public void openDatabase() {
        subscription = Observable.create(new Observable.OnSubscribe<Realm>() {
            @Override
            public void call(final Subscriber<? super Realm> subscriber) {
                final Realm observableRealm = Realm.getDefaultInstance();
                final RealmChangeListener<Realm> listener = realm -> {
                    if(!subscriber.isUnsubscribed()) {
                        subscriber.onNext(observableRealm);
                    }
                };
                observableRealm.addChangeListener(listener);
                subscriber.add(Subscriptions.create(() -> {
                    observableRealm.removeChangeListener(listener);
                    observableRealm.close();
                }));
                subscriber.onNext(observableRealm);
            }
        }).subscribeOn(looperScheduler.getScheduler()).unsubscribeOn(looperScheduler.getScheduler()).subscribe();
    }

    public void closeDatabase() {
        if(subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }
}
