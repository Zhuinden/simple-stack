package com.zhuinden.simplestacktutorials.steps.step_6

import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize

@Parcelize
data class Step6FirstScreen(private val noArg: String = "") : Step6Screen() {
    override fun instantiateFragment(): Fragment = Step6FirstFragment()
}