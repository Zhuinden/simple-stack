package com.community.simplestackkotlindaggerexample.application

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.community.simplestackkotlindaggerexample.R
import com.community.simplestackkotlindaggerexample.databinding.ActivityMainBinding
import com.community.simplestackkotlindaggerexample.screens.home.HomeKey
import com.zhuinden.simplestack.*
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger

class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private lateinit var backstack: Backstack

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            backstack.goBack()
        }
    }

    private val updateBackPressedCallback = AheadOfTimeWillHandleBackChangedListener {
        backPressedCallback.isEnabled = it
    }

    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is the reliable way to handle back for now

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.container)

        backstack = Navigator.configure()
            .setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME)
            .setStateChanger(SimpleStateChanger(this))
            .install(this, binding.container, History.single(HomeKey))


        backPressedCallback.isEnabled = backstack.willHandleAheadOfTimeBack()
        backstack.addAheadOfTimeWillHandleBackChangedListener(updateBackPressedCallback)
    }

    override fun onDestroy() {
        backstack.removeAheadOfTimeWillHandleBackChangedListener(updateBackPressedCallback)
        super.onDestroy()
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }
}