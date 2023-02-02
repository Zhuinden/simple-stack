package com.zhuinden.simplestacktutorials.steps.step_4

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestacktutorials.databinding.ActivityStep4Binding

class Step4Activity : AppCompatActivity() {
    @Suppress("DEPRECATION")
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!Navigator.onBackPressed(this@Step4Activity)) {
                this.remove()
                onBackPressed()  // this is the only safe way to manually invoke onBackPressed when using onBackPressedDispatcher`
                this@Step4Activity.onBackPressedDispatcher.addCallback(this)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("RedundantModalityModifier", "deprecation")
    final override fun onBackPressed() { // you cannot use `onBackPressed()` if you use `OnBackPressedDispatcher`
        super.onBackPressed()
    }


    private lateinit var binding: ActivityStep4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStep4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is required for `onBackPressedDispatcher` to work correctly

        Navigator.install(this, binding.step4Root, History.of(Step4FirstScreen()))
    }
}