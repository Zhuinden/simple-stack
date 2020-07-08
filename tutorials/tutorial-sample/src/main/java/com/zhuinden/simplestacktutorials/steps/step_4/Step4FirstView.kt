package com.zhuinden.simplestacktutorials.steps.step_4

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.simplestacktutorials.utils.onClick
import kotlinx.android.synthetic.main.step4_first_view.view.*

class Step4FirstView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defaultStyleRes: Int) : super(context, attrs, defaultStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()

        step4FirstButton.onClick {
            backstack.goTo(Step4SecondScreen())
        }
    }
}