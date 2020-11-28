package com.zhuinden.simplestacktutorials.steps.step_4

import com.zhuinden.simplestacktutorials.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Step4SecondScreen(private val placeholder: String = "") :
    Step4Screen() {  // generate equals/hashCode/toString
    override fun layout(): Int = R.layout.step4_second_view
}