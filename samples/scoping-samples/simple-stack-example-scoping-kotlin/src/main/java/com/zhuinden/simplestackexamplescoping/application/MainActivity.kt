package com.zhuinden.simplestackexamplescoping.application

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.*
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackexamplescoping.R
import com.zhuinden.simplestackexamplescoping.databinding.ActivityMainBinding
import com.zhuinden.simplestackexamplescoping.features.words.WordListKey
import com.zhuinden.simplestackexamplescoping.utils.viewBinding
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

/**
 * Created by Zhuinden on 2018.09.17.
 */
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

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is the reliable way to handle back for now

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.container)

        backstack = Navigator.configure()
            .setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME)
            .setStateChanger(SimpleStateChanger(this))
            .setScopedServices(DefaultServiceProvider())
            .install(this, binding.container, History.of(WordListKey))

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
