package com.zhuinden.simplestackextensionscomposesample.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.navigatorktx.androidContentFrame
import com.zhuinden.simplestackextensions.servicesktx.get
import com.zhuinden.simplestackextensionscomposesample.R
import com.zhuinden.simplestackextensionscomposesample.core.FragmentStateChanger
import com.zhuinden.simplestackextensionscomposesample.features.login.LoginKey
import com.zhuinden.simplestackextensionscomposesample.features.profile.ProfileKey

class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private lateinit var fragmentStateChanger: FragmentStateChanger

    private lateinit var authenticationManager: AuthenticationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        val app = application as CustomApplication
        val globalServices = app.globalServices

        authenticationManager = globalServices.get()

        fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.container)

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .setScopedServices(ServiceProvider())
            .setGlobalServices(globalServices)
            .install(
                this, androidContentFrame, History.of(
                    when {
                        authenticationManager.isAuthenticated() -> ProfileKey(authenticationManager.getAuthenticatedUser())
                        else -> LoginKey()
                    }
                )
            )
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