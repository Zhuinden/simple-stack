package com.zhuinden.simplestackdemoexample.common

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import butterknife.ButterKnife
import butterknife.OnClick
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestackdemoexample.R
import com.zhuinden.statebundle.StateBundle

/**
 * Created by Owner on 2017. 01. 12..
 */
class FirstView : RelativeLayout, Bundleable {
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

    lateinit internal var backstack: Backstack

    lateinit internal var firstKey: FirstKey

    private fun init(context: Context) {
        if (!isInEditMode) {
            backstack = BackstackService.get(context)
            firstKey = Backstack.getKey<FirstKey>(context)
        }
    }

    @OnClick(R.id.first_button)
    fun clickButton(view: View) {
        backstack.goTo(SecondKey.create())
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        ButterKnife.bind(this)
    }

    override fun toBundle(): StateBundle {
        return StateBundle().apply {
            putString("HELLO", "WORLD")
        }
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.let {
            Log.i(TAG, it.getString("HELLO"))
        }
    }

    companion object {
        private val TAG = "FirstView"
    }
}
