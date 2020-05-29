package com.zhuinden.navigationexamplekotlinview.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.navigationexamplekotlinview.R
import com.zhuinden.navigationexamplekotlinview.screens.DashboardKey
import com.zhuinden.navigationexamplekotlinview.screens.HomeKey
import com.zhuinden.navigationexamplekotlinview.screens.NotificationKey
import com.zhuinden.navigationexamplekotlinview.utils.replaceHistory
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.Navigator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener { item ->
            val backstack = Navigator.getBackstack(this)
            when (item.itemId) {
                R.id.navigation_home -> {
                    backstack.replaceHistory(HomeKey())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_dashboard -> {
                    backstack.replaceHistory(DashboardKey())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    backstack.replaceHistory(NotificationKey())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        Navigator.install(this, root, History.single(HomeKey()))
    }

    override fun onBackPressed() {
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }
}
