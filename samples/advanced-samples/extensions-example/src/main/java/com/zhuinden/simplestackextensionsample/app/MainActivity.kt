package com.zhuinden.simplestackextensionsample.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = MainActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.step9Root)

        val app = application as CustomApplication

        val globalServices = app.globalServices

        authenticationManager = globalServices.get()

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .setScopedServices(ServiceProvider())
            .setGlobalServices(globalServices)
            .install(
                this, binding.step9Root, History.of(
                when {
                    authenticationManager.isAuthenticated() -> ProfileKey(authenticationManager.getAuthenticatedUser())
                    else -> LoginKey
                }
            ))
    }

    override fun onBackPressed() {
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }

    override fun onDestroy() {
        if (isFinishing) {
            authenticationManager.clearRegistration() // just for sample repeat sake
        }

        super.onDestroy()
    }
}