package com.zhuinden.simplestackextensionsample.features.login

import android.content.Context
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.rxcombinetuplekt.combineTuple
import com.zhuinden.simplestack.*
import com.zhuinden.simplestackextensionsample.app.AuthenticationManager
import com.zhuinden.simplestackextensionsample.features.profile.ProfileKey
import com.zhuinden.simplestackextensionsample.features.registration.EnterProfileDataKey
import com.zhuinden.simplestackextensionsample.utils.get
import com.zhuinden.simplestackextensionsample.utils.set
import com.zhuinden.statebundle.StateBundle
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class LoginViewModel(
    private val appContext: Context,
    private val backstack: Backstack
) : Bundleable, ScopedServices.Registered {
    private val compositeDisposable = CompositeDisposable()

    val username = BehaviorRelay.createDefault("")
    val password = BehaviorRelay.createDefault("")

    val isLoginEnabled = BehaviorRelay.createDefault(false)

    override fun onServiceRegistered() {
        combineTuple(username, password)
            .subscribeBy { (username, password) ->
                isLoginEnabled.set(username.isNotBlank() && password.isNotBlank())
            }.addTo(compositeDisposable)
    }

    override fun onServiceUnregistered() {
        compositeDisposable.clear()
    }

    fun onLoginClicked() {
        if (isLoginEnabled.get()) {
            AuthenticationManager.saveRegistration(appContext)
            backstack.setHistory(History.of(ProfileKey()), StateChange.FORWARD)
        }
    }

    fun onRegisterClicked() {
        backstack.goTo(EnterProfileDataKey())
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putString("username", username.get())
        putString("password", password.get())
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            username.set(getString("username", ""))
            password.set(getString("password", ""))
        }
    }
}