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
                onBackPressed() // this is the reliable way to handle back for now
                this@Step4Activity.onBackPressedDispatcher.addCallback(this)
            }
        }
    }

    private lateinit var binding: ActivityStep4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is the reliable way to handle back for now

        binding = ActivityStep4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        Navigator.install(this, binding.step4Root, History.of(Step4FirstScreen()))
    }
}