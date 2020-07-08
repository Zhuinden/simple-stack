package com.zhuinden.simplestacktutorials.steps.step_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.utils.onClick
import kotlinx.android.synthetic.main.step5_second_fragment.*

class Step5SecondFragment : Step5BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.step5_second_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = getScreen<Step5SecondScreen>() // get args passed from previous screen

        step5SecondBack.onClick {
            backstack.goBack()
        }
    }
}