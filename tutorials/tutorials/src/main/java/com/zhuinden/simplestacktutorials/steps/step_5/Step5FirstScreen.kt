package com.zhuinden.simplestacktutorials.steps.step_5

import androidx.fragment.app.Fragment
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Step5FirstScreen(private val placeholder: String = "") :
    Step5Screen() {  // generate equals/hashCode/toString
    override fun instantiateFragment(): Fragment = Step5FirstFragment()
}