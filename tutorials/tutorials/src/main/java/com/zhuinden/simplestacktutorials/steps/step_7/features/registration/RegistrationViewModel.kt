package com.zhuinden.simplestacktutorials.steps.step_7.features.registration

import android.content.Context
import com.zhuinden.simplestack.*
import com.zhuinden.simplestacktutorials.steps.step_7.AuthenticationManager
import com.zhuinden.simplestacktutorials.steps.step_7.features.profile.ProfileKey
import com.zhuinden.statebundle.StateBundle

class RegistrationViewModel(
    private val appContext: Context,
    private val backstack: Backstack
): Bundleable, ScopedServices.HandlesBack {
    enum class RegistrationState { // this is actually kinda superfluous/unnecessary but ok
        COLLECT_PROFILE_DATA,
        COLLECT_USER_PASSWORD,
        REGISTRATION_COMPLETED
    }

    private var currentState: RegistrationState = RegistrationState.COLLECT_PROFILE_DATA

    var fullName: String = ""
        private set
    var bio: String = ""
        private set

    var username: String = ""
        private set
    var password: String = ""
        private set

    fun onFullNameChanged(fullName: String) {
        this.fullName = fullName
    }

    fun onBioChanged(bio: String) {
        this.bio = bio
    }

    fun onUsernameChanged(username: String) {
        this.username = username
    }

    fun onPasswordChanged(password: String) {
        this.password = password
    }

    fun onRegisterAndLoginClicked() {
        if(username.isNotBlank() && password.isNotBlank()) {
            currentState = RegistrationState.REGISTRATION_COMPLETED
            AuthenticationManager.saveRegistration(appContext)
            backstack.setHistory(History.of(ProfileKey()), StateChange.FORWARD)
        }
    }

    fun onEnterProfileNextClicked() {
        if(fullName.isNotBlank() && bio.isNotBlank()) {
            currentState = RegistrationState.COLLECT_USER_PASSWORD
            backstack.goTo(CreateLoginCredentialsKey())
        }
    }

    override fun onBackEvent(): Boolean {
        if(currentState == RegistrationState.COLLECT_USER_PASSWORD) {
            currentState = RegistrationState.COLLECT_PROFILE_DATA
            return false // already dispatching, so just go back a screen
        }
        return false
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putSerializable("currentState", currentState)
        putString("username", username)
        putString("password", password)
        putString("fullName", fullName)
        putString("bio", bio)
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            currentState = getSerializable("currentState") as RegistrationState
            username = getString("username", "")
            password = getString("password", "")
            fullName = getString("fullName", "")
            bio = getString("bio", bio)
        }
    }
}