package com.zhuinden.simplestackextensionscomposesample.features.registration

import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.rxvalidatebykt.validateBy
import com.zhuinden.simplestack.*
import com.zhuinden.simplestackextensionscomposesample.app.AuthenticationManager
import com.zhuinden.simplestackextensionscomposesample.features.profile.ProfileKey
import com.zhuinden.simplestackextensionscomposesample.utils.get
import com.zhuinden.simplestackextensionscomposesample.utils.set
import com.zhuinden.statebundle.StateBundle
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class RegistrationViewModel(
    private val authenticationManager: AuthenticationManager,
    private val backstack: Backstack
) : Bundleable, ScopedServices.Registered {
    private val compositeDisposable = CompositeDisposable()

    val fullName = BehaviorRelay.createDefault("")
    val bio = BehaviorRelay.createDefault("")

    val username = BehaviorRelay.createDefault("")
    val password = BehaviorRelay.createDefault("")

    private val isRegisterAndLoginEnabledRelay = BehaviorRelay.createDefault(false)
    val isRegisterAndLoginEnabled: Observable<Boolean> = isRegisterAndLoginEnabledRelay

    private val isEnterProfileNextEnabledRelay = BehaviorRelay.createDefault(false)
    val isEnterProfileNextEnabled: Observable<Boolean> = isEnterProfileNextEnabledRelay

    override fun onServiceRegistered() {
        validateBy(
            fullName.map { it.isNotBlank() },
            bio.map { it.isNotBlank() }
        ).subscribeBy { isEnabled ->
            isEnterProfileNextEnabledRelay.set(isEnabled)
        }.addTo(compositeDisposable)

        validateBy(
            username.map { it.isNotBlank() },
            password.map { it.isNotBlank() }
        ).subscribeBy { isEnabled ->
            isRegisterAndLoginEnabledRelay.set(isEnabled)
        }.addTo(compositeDisposable)
    }

    override fun onServiceUnregistered() {
        compositeDisposable.clear()
    }

    fun onRegisterAndLoginClicked() {
        if (isRegisterAndLoginEnabledRelay.get()) {
            val username = username.get()
            authenticationManager.saveRegistration(username)
            backstack.setHistory(History.of(ProfileKey(username)), StateChange.REPLACE)
        }
    }

    fun onEnterProfileNextClicked() {
        if (isEnterProfileNextEnabledRelay.get()) {
            backstack.goTo(CreateLoginCredentialsKey)
        }
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putString("username", username.get())
        putString("password", password.get())
        putString("fullName", fullName.get())
        putString("bio", bio.get())
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            username.set(getString("username", ""))
            password.set(getString("password", ""))
            fullName.set(getString("fullName", ""))
            bio.set(getString("bio", ""))
        }
    }
}