package com.zhuinden.simplestacktutorials.steps.step_4

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestacktutorials.databinding.Step4SecondViewBinding
import com.zhuinden.simplestacktutorials.utils.onClick

class Step4SecondView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defaultStyleRes: Int) : super(context, attrs, defaultStyleRes)

    private lateinit var binding: Step4SecondViewBinding

    override fun onFinishInflate() {
        super.onFinishInflate()

        binding = Step4SecondViewBinding.bind(this)

        if (isInEditMode) {
            return
        }


        val args = Backstack.getKey<Step4SecondScreen>(context) // get args passed from previous screen

        binding.step4SecondBack.onClick {
            backstack.goBack()
        }
    }
}