package com.zhuinden.simplestacktutorials.steps.step_8.features.form

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged
import kotlinx.android.synthetic.main.step8_form_fragment.*

class FormFragment : KeyedFragment(R.layout.step8_form_fragment) {
    private val viewModel by lazy { lookup<FormViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputSomeData.setText(viewModel.someData)
        inputMoreData.setText(viewModel.moreData)

        inputSomeData.onTextChanged { viewModel.someData = it }
        inputMoreData.onTextChanged { viewModel.moreData = it }

        buttonPassResults.onClick { viewModel.onButtonClicked() }
    }
}