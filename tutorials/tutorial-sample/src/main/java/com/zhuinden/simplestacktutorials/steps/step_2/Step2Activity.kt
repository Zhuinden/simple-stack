package com.zhuinden.simplestacktutorials.steps.step_2

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestacktutorials.databinding.ActivityStep2Binding
import com.zhuinden.simplestacktutorials.utils.hide
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.safe
import com.zhuinden.simplestacktutorials.utils.show
import kotlinx.parcelize.Parcelize

private val Context.backstack: Backstack
    get() = Navigator.getBackstack(this)

class Step2Activity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {

    @Suppress("DEPRECATION")
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!Navigator.onBackPressed(this@Step2Activity)) {
                this.remove()
                onBackPressed() // this is the reliable way to handle back for now
                this@Step2Activity.onBackPressedDispatcher.addCallback(this)
            }
        }
    }

    sealed class Screens : Parcelable {
        @Parcelize
        data object First : Screens()

        @Parcelize
        data object Second : Screens()
    }

    private lateinit var binding: ActivityStep2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is the reliable way to handle back for now

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .install(this, binding.step2Root, History.of(Screens.First)) // auto-install backstack
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        val newKey = stateChange.topNewKey<Screens>()

        when (newKey) {
            Screens.First -> {
                binding.step2Text.text = "First Screen"

                binding.step2Button.show()
                binding.step2Button.onClick {
                    backstack.goTo(Screens.Second)
                }
            }
            Screens.Second -> {
                binding.step2Text.text = "Second Screen"

                binding.step2Button.hide()
            }
        }.safe()
    }
}