package com.zhuinden.simplestacktutorials.steps.step_7

import android.content.Context
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.databinding.ActivityStep7Binding
import com.zhuinden.simplestacktutorials.steps.step_7.features.login.LoginKey
import com.zhuinden.simplestacktutorials.steps.step_7.features.profile.ProfileKey

class Step7Activity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    @Suppress("DEPRECATION")
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!Navigator.onBackPressed(this@Step7Activity)) {
                this.remove()
                onBackPressed() // this is the reliable way to handle back for now
                this@Step7Activity.onBackPressedDispatcher.addCallback(this)
            }
        }
    }

    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger
    private lateinit var appContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityStep7Binding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is the reliable way to handle back for now

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.step7Root)
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
                this, binding.step7Root, History.of(
                    when {
                        AuthenticationManager.isAuthenticated(appContext) -> ProfileKey()
                        else -> LoginKey()
                    }
                )
            )
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