package com.community.simplestackkotlindaggerexample.screens.users

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.community.simplestackkotlindaggerexample.R
import com.community.simplestackkotlindaggerexample.application.Injector
import com.community.simplestackkotlindaggerexample.core.schedulers.SchedulerProvider
import com.community.simplestackkotlindaggerexample.data.api.ApiService
import com.community.simplestackkotlindaggerexample.data.database.User
import com.community.simplestackkotlindaggerexample.databinding.FragmentUsersBinding
import com.community.simplestackkotlindaggerexample.utils.clearIfNotDisposed
import com.community.simplestackkotlindaggerexample.utils.hide
import com.community.simplestackkotlindaggerexample.utils.onTextChanged
import com.community.simplestackkotlindaggerexample.utils.show
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.realm.Realm
import io.realm.kotlin.where
import javax.inject.Inject

class UsersFragment : KeyedFragment(R.layout.fragment_users) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentUsersBinding.bind(view)

        realm = Realm.getDefaultInstance()

        usersAdapter = UsersAdapter(realm.where<User>().findAllAsync())

        binding.listContacts.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.listContacts.adapter = usersAdapter

        binding.textUserSearch.onTextChanged { username ->
            val query = realm.where<User>().run {
                if (username.isNotEmpty()) {
                    return@run this.contains("userName", username)
                } else {
                    this
                }
            }
            usersAdapter.updateData(query.findAllAsync())
        }


        fun loadUsers() {
            binding.loadingView.root.show()

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
                .doFinally { binding.loadingView.root.hide() }
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


        loadUsers()
    }

    override fun onDestroyView() {
        compositeDisposable.clearIfNotDisposed()
        super.onDestroyView()
        realm.close()
    }
}