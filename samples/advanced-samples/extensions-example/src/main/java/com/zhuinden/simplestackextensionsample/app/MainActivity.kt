package com.zhuinden.simplestackextensionsample.app

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensionsample.R
import com.zhuinden.simplestackextensionsample.features.login.LoginKey
import com.zhuinden.simplestackextensionsample.features.profile.ProfileKey
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger
    private lateinit var appContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.step9Root)
        appContext = applicationContext

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .setScopedServices(ServiceProvider())
            .setGlobalServices(
                GlobalServices.builder()
                    .addService("appContext", appContext)
                    .build()
            )
            .install(
                this, step9Root, History.of(
                    when {
                        AuthenticationManager.isAuthenticated(appContext) -> ProfileKey()
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
            AuthenticationManager.clearRegistration(appContext) // just for sample repeat sake
        }

        super.onDestroy()
    }
}