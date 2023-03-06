package com.zhuinden.simplestacktutorials.steps.step_3

import com.zhuinden.simplestack.navigator.Navigator
import kotlinx.parcelize.Parcelize

@Parcelize
data object Step3FirstScreen : Step3Screen() {
    operator fun invoke() = this

    override val titleText: String
        get() = "First title"
    override val centerText: String
        get() = "First screen"
    override val buttonConfiguration: Step3ButtonConfiguration?
        get() = Step3ButtonConfiguration("Navigate forward") { view ->
            Navigator.getBackstack(view.context).goTo(Step3SecondScreen())
        }
}