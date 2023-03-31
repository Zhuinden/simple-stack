package com.zhuinden.simplestackextensionsample.app

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.*
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensions.servicesktx.get
import com.zhuinden.simplestackextensionsample.R
import com.zhuinden.simplestackextensionsample.databinding.MainActivityBinding
import com.zhuinden.simplestackextensionsample.features.login.LoginKey
import com.zhuinden.simplestackextensionsample.features.profile.ProfileKey

class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger
    private lateinit var authenticationManager: AuthenticationManager

    private lateinit var backstack: Backstack

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            backstack.goBack()
        }
    }

    private val updateBackPressedCallback = AheadOfTimeWillHandleBackChangedListener {
        backPressedCallback.isEnabled = it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = MainActivityBinding.inflate(layoutInflater)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is the reliable way to handle back for now

        setContentView(binding.root)

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.step9Root)

        val app = application as CustomApplication

        val globalServices = app.globalServices

        authenticationManager = globalServices.get()

        backstack = Navigator.configure()
            .setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME)
            .setStateChanger(SimpleStateChanger(this))
            .setScopedServices(ServiceProvider())
            .setGlobalServices(globalServices)
            .install(
                this, binding.step9Root, History.of(
                    when {
                        authenticationManager.isAuthenticated() -> ProfileKey(authenticationManager.getAuthenticatedUser())
                        else -> LoginKey
                    }
                )
            )

        backPressedCallback.isEnabled = backstack.willHandleAheadOfTimeBack()
        backstack.addAheadOfTimeWillHandleBackChangedListener(updateBackPressedCallback)
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }

    override fun onDestroy() {
        backstack.removeAheadOfTimeWillHandleBackChangedListener(updateBackPressedCallback)

        if (isFinishing) {
            authenticationManager.clearRegistration() // just for sample repeat sake
        }

        super.onDestroy()
    }
}