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
import com.zhuinden.simplestack.AheadOfTimeWillHandleBackChangedListener
import com.zhuinden.simplestack.BackHandlingModel
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.lifecyclektx.observeAheadOfTimeWillHandleBackChanged
import com.zhuinden.simplestackextensions.navigatorktx.backstack

class MainActivity : AppCompatActivity() {
    private lateinit var backstack: Backstack

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            backstack.goBack()
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

        backstack = Navigator.configure()
            .setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME)
            .install(this, binding.container, History.single(HomeKey))

        backPressedCallback.isEnabled = backstack.willHandleAheadOfTimeBack()
        backstack.observeAheadOfTimeWillHandleBackChanged(this, backPressedCallback::isEnabled::set)
    }
}
