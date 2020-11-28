package com.zhuinden.simplestacktutorials.steps.step_5

import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize

@Parcelize
data class Step5SecondScreen(private val placeholder: String = "") :
    Step5Screen() {  // generate equals/hashCode/toString
    override fun instantiateFragment(): Fragment = Step5SecondFragment()
}