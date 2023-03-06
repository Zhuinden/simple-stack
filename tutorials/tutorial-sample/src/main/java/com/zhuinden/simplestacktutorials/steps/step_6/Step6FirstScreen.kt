package com.zhuinden.simplestacktutorials.steps.step_6

import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize

@Parcelize
data object Step6FirstScreen : Step6Screen() {
    operator fun invoke() = this

    override fun instantiateFragment(): Fragment = Step6FirstFragment()
}