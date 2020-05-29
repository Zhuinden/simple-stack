package com.zhuinden.simplestacktutorials.steps.step_8.features.main

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.steps.step_8.core.navigation.BaseFragment
import com.zhuinden.simplestacktutorials.steps.step_8.core.navigation.backstack
import com.zhuinden.simplestacktutorials.steps.step_8.core.viewmodels.lookup
import com.zhuinden.simplestacktutorials.steps.step_8.features.form.FormKey
import com.zhuinden.simplestacktutorials.utils.onClick
import kotlinx.android.synthetic.main.step8_main_fragment.*

class MainFragment: BaseFragment(R.layout.step8_main_fragment) {
    private val viewModel by lazy { lookup<MainViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textResult.setText(viewModel.state)

        buttonBeginFlow.onClick {
            backstack.goTo(FormKey())
        }
    }
}