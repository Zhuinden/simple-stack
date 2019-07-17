package com.zhuinden.simplestackexamplekotlinfragment.application

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackexamplekotlinfragment.R
import com.zhuinden.simplestackexamplekotlinfragment.core.navigation.FragmentStateChanger
import com.zhuinden.simplestackexamplekotlinfragment.screens.DashboardKey
import com.zhuinden.simplestackexamplekotlinfragment.screens.HomeKey
import com.zhuinden.simplestackexamplekotlinfragment.screens.NotificationKey
import com.zhuinden.utils.backstack
import com.zhuinden.utils.replaceHistory
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Owner on 2017.11.13.
 */
class MainActivity : AppCompatActivity(), StateChanger {
    private lateinit var fragmentStateChanger: FragmentStateChanger

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

        fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.root)

        Navigator.configure()
            .setStateChanger(this)
            .install(this, root, History.single(HomeKey()))
    }

    override fun onBackPressed() {
        if (!backstack.goBack()) {
            super.onBackPressed()
        }
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        if (stateChange.isTopNewKeyEqualToPrevious) {
            completionCallback.stateChangeComplete()
            return
        }
        fragmentStateChanger.handleStateChange(stateChange)
        completionCallback.stateChangeComplete()
    }
}
