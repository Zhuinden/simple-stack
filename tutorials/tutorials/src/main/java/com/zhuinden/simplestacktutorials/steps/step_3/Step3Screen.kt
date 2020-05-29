package com.zhuinden.simplestacktutorials.steps.step_3

import android.os.Parcelable

abstract class Step3Screen : Parcelable {
    abstract val titleText: String

    abstract val centerText: String

    abstract val buttonConfiguration: Step3ButtonConfiguration?
}