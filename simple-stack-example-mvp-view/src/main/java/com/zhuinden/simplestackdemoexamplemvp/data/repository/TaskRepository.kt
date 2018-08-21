package com.zhuinden.simplestackdemoexamplemvp.data.repository

import com.zhuinden.simplestackdemoexamplemvp.data.entity.DbTask
import com.zhuinden.simplestackdemoexamplemvp.data.entity.DbTaskFields
import com.zhuinden.simplestackdemoexamplemvp.presentation.mapper.TaskMapper
import com.zhuinden.simplestackdemoexamplemvp.presentation.objects.Task
import com.zhuinden.simplestackdemoexamplemvp.util.SchedulerHolder
import com.zhuinden.simplestackdemoexamplemvp.util.optional.Optional
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.disposables.Disposables
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where
import java.util.concurrent.Callable
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskMapper: TaskMapper,
    @param:Named("LOOPER_SCHEDULER") private val looperScheduler: SchedulerHolder,
    @param:Named("WRITE_SCHEDULER") private val writeScheduler: SchedulerHolder
) {
    val tasksWithChanges: Observable<List<Task>>
        get() = createResults { realm ->
            realm.where<DbTask>()
                .sort(DbTaskFields.ID, Sort.ASCENDING)
                .findAllAsync()
        }

    val completedTasksWithChanges: Observable<List<Task>>
        get() = createResults { realm ->
            realm.where<DbTask>()
                .equalTo(DbTaskFields.COMPLETED, true)
                .sort(DbTaskFields.ID, Sort.ASCENDING)
                .findAllAsync()
        }

    val activeTasksWithChanges: Observable<List<Task>>
        get() = createResults { realm ->
            realm.where<DbTask>()
                .equalTo(DbTaskFields.COMPLETED, false)
                .sort(DbTaskFields.ID, Sort.ASCENDING)
                .findAllAsync()
        }

    private fun mapFrom(dbTasks: RealmResults<DbTask>): List<Task> =
        dbTasks.map { task -> taskMapper.fromRealm(task) }

    private fun doWrite(transaction: (Realm) -> Unit) {
        Single.create(SingleOnSubscribe<Void> { _ ->
            Realm.getDefaultInstance().use { r ->
                r.executeTransaction(transaction)
            }
        }).subscribeOn(writeScheduler.scheduler)
            .subscribe()
    }

    private fun createResults(querySelector: (Realm) -> RealmResults<DbTask>): Observable<List<Task>> {
        return Observable.create(ObservableOnSubscribe<List<Task>> { emitter ->
            val realm = Realm.getDefaultInstance()
            val dbTasks = querySelector(realm)
            val realmChangeListener = RealmChangeListener<RealmResults<DbTask>> { element ->
                if (element.isLoaded && !emitter.isDisposed) {
                    val tasks = mapFrom(element)
                    if (!emitter.isDisposed) {
                        emitter.onNext(tasks)
                    }
                }
            }
            emitter.setDisposable(Disposables.fromAction {
                if (dbTasks.isValid) {
                    dbTasks.removeChangeListener(realmChangeListener)
                }
                realm.close()
            })
            dbTasks.addChangeListener(realmChangeListener)
        }).subscribeOn(looperScheduler.scheduler).unsubscribeOn(looperScheduler.scheduler)
    }

    fun insertTask(task: Task) {
        doWrite { realm -> realm.insertOrUpdate(taskMapper.toRealm(task)) }
    }

    fun insertTasks(tasks: List<Task>) {
        doWrite { realm ->
            realm.insertOrUpdate(tasks.map { task -> taskMapper.toRealm(task) })
        }
    }

    fun deleteCompletedTasks() {
        doWrite { realm ->
            realm.where<DbTask>()
                .equalTo(DbTaskFields.COMPLETED, true)
                .findAll()
                .deleteAllFromRealm()
        }
    }

    fun findTask(taskId: String): Single<Optional<Task>> =
        Single.fromCallable(Callable<Optional<Task>> {
            Realm.getDefaultInstance().use { realm ->
                val tasks = realm.where<DbTask>()
                    .equalTo(DbTaskFields.ID, taskId)
                    .findAll()
                return@Callable when {
                    tasks.size > 0 -> Optional.of(taskMapper.fromRealm(tasks[0]!!))
                    else -> Optional.absent<Task>()
                }
            }
        }).subscribeOn(looperScheduler.scheduler)

    fun setTaskCompleted(task: Task) {
        insertTask(task.copy(completed = true))
    }

    fun setTaskActive(task: Task) {
        insertTask(task.copy(completed = false))
    }

    fun deleteTask(task: Task) {
        doWrite { realm ->
            realm.where<DbTask>()
                .equalTo(DbTaskFields.ID, task.id)
                .findAll()
                .deleteAllFromRealm()
        }
    }
}