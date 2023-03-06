package com.zhuinden.navigationexamplekotlinview.application

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
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
    @Suppress("DEPRECATION")
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!Navigator.onBackPressed(this@MainActivity)) {
                this.remove()
                onBackPressed() // this is the reliable way to handle back for now
                this@MainActivity.onBackPressedDispatcher.addCallback(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is the reliable way to handle back for now

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
}
