package com.zhuinden.simplestacktutorials.steps.step_4

import com.zhuinden.simplestacktutorials.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Step4FirstScreen(private val placeholder: String = "") : Step4Screen() { // generate equals/hashCode/toString
    override fun layout(): Int = R.layout.step4_first_view
}