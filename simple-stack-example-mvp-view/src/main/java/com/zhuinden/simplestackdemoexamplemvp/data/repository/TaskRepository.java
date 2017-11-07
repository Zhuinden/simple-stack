package com.zhuinden.simplestackdemoexamplemvp.data.repository;

import com.zhuinden.simplestackdemoexamplemvp.data.entity.DbTask;
import com.zhuinden.simplestackdemoexamplemvp.data.entity.DbTaskFields;
import com.zhuinden.simplestackdemoexamplemvp.presentation.mapper.TaskMapper;
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task;
import com.zhuinden.simplestackdemoexamplemvp.util.SchedulerHolder;
import com.zhuinden.simplestackdemoexamplemvp.util.optional.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposables;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Owner on 2017. 01. 26..
 */
@Singleton
public class TaskRepository {
    private final TaskMapper taskMapper;
    private final SchedulerHolder looperScheduler;
    private final SchedulerHolder writeScheduler;

    @Inject
    public TaskRepository(TaskMapper taskMapper, @Named("LOOPER_SCHEDULER") SchedulerHolder looperScheduler, @Named("WRITE_SCHEDULER") SchedulerHolder writeScheduler) {
        this.taskMapper = taskMapper;
        this.looperScheduler = looperScheduler;
        this.writeScheduler = writeScheduler;
    }

    private List<Task> mapFrom(RealmResults<DbTask> dbTasks) {
        List<Task> tasks = new ArrayList<>(dbTasks.size());
        for(DbTask dbTask : dbTasks) {
            tasks.add(taskMapper.fromRealm(dbTask));
        }
        return tasks;
    }

    private interface QuerySelector<E extends RealmModel> {
        RealmResults<E> createQuery(Realm realm);
    }

    private Observable<List<Task>> createResults(QuerySelector<DbTask> querySelector) {
        return Observable.create((ObservableOnSubscribe<List<Task>>) emitter -> {
            Realm realm = Realm.getDefaultInstance();
            final RealmResults<DbTask> dbTasks = querySelector.createQuery(realm);
            final RealmChangeListener<RealmResults<DbTask>> realmChangeListener = element -> {
                if(element.isLoaded() && !emitter.isDisposed()) {
                    List<Task> tasks = mapFrom(element);
                    if(!emitter.isDisposed()) {
                        emitter.onNext(tasks);
                    }
                }
            };
            emitter.setDisposable(Disposables.fromAction(() -> {
                if(dbTasks.isValid()) {
                    dbTasks.removeChangeListener(realmChangeListener);
                }
                realm.close();
            }));
            dbTasks.addChangeListener(realmChangeListener);
        }).subscribeOn(looperScheduler.getScheduler()).unsubscribeOn(looperScheduler.getScheduler());
    }

    public Observable<List<Task>> getTasks() {
        return createResults((realm) -> realm.where(DbTask.class).findAllSortedAsync(DbTaskFields.ID, Sort.ASCENDING));
    }

    public Observable<List<Task>> getCompletedTasks() {
        return createResults((realm) -> realm
                .where(DbTask.class)
                .equalTo(DbTaskFields.COMPLETED, true)
                .findAllSortedAsync(DbTaskFields.ID, Sort.ASCENDING));
    }

    public Observable<List<Task>> getActiveTasks() {
        return createResults((realm) -> realm
                .where(DbTask.class)
                .equalTo(DbTaskFields.COMPLETED, false)
                .findAllSortedAsync(DbTaskFields.ID, Sort.ASCENDING));
    }

    @SuppressWarnings("NewApi")
    public void insertTask(Task task) {
        Single.create((SingleOnSubscribe<Void>) singleSubscriber -> {
            try(Realm r = Realm.getDefaultInstance()) {
                r.executeTransaction(realm -> {
                    realm.insertOrUpdate(taskMapper.toRealm(task));
                });
            }
        }).subscribeOn(writeScheduler.getScheduler()).subscribe();
    }

    @SuppressWarnings("NewApi")
    public void insertTasks(List<Task> tasks) {
        Single.create((SingleOnSubscribe<Void>) singleSubscriber -> {
            try(Realm r = Realm.getDefaultInstance()) {
                List<DbTask> dbTasks = new ArrayList<>(tasks.size());
                for(Task task : tasks) {
                    dbTasks.add(taskMapper.toRealm(task));
                }
                r.executeTransaction(realm -> {
                    realm.insertOrUpdate(dbTasks);
                });
            }
        }).subscribeOn(writeScheduler.getScheduler()).subscribe();
    }

    @SuppressWarnings("NewApi")
    public void deleteCompletedTasks() {
        Single.create((SingleOnSubscribe<Void>) singleSubscriber -> {
            try(Realm r = Realm.getDefaultInstance()) {
                r.executeTransaction(realm -> realm.where(DbTask.class)
                        .equalTo(DbTaskFields.COMPLETED, true)
                        .findAll()
                        .deleteAllFromRealm());
            }
        }).subscribeOn(writeScheduler.getScheduler()).subscribe();
    }

    @SuppressWarnings("NewApi")
    public Single<Optional<Task>> findTask(String taskId) {
        return Single.fromCallable((Callable<Optional<Task>>) () -> {
            try(Realm realm = Realm.getDefaultInstance()) {
                RealmResults<DbTask> tasks = realm.where(DbTask.class).equalTo(DbTaskFields.ID, taskId).findAll();
                if(tasks.size() > 0) {
                    return Optional.of(taskMapper.fromRealm(tasks.get(0)));
                } else {
                    return Optional.absent();
                }
            }
        }).subscribeOn(looperScheduler.getScheduler());
    }

    public void setTaskCompleted(Task task) {
        insertTask(task.toBuilder().setCompleted(true).build());
    }

    public void setTaskActive(Task task) {
        insertTask(task.toBuilder().setCompleted(false).build());
    }

    @SuppressWarnings("NewApi")
    public void deleteTask(Task task) {
        Single.create((SingleOnSubscribe<Void>) singleSubscriber -> {
            try(Realm r = Realm.getDefaultInstance()) {
                r.executeTransaction(realm -> realm.where(DbTask.class).equalTo(DbTaskFields.ID, task.id()).findAll().deleteAllFromRealm());
            }
        }).subscribeOn(writeScheduler.getScheduler()).subscribe();
    }
}
