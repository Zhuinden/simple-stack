package com.community.simplestackkotlindaggerexample.screens.users

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.VERTICAL
import com.community.simplestackkotlindaggerexample.R
import com.community.simplestackkotlindaggerexample.application.Injector
import com.community.simplestackkotlindaggerexample.core.navigation.BaseFragment
import com.community.simplestackkotlindaggerexample.core.schedulers.SchedulerProvider
import com.community.simplestackkotlindaggerexample.data.api.ApiService
import com.community.simplestackkotlindaggerexample.data.database.User
import com.community.simplestackkotlindaggerexample.utils.clearIfNotDisposed
import com.community.simplestackkotlindaggerexample.utils.hide
import com.community.simplestackkotlindaggerexample.utils.onTextChanged
import com.community.simplestackkotlindaggerexample.utils.show
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_users.*
import javax.inject.Inject

class UsersFragment : BaseFragment() {
    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private lateinit var realm: Realm
    private lateinit var usersAdapter: UsersAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Injector.get().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_users, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        realm = Realm.getDefaultInstance()

        usersAdapter = UsersAdapter(realm.where<User>().findAllAsync())

        listContacts.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        listContacts.adapter = usersAdapter

        textUserSearch.onTextChanged { username ->
            val query = realm.where<User>().run {
                if (username.isNotEmpty()) {
                    return@run this.contains("userName", username)
                } else {
                    this
                }
            }
            usersAdapter.updateData(query.findAllAsync())
        }

        loadUsers()
    }

    private fun loadUsers() {
        loadingView.show()

        compositeDisposable += apiService
            .getAllUsers()
            .subscribeOn(schedulerProvider.io())
            .doOnNext { userResponse ->
                val users = userResponse.result.map { user -> User.createFromUser(user) }

                Realm.getDefaultInstance().use { r ->
                    r.executeTransaction { realm ->
                        realm.insertOrUpdate(users)
                    }
                }
            }
            .observeOn(schedulerProvider.ui())
            .doFinally { loadingView.hide() }
            .subscribeBy(
                onNext = {
                    println("Saved users to Realm.")
                },
                onError = { e ->
                    e.printStackTrace()
                },
                onComplete = {
                }
            )
    }

    override fun onDestroyView() {
        compositeDisposable.clearIfNotDisposed()
        super.onDestroyView()
        realm.close()
    }
}