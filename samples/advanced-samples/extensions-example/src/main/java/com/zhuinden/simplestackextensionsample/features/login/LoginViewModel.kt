package com.zhuinden.simplestackextensionsample.features.login

import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.rxvalidatebykt.validateBy
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestackextensionsample.app.AuthenticationManager
import com.zhuinden.simplestackextensionsample.features.profile.ProfileKey
import com.zhuinden.simplestackextensionsample.features.registration.EnterProfileDataKey
import com.zhuinden.simplestackextensionsample.utils.get
import com.zhuinden.simplestackextensionsample.utils.observe
import com.zhuinden.simplestackextensionsample.utils.set
import com.zhuinden.statebundle.StateBundle
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class LoginViewModel(
    private val authenticationManager: AuthenticationManager,
    private val backstack: Backstack
) : Bundleable, ScopedServices.Registered {
    private val compositeDisposable = CompositeDisposable()

    val username = BehaviorRelay.createDefault("")
    val password = BehaviorRelay.createDefault("")

    private val isLoginEnabledRelay = BehaviorRelay.createDefault(false)
    val isLoginEnabled: Observable<Boolean> = isLoginEnabledRelay

    override fun onServiceRegistered() {
        validateBy(
            username.map { it.isNotBlank() },
            password.map { it.isNotBlank() },
        ).observe(compositeDisposable) {
            isLoginEnabledRelay.set(it)
        }
    }

    override fun onServiceUnregistered() {
        compositeDisposable.clear()
    }

    fun onLoginClicked() {
        if (isLoginEnabledRelay.get()) {
            authenticationManager.saveRegistration(username.get())
            backstack.setHistory(History.of(ProfileKey(username.get())), StateChange.FORWARD)
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