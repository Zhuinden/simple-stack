package com.zhuinden.simplestacktutorials.steps.step_4

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestacktutorials.utils.onClick
import kotlinx.android.synthetic.main.step4_second_view.view.*

class Step4SecondView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defaultStyleRes: Int) : super(context, attrs, defaultStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isInEditMode) {
            return
        }

        val args = Backstack.getKey<Step4SecondScreen>(context) // get args passed from previous screen

        step4SecondBack.onClick {
            backstack.goBack()
        }
    }
}