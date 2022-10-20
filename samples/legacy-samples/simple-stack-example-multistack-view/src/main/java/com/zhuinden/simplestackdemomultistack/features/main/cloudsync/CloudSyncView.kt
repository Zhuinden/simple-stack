package com.zhuinden.simplestackdemomultistack.features.main.cloudsync

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.core.navigation.backstack
import com.zhuinden.simplestackdemomultistack.features.main.cloudsync.another.AnotherKey
import com.zhuinden.simplestackdemomultistack.util.onClick

class CloudSyncView : RelativeLayout {
    lateinit var cloudSyncKey: CloudSyncKey

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        if (!isInEditMode) {
            cloudSyncKey = Backstack.getKey(context)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        findViewById<View>(R.id.buttonFirst).onClick {
            backstack.goTo(AnotherKey)
        }
    }
}
