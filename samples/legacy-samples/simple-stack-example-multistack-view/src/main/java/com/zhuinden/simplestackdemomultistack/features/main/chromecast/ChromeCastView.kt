package com.zhuinden.simplestackdemomultistack.features.main.chromecast

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

import com.zhuinden.simplestack.Backstack

class ChromeCastView : RelativeLayout {
    lateinit var chromeCastKey: ChromeCastKey

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        if (!isInEditMode) {
            chromeCastKey = Backstack.getKey(context)
        }
    }
}
