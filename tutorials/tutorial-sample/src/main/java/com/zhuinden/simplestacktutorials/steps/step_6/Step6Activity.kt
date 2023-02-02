package com.zhuinden.simplestacktutorials.steps.step_6

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.databinding.ActivityStep6Binding

class Step6Activity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {

    @Suppress("DEPRECATION")
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!Navigator.onBackPressed(this@Step6Activity)) {
                this.remove()
                onBackPressed()  // this is the only safe way to manually invoke onBackPressed when using onBackPressedDispatcher`
                this@Step6Activity.onBackPressedDispatcher.addCallback(this)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("RedundantModalityModifier", "deprecation")
    final override fun onBackPressed() { // you cannot use `onBackPressed()` if you use `OnBackPressedDispatcher`
        super.onBackPressed()
    }


    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityStep6Binding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.step6Root)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is required for `onBackPressedDispatcher` to work correctly

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .install(this, binding.step6Root, History.of(Step6FirstScreen()))
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }
}