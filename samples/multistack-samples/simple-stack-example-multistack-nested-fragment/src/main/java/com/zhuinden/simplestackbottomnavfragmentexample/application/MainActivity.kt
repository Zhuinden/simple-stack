package com.zhuinden.simplestackbottomnavfragmentexample.application

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackbottomnavfragmentexample.R
import com.zhuinden.simplestackbottomnavfragmentexample.features.initial.InitialScreen
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    @Suppress("DEPRECATION")
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!Navigator.onBackPressed(this@MainActivity)) {
                this.remove()
                onBackPressed()  // this is the only safe way to manually invoke onBackPressed when using onBackPressedDispatcher`
                this@MainActivity.onBackPressedDispatcher.addCallback(this)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("RedundantModalityModifier", "deprecation")
    final override fun onBackPressed() { // you cannot use `onBackPressed()` if you use `OnBackPressedDispatcher`
        super.onBackPressed()
    }


    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.container)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is required for `onBackPressedDispatcher` to work correctly

        Navigator.configure()
            .setScopedServices(DefaultServiceProvider())
            .setStateChanger(SimpleStateChanger(this))
            .install(this, findViewById(R.id.container), History.of(InitialScreen()))
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }
}