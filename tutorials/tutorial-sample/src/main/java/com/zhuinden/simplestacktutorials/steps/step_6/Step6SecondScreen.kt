package com.zhuinden.simplestacktutorials.steps.step_6

import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize

@Parcelize
data class Step6SecondScreen(private val noArg: String = "") : Step6Screen() {
    override fun instantiateFragment(): Fragment = Step6SecondFragment()
}