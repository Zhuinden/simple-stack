package com.example.ktdagger.userdetail

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.VERTICAL
import com.example.ktdagger.AndroidApp
import com.example.ktdagger.ApiService
import com.example.ktdagger.BaseFragment
import com.example.ktdagger.R
import com.example.ktdagger.clearIfNotDisposed
import com.example.ktdagger.realmobjects.UserRO
import com.example.ktdagger.reponses.AllUsersResponse
import com.example.ktdagger.schedulers.BaseSchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_users.contactsView
import kotlinx.android.synthetic.main.fragment_users.contentView
import kotlinx.android.synthetic.main.fragment_users.usersSearchView
import kotlinx.android.synthetic.main.fragment_users.viewAnimator
import kotlinx.android.synthetic.main.view_loading.loadingView
import javax.inject.Inject

class UsersFragment : BaseFragment() {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Inject
    lateinit var schedulerProvider: BaseSchedulerProvider

    private lateinit var realm: Realm;
    private lateinit var autoCompleteAdapter: UsersAdapter
    private var indexOfContentView: Int = 0
    private var indexOfLoadingView: Int = 0
    private lateinit var items: RealmResults<UserRO>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidApp.appComponent?.inject(this);

        indexOfContentView = viewAnimator.indexOfChild(contentView)
        indexOfLoadingView = viewAnimator.indexOfChild(loadingView)

        realm = Realm.getDefaultInstance()
        val items = realm.where<UserRO>().sort("userName", Sort.ASCENDING).findAll()
        autoCompleteAdapter = UsersAdapter(realm, items)
        usersSearchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                autoCompleteAdapter.updateInput(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        });
        contactsView.adapter = autoCompleteAdapter
        contactsView.layoutManager = LinearLayoutManager(view.context, VERTICAL, false)
    }

    override fun onStart() {
        super.onStart()
        loadUsers()
    }

    private fun loadUsers() {
        loadingView.visibility = View.VISIBLE
        compositeDisposable += apiService
            .getAllUsers()
            .retry()
            .subscribeOn(schedulerProvider.io())
            .doOnNext { allUserResponse: AllUsersResponse ->
                println("Thread doOnNext: %s" + Thread.currentThread().name)
                val userROs: MutableList<UserRO> = ArrayList();
                for (user in allUserResponse.result) {
                    val userRO: UserRO = UserRO.createFromUser(user);
                    userROs.add(userRO);
                }
                Realm.getDefaultInstance().use { realmInstance ->
                    realmInstance.executeTransaction {
                        val result: UserRO? = realmInstance.where<UserRO>().findFirst()
                        if (result == null) {
                            realmInstance.createObject<UserRO>(0)
                        } else {
                            realmInstance.copyToRealmOrUpdate(userROs)
                        }
                    }
                }
            }
            .observeOn(schedulerProvider.ui())
            .subscribeBy(  // named arguments for lambda Subscribers
                onNext = {
                    loadingView.visibility = View.GONE
                },
                onError = {
                    loadingView.visibility = View.GONE
                    it.printStackTrace()
                },
                onComplete = {
                    loadingView.visibility = View.GONE
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