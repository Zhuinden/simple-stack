package com.zhuinden.navigationexamplekotlinview.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.navigationexamplekotlinview.R
import com.zhuinden.navigationexamplekotlinview.databinding.ActivityMainBinding
import com.zhuinden.navigationexamplekotlinview.screens.DashboardKey
import com.zhuinden.navigationexamplekotlinview.screens.HomeKey
import com.zhuinden.navigationexamplekotlinview.screens.NotificationKey
import com.zhuinden.navigationexamplekotlinview.utils.replaceHistory
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.navigatorktx.backstack

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    backstack.replaceHistory(HomeKey)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_dashboard -> {
                    backstack.replaceHistory(DashboardKey)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    backstack.replaceHistory(NotificationKey)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        Navigator.install(this, binding.container, History.single(HomeKey))
    }

    override fun onBackPressed() {
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }
}
