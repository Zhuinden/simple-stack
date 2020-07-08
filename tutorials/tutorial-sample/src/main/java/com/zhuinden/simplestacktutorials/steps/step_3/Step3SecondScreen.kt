package com.zhuinden.simplestacktutorials.steps.step_3

import kotlinx.android.parcel.Parcelize

@Parcelize
class Step3SecondScreen : Step3Screen() {
    override val titleText: String
        get() = "Second title"
    override val centerText: String
        get() = "Second screen"
    override val buttonConfiguration: Step3ButtonConfiguration?
        get() = null
}