package com.zhuinden.simplestacktutorials.steps.step_8.features.main

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.steps.step_8.features.form.FormKey
import com.zhuinden.simplestacktutorials.utils.onClick
import kotlinx.android.synthetic.main.step8_main_fragment.*

class MainFragment : KeyedFragment(R.layout.step8_main_fragment) {
    private val viewModel by lazy { lookup<MainViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textResult.setText(viewModel.state)

        buttonBeginFlow.onClick {
            backstack.goTo(FormKey())
        }
    }
}