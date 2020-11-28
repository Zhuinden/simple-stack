package com.zhuinden.simplestacktutorials.steps.step_8.features.form

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.databinding.Step8FormFragmentBinding
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged

class FormFragment : KeyedFragment(R.layout.step8_form_fragment) {
    private val viewModel by lazy { lookup<FormViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = Step8FormFragmentBinding.bind(view)

        binding.inputSomeData.setText(viewModel.someData)
        binding.inputMoreData.setText(viewModel.moreData)

        binding.inputSomeData.onTextChanged { viewModel.someData = it }
        binding.inputMoreData.onTextChanged { viewModel.moreData = it }

        binding.buttonPassResults.onClick { viewModel.onButtonClicked() }
    }
}