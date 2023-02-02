package com.community.simplestackkotlindaggerexample.application

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.community.simplestackkotlindaggerexample.R
import com.community.simplestackkotlindaggerexample.databinding.ActivityMainBinding
import com.community.simplestackkotlindaggerexample.screens.home.HomeKey
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger

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

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.container)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is required for `onBackPressedDispatcher` to work correctly

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .install(this, binding.container, History.single(HomeKey))
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }
}