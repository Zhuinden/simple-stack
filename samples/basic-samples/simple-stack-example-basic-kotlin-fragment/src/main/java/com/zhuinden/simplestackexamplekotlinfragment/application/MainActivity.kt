package com.zhuinden.simplestackexamplekotlinfragment.application

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackexamplekotlinfragment.R
import com.zhuinden.simplestackexamplekotlinfragment.databinding.ActivityMainBinding
import com.zhuinden.simplestackexamplekotlinfragment.screens.DashboardKey
import com.zhuinden.simplestackexamplekotlinfragment.screens.HomeKey
import com.zhuinden.simplestackexamplekotlinfragment.screens.NotificationKey
import com.zhuinden.simplestackexamplekotlinfragment.utils.replaceHistory
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensions.navigatorktx.backstack

/**
 * Created by Owner on 2017.11.13.
 */
class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigation.setOnNavigationItemSelectedListener { item ->
            val destination = when (item.itemId) {
                R.id.navigation_home -> HomeKey
                R.id.navigation_dashboard -> DashboardKey
                R.id.navigation_notifications -> NotificationKey
                else -> null
            }

            destination?.let { key ->
                backstack.replaceHistory(key)
                true
            } ?: false
        }

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
