package com.zhuinden.simplestacktutorials.steps.step_3

import android.app.Activity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.*
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.lifecyclektx.observeAheadOfTimeWillHandleBackChanged
import com.zhuinden.simplestacktutorials.databinding.ActivityStep3Binding
import com.zhuinden.simplestacktutorials.utils.hide
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.show
import com.zhuinden.simplestacktutorials.utils.showIf

private val Activity.backstack: Backstack
    get() = Navigator.getBackstack(this)

class Step3Activity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private lateinit var backstack: Backstack

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            backstack.goBack()
        }
    }

    private val updateBackPressedCallback = AheadOfTimeWillHandleBackChangedListener {
        backPressedCallback.isEnabled = it
    }

    private lateinit var binding: ActivityStep3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is the reliable way to handle back for now

        binding.step3TitleButtonBack.onClick {
            backstack.goBack()
        }

        backstack = Navigator.configure()
            .setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME)
            .setStateChanger(SimpleStateChanger(this))
            .install(this, binding.step3Root, History.of(Step3FirstScreen()))

        backPressedCallback.isEnabled = backstack.willHandleAheadOfTimeBack()
        backstack.observeAheadOfTimeWillHandleBackChanged(this) {
            backPressedCallback.isEnabled = it
        }
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        val newKeys = stateChange.getNewKeys<Step3Screen>()
        binding.step3TitleButtonBack.showIf { newKeys.size > 1 } // show up if can go back

        val topKey = stateChange.topNewKey<Step3Screen>()

        binding.step3TitleText.text = topKey.titleText
        binding.step3CenterText.text = topKey.centerText

        val buttonConfiguration = topKey.buttonConfiguration
        if (buttonConfiguration == null) {
            binding.step3Button.hide()
        } else {
            binding.step3Button.show()
            binding.step3Button.text = buttonConfiguration.buttonText
            binding.step3Button.onClick(buttonConfiguration.buttonAction)
        }
    }
}