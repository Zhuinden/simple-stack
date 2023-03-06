package com.zhuinden.simplestacktutorials.steps.step_5

import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize

@Parcelize
data object Step5SecondScreen : Step5Screen() {  // generate equals/hashCode/toString
    operator fun invoke() = this

    override fun instantiateFragment(): Fragment = Step5SecondFragment()
}