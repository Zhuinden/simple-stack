package com.zhuinden.simplestacktutorials.steps.step_4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestacktutorials.databinding.ActivityStep4Binding

class Step4Activity : AppCompatActivity() {
    private lateinit var binding: ActivityStep4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStep4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        Navigator.install(this, binding.step4Root, History.of(Step4FirstScreen()))
    }

    override fun onBackPressed() {
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }
}