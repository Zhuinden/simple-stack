package com.zhuinden.simplestacktutorials.steps.step_3

import kotlinx.parcelize.Parcelize

@Parcelize
data object Step3SecondScreen : Step3Screen() {
    operator fun invoke() = this

    override val titleText: String
        get() = "Second title"
    override val centerText: String
        get() = "Second screen"
    override val buttonConfiguration: Step3ButtonConfiguration?
        get() = null
}