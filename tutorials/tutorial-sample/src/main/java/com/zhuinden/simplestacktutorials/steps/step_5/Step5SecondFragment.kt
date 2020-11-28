package com.zhuinden.simplestacktutorials.steps.step_5

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.databinding.Step5SecondFragmentBinding
import com.zhuinden.simplestacktutorials.utils.onClick

class Step5SecondFragment : Step5BaseFragment(R.layout.step5_second_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = getScreen<Step5SecondScreen>() // get args passed from previous screen

        val binding = Step5SecondFragmentBinding.bind(view)

        binding.step5SecondBack.onClick {
            backstack.goBack()
        }
    }
}