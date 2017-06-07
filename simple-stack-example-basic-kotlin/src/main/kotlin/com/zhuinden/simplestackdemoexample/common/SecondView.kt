package com.zhuinden.simplestackdemoexample.common

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

import com.zhuinden.simplestack.Backstack

/**
 * Created by Owner on 2017. 01. 12..
 */

class SecondView : RelativeLayout {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    private fun init(context: Context) {
        if (!isInEditMode) {
            backstack = BackstackService.get(context)
            secondKey = Backstack.getKey<SecondKey>(context)
        }
    }

    lateinit internal var backstack: Backstack

    lateinit internal var secondKey: SecondKey
}
