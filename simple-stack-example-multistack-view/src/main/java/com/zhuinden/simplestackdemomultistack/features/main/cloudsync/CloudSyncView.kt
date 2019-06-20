package com.zhuinden.simplestackdemomultistack.features.main.cloudsync

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.RelativeLayout

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackKey
import com.zhuinden.simplestackdemomultistack.features.main.cloudsync.another.AnotherKey
import com.zhuinden.statebundle.StateBundle

import com.zhuinden.simplestackdemomultistack.util.onClick
import kotlinx.android.synthetic.main.cloudsync_view.view.*

class CloudSyncView : RelativeLayout, Bundleable {
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

        buttonFirst.onClick {
            val key = Backstack.getKey<MultistackKey>(context)
            key.selectDelegate(context).backstack.goTo(AnotherKey.create())
        }
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putString("HELLO", "WORLD")
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            Log.i(TAG, getString("HELLO"))
        }
    }

    companion object {
        private const val TAG = "FirstView"
    }
}
