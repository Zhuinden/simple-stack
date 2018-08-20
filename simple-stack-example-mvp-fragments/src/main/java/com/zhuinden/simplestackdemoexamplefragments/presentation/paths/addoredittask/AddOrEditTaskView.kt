package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

/**
 * Created by Owner on 2017. 01. 26..
 */

class AddOrEditTaskView : ScrollView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    }
}
