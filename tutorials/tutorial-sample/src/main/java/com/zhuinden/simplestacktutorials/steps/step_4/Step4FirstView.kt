package com.zhuinden.simplestacktutorials.steps.step_4

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.simplestacktutorials.databinding.Step4FirstViewBinding
import com.zhuinden.simplestacktutorials.utils.onClick

class Step4FirstView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defaultStyleRes: Int) : super(context, attrs, defaultStyleRes)

    private lateinit var binding: Step4FirstViewBinding

    override fun onFinishInflate() {
        super.onFinishInflate()

        binding = Step4FirstViewBinding.bind(this)

        binding.step4FirstButton.onClick {
            backstack.goTo(Step4SecondScreen())
        }
    }
}