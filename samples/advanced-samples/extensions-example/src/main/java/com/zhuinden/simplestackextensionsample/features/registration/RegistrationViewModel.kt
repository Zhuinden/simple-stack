package com.zhuinden.simplestackextensionsample.features.registration

import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.rxvalidatebykt.validateBy
import com.zhuinden.simplestack.*
import com.zhuinden.simplestackextensionsample.app.AuthenticationManager
import com.zhuinden.simplestackextensionsample.features.profile.ProfileKey
import com.zhuinden.simplestackextensionsample.utils.get
import com.zhuinden.simplestackextensionsample.utils.isNotBlank
import com.zhuinden.simplestackextensionsample.utils.observe
import com.zhuinden.simplestackextensionsample.utils.set
import com.zhuinden.statebundle.StateBundle
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

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
        validateBy(fullName.isNotBlank(), bio.isNotBlank()).observe(compositeDisposable) {
            isEnterProfileNextEnabledRelay.set(it)
        }

        validateBy(username.isNotBlank(), password.isNotBlank()).observe(compositeDisposable) {
            isRegisterAndLoginEnabledRelay.set(it)
        }
    }

    override fun onServiceUnregistered() {
        compositeDisposable.clear()
    }

    fun onRegisterAndLoginClicked() {
        if (isRegisterAndLoginEnabledRelay.get()) {
            authenticationManager.saveRegistration(username.get())
            backstack.setHistory(History.of(ProfileKey(username.get())), StateChange.FORWARD)
        }
    }

    fun onEnterProfileNextClicked() {
        if (isEnterProfileNextEnabledRelay.get()) {
            backstack.goTo(CreateLoginCredentialsKey())
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