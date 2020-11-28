package com.zhuinden.simplestacktutorials.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestacktutorials.databinding.ActivityMainBinding
import com.zhuinden.simplestacktutorials.steps.step_1.Step1Activity
import com.zhuinden.simplestacktutorials.steps.step_2.Step2Activity
import com.zhuinden.simplestacktutorials.steps.step_3.Step3Activity
import com.zhuinden.simplestacktutorials.steps.step_4.Step4Activity
import com.zhuinden.simplestacktutorials.steps.step_5.Step5Activity
import com.zhuinden.simplestacktutorials.steps.step_6.Step6Activity
import com.zhuinden.simplestacktutorials.steps.step_7.Step7Activity
import com.zhuinden.simplestacktutorials.steps.step_8.Step8Activity
import com.zhuinden.simplestacktutorials.steps.step_9.Step9Activity
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.startActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonStep1.onClick {
            startActivity<Step1Activity>()
        }

        binding.buttonStep2.onClick {
            startActivity<Step2Activity>()
        }

        binding.buttonStep3.onClick {
            startActivity<Step3Activity>()
        }

        binding.buttonStep4.onClick {
            startActivity<Step4Activity>()
        }

        binding.buttonStep5.onClick {
            startActivity<Step5Activity>()
        }

        binding.buttonStep6.onClick {
            startActivity<Step6Activity>()
        }

        binding.buttonStep7.onClick {
            startActivity<Step7Activity>()
        }

        binding.buttonStep8.onClick {
            startActivity<Step8Activity>()
        }

        binding.buttonStep9.onClick {
            startActivity<Step9Activity>()
        }
    }
}
