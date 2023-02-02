package com.zhuinden.simplestacktutorials.steps.step_9

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
import com.zhuinden.simplestacktutorials.databinding.ActivityStep9Binding
import com.zhuinden.simplestacktutorials.steps.step_9.features.login.LoginKey
import com.zhuinden.simplestacktutorials.steps.step_9.features.profile.ProfileKey

class Step9Activity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    @Suppress("DEPRECATION")
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!Navigator.onBackPressed(this@Step9Activity)) {
                this.remove()
                onBackPressed()  // this is the only safe way to manually invoke onBackPressed when using onBackPressedDispatcher`
                this@Step9Activity.onBackPressedDispatcher.addCallback(this)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("RedundantModalityModifier", "deprecation")
    final override fun onBackPressed() { // you cannot use `onBackPressed()` if you use `OnBackPressedDispatcher`
        super.onBackPressed()
    }

    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger
    private lateinit var appContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityStep9Binding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.step9Root)
        appContext = applicationContext

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is required for `onBackPressedDispatcher` to work correctly

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .setScopedServices(ServiceProvider())
            .setGlobalServices(
                GlobalServices.builder()
                    .addService("appContext", appContext)
                    .build()
            )
            .install(
                this, binding.step9Root, History.of(
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