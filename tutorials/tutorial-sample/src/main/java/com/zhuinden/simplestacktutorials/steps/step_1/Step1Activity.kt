package com.zhuinden.simplestacktutorials.steps.step_1

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestacktutorials.databinding.ActivityStep1Binding
import com.zhuinden.simplestacktutorials.utils.hide
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.safe
import com.zhuinden.simplestacktutorials.utils.show
import kotlinx.parcelize.Parcelize

class Step1Activity : AppCompatActivity(), StateChanger {
    private lateinit var backstack: Backstack

    sealed class Screens : Parcelable { // a screen should be Parcelable so they can be put to Bundle.
        @Parcelize
        object First : Screens()

        @Parcelize
        object Second : Screens()
    }

    private lateinit var binding: ActivityStep1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION") // don't worry, Navigator will handle it in step 2
        backstack = lastCustomNonConfigurationInstance?.let { it as Backstack }
            ?: Backstack().also { backstack ->
                backstack.setup(History.of(Screens.First))

                savedInstanceState?.let { bundle ->
                    backstack.fromBundle(bundle.getParcelable("BACKSTACK_STATE"))
                }
            }

        backstack.setStateChanger(this) // handle navigation in this class
    }

    override fun onBackPressed() {
        if (!backstack.goBack()) {
            super.onBackPressed()
        }
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        if (stateChange.isTopNewKeyEqualToPrevious) { // handle when you navigate to the same screen twice
            completionCallback.stateChangeComplete() // don't do anything in this case
            return
        }

        val newKey = stateChange.topNewKey<Screens>()

        when (newKey) {
            Screens.First -> {
                binding.step1Text.text = "First Screen"

                binding.step1Button.show()
                binding.step1Button.onClick {
                    backstack.goTo(Screens.Second)
                }
            }
            Screens.Second -> {
                binding.step1Text.text = "Second Screen"

                binding.step1Button.hide()
            }
        }.safe()

        completionCallback.stateChangeComplete()
    }

    // some android-specific plumbing code, Navigator hides this in step 2
    override fun onRetainCustomNonConfigurationInstance(): Any? = backstack

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("BACKSTACK_STATE", backstack.toBundle())
    }

    override fun onResume() {
        super.onResume()
        backstack.reattachStateChanger()
    }

    override fun onPause() {
        backstack.detachStateChanger()
        super.onPause()
    }

    override fun onDestroy() {
        backstack.executePendingStateChange()

        if (isFinishing) {
            backstack.finalizeScopes()
        }

        super.onDestroy()
    }
}