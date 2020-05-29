package com.zhuinden.simplestacktutorials.steps.step_4

import android.os.Parcelable
import com.zhuinden.simplestack.navigator.DefaultViewKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler

abstract class Step4Screen : DefaultViewKey, Parcelable {
    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
}