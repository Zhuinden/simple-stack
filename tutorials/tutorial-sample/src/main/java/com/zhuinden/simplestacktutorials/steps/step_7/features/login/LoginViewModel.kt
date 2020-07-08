package com.zhuinden.simplestacktutorials.steps.step_7.features.login

import android.content.Context
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestacktutorials.steps.step_7.AuthenticationManager
import com.zhuinden.simplestacktutorials.steps.step_7.features.profile.ProfileKey
import com.zhuinden.simplestacktutorials.steps.step_7.features.registration.EnterProfileDataKey
import com.zhuinden.statebundle.StateBundle

class LoginViewModel(
    private val appContext: Context,
    private val backstack: Backstack
) : Bundleable {
    private val eventEmitter: EventEmitter<String> = EventEmitter()
    val events: EventSource<String> get() = eventEmitter

    var username: String = ""
        private set
    var password: String = ""
        private set

    fun onUsernameChanged(username: String) {
        this.username = username
    }

    fun onPasswordChanged(password: String) {
        this.password = password
    }

    fun onLoginClicked() {
        if (username.isNotBlank() && password.isNotBlank()) {
            AuthenticationManager.saveRegistration(appContext)
            backstack.setHistory(History.of(ProfileKey()), StateChange.FORWARD)
        } else {
            eventEmitter.emit("Invalid username or password!")
        }
    }

    fun onRegisterClicked() {
        backstack.goTo(EnterProfileDataKey())
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putString("username", username)
        putString("password", password)
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            username = getString("username", "")
            password = getString("password", "")
        }
    }
}