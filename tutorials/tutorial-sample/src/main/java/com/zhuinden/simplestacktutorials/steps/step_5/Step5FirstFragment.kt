package com.zhuinden.simplestacktutorials.steps.step_5

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.databinding.Step5FirstFragmentBinding
import com.zhuinden.simplestacktutorials.utils.onClick

class Step5FirstFragment : Step5BaseFragment(R.layout.step5_first_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = Step5FirstFragmentBinding.bind(view)

        binding.step5FirstButton.onClick {
            backstack.goTo(Step5SecondScreen())
        }
    }
}