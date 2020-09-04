package com.zhuinden.simplestacktutorials.steps.step_6

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestacktutorials.R

class Step6SecondFragment : KeyedFragment(R.layout.step6_second_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = getKey<Step6SecondScreen>() // get args passed from previous screen
    }
}