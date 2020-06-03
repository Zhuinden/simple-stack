package com.zhuinden.simplestackextensionsample.features.registration

import android.content.Context
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.rxcombinetuplekt.combineTuple
import com.zhuinden.simplestack.*
import com.zhuinden.simplestackextensionsample.app.AuthenticationManager
import com.zhuinden.simplestackextensionsample.features.profile.ProfileKey
import com.zhuinden.simplestackextensionsample.utils.get
import com.zhuinden.simplestackextensionsample.utils.set
import com.zhuinden.statebundle.StateBundle
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class RegistrationViewModel(
    private val authenticationManager: AuthenticationManager,
    private val backstack: Backstack
) : Bundleable, ScopedServices.Registered, ScopedServices.HandlesBack {
    enum class RegistrationState { // this is actually kinda superfluous/unnecessary but ok
        COLLECT_PROFILE_DATA,
        COLLECT_USER_PASSWORD,
        REGISTRATION_COMPLETED
    }

    private var currentState: RegistrationState = RegistrationState.COLLECT_PROFILE_DATA

    private val compositeDisposable = CompositeDisposable()

    val fullName = BehaviorRelay.createDefault("")
    val bio = BehaviorRelay.createDefault("")

    val username = BehaviorRelay.createDefault("")
    val password = BehaviorRelay.createDefault("")

    val isRegisterAndLoginEnabled = BehaviorRelay.createDefault(false)
    val isEnterProfileNextEnabled = BehaviorRelay.createDefault(false)

    override fun onServiceRegistered() {
        combineTuple(fullName, bio)
            .subscribeBy { (fullName, bio) ->
                isEnterProfileNextEnabled.set(fullName.isNotBlank() && bio.isNotBlank())
            }.addTo(compositeDisposable)

        combineTuple(username, password)
            .subscribeBy { (username, password) ->
                isRegisterAndLoginEnabled.set(username.isNotBlank() && password.isNotBlank())
            }.addTo(compositeDisposable)
    }

    override fun onServiceUnregistered() {
        compositeDisposable.clear()
    }

    fun onRegisterAndLoginClicked() {
        if (isRegisterAndLoginEnabled.get()) {
            currentState = RegistrationState.REGISTRATION_COMPLETED
            authenticationManager.saveRegistration()
            backstack.setHistory(History.of(ProfileKey()), StateChange.FORWARD)
        }
    }

    fun onEnterProfileNextClicked() {
        if (isEnterProfileNextEnabled.get()) {
            currentState = RegistrationState.COLLECT_USER_PASSWORD
            backstack.goTo(CreateLoginCredentialsKey())
        }
    }

    override fun onBackEvent(): Boolean {
        if (currentState == RegistrationState.COLLECT_USER_PASSWORD) {
            currentState = RegistrationState.COLLECT_PROFILE_DATA
            return false // already dispatching, so just go back a screen
        }
        return false
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putSerializable("currentState", currentState)
        putString("username", username.get())
        putString("password", password.get())
        putString("fullName", fullName.get())
        putString("bio", bio.get())
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            currentState = getSerializable("currentState") as RegistrationState
            username.set(getString("username", ""))
            password.set(getString("password", ""))
            fullName.set(getString("fullName", ""))
            bio.set(getString("bio", ""))
        }
    }
}