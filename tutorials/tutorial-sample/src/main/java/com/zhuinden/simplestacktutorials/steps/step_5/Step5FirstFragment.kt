package com.zhuinden.simplestacktutorials.steps.step_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.utils.onClick
import kotlinx.android.synthetic.main.step5_first_fragment.*

class Step5FirstFragment : Step5BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.step5_first_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        step5FirstButton.onClick {
            backstack.goTo(Step5SecondScreen())
        }
    }
}