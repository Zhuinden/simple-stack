package com.zhuinden.simplestackextensionscomposesample.features.login

import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.rxvalidatebykt.validateBy
import com.zhuinden.simplestack.*
import com.zhuinden.simplestackextensionscomposesample.app.AuthenticationManager
import com.zhuinden.simplestackextensionscomposesample.features.profile.ProfileKey
import com.zhuinden.simplestackextensionscomposesample.features.registration.EnterProfileDataKey
import com.zhuinden.simplestackextensionscomposesample.utils.get
import com.zhuinden.simplestackextensionscomposesample.utils.set
import com.zhuinden.statebundle.StateBundle
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

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
            password.map { it.isNotBlank() }
        ).subscribeBy { isEnabled ->
            isLoginEnabledRelay.set(isEnabled)
        }.addTo(compositeDisposable)
    }

    override fun onServiceUnregistered() {
        compositeDisposable.clear()
    }

    fun onLoginClicked() {
        if (!isLoginEnabledRelay.get()) {
            return
        }

        val username = username.get()
        authenticationManager.saveRegistration(username)
        backstack.setHistory(History.of(ProfileKey(username)), StateChange.REPLACE)
    }

    fun onRegisterClicked() {
        backstack.goTo(EnterProfileDataKey)
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