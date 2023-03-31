package com.zhuinden.simplestacktutorials.steps.step_6

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.*
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.databinding.ActivityStep6Binding

class Step6Activity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
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

        val binding = ActivityStep6Binding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is the reliable way to handle back for now

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.step6Root)

        backstack = Navigator.configure()
            .setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME)
            .setStateChanger(SimpleStateChanger(this))
            .install(this, binding.step6Root, History.of(Step6FirstScreen()))

        backPressedCallback.isEnabled = backstack.willHandleAheadOfTimeBack()
        backstack.addAheadOfTimeWillHandleBackChangedListener(updateBackPressedCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        backstack.removeAheadOfTimeWillHandleBackChangedListener(updateBackPressedCallback)
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }
}