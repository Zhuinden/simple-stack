package com.zhuinden.simplestackkotlindaggerexample.userdetail

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.VERTICAL
import com.zhuinden.simplestackkotlindaggerexample.AndroidApp
import com.zhuinden.simplestackkotlindaggerexample.ApiService
import com.zhuinden.simplestackkotlindaggerexample.BaseFragment
import com.zhuinden.simplestackkotlindaggerexample.R
import com.zhuinden.simplestackkotlindaggerexample.clearIfNotDisposed
import com.zhuinden.simplestackkotlindaggerexample.realmobjects.UserRO
import com.zhuinden.simplestackkotlindaggerexample.schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_users.contactsView
import kotlinx.android.synthetic.main.fragment_users.contentView
import kotlinx.android.synthetic.main.fragment_users.usersSearchView
import kotlinx.android.synthetic.main.fragment_users.viewAnimator
import kotlinx.android.synthetic.main.view_loading.loadingView
import org.jetbrains.anko.sdk15.listeners.textChangedListener
import javax.inject.Inject

class UsersFragment : BaseFragment() {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private lateinit var realm: Realm
    private lateinit var usersAdapter: UsersAdapter
    private var indexOfContentView: Int = 0
    private var indexOfLoadingView: Int = 0
    private lateinit var items: RealmResults<UserRO>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidApp.appComponent.inject(this)
        indexOfContentView = viewAnimator.indexOfChild(contentView)
        indexOfLoadingView = viewAnimator.indexOfChild(loadingView)
        realm = Realm.getDefaultInstance()
        items = realm.where<UserRO>().sort("userName", Sort.ASCENDING).findAll()
        usersAdapter = UsersAdapter(items)
        usersSearchView.textChangedListener {
            afterTextChanged {
                usersAdapter.updateData(
                    realm.where<UserRO>()
                        .contains("userName", it.toString()).findAllAsync()
                )
            }
        }
        contactsView.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        contactsView.adapter = usersAdapter
        loadUsers()
    }

    private fun loadUsers() {
        loadingView.visibility = View.VISIBLE
        compositeDisposable += apiService
            .getAllUsers()
            .retry()
            .subscribeOn(schedulerProvider.io())
            .doOnNext {
                println("Thread doOnNext: %s" + Thread.currentThread().name)
                val userROs: MutableList<UserRO> = ArrayList()
                for (user in it.result) {
                    val userRO: UserRO = UserRO.createFromUser(user)
                    userROs.add(userRO)
                }
                Realm.getDefaultInstance().use { realmInstance ->
                    realmInstance.executeTransaction {
                        realmInstance.insertOrUpdate(userROs)
                    }
                }
            }
            .observeOn(schedulerProvider.ui())
            .doFinally { loadingView.visibility = View.GONE }
            .subscribeBy(
                onNext = {
                    println("onNext!")
                },
                onError = {
                    it.printStackTrace()
                },
                onComplete = {
                    println("Done!")
                }
            )
    }

    override fun onDestroy() {
        realm.close()
        compositeDisposable.clearIfNotDisposed()
        super.onDestroy()
    }

}