package com.zhuinden.simplestackexamplekotlinfragment.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackexamplekotlinfragment.R
import com.zhuinden.simplestackexamplekotlinfragment.screens.DashboardKey
import com.zhuinden.simplestackexamplekotlinfragment.screens.HomeKey
import com.zhuinden.simplestackexamplekotlinfragment.screens.NotificationKey
import com.zhuinden.simplestackexamplekotlinfragment.utils.replaceHistory
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensions.navigatorktx.backstack
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Owner on 2017.11.13.
 */
class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener { item ->
            val destination = when (item.itemId) {
                R.id.navigation_home -> HomeKey()
                R.id.navigation_dashboard -> DashboardKey()
                R.id.navigation_notifications -> NotificationKey()
                else -> null
            }

            destination?.let { key ->
                backstack.replaceHistory(key)
                true
            } ?: false
        }

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.root)

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .install(this, root, History.single(HomeKey()))
    }

    override fun onBackPressed() {
        if (!backstack.goBack()) {
            super.onBackPressed()
        }
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }
}
