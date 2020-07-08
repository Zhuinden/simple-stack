package com.zhuinden.simplestacktutorials.steps.step_3

import android.view.View

data class Step3ButtonConfiguration(
    val buttonText: String,
    val buttonAction: (View) -> Unit
)