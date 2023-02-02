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

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is required for `onBackPressedDispatcher` to work correctly

        Navigator.install(this, binding.container, History.single(HomeKey))
    }
}
