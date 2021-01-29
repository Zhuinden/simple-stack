package com.zhuinden.simplestackexamplemvvm.core.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.application.BaseKey
import com.zhuinden.simplestackexamplemvvm.application.MainActivity
import com.zhuinden.simplestackexamplemvvm.databinding.ViewToolbarBinding
import com.zhuinden.simplestackexamplemvvm.util.dp
import com.zhuinden.simplestackexamplemvvm.util.findActivity
import com.zhuinden.simplestackexamplemvvm.util.getSelectableItemBackgroundDrawable
import com.zhuinden.simplestackextensions.navigatorktx.backstack

class CustomToolbar : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    lateinit var binding: ViewToolbarBinding
        private set

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isInEditMode) return

        binding = ViewToolbarBinding.bind(this)

        val isBackEnabled = backstack.getHistory<BaseKey>().size > 1

        binding.buttonAction.setImageResource(when {
            isBackEnabled -> R.drawable.ic_baseline_arrow_back_white_24
            else -> R.drawable.ic_hamburger
        })
        binding.buttonAction.contentDescription = when {
            isBackEnabled -> "Back"
            else -> "Open Drawer"
        }
        binding.buttonAction.setOnClickListener {
            if (isBackEnabled) {
                backstack.goBack()
            } else {
                (context.findActivity() as MainActivity).toggleLeftDrawer()
            }
        }
    }

    fun addExtraAction(contentDescription: String, @DrawableRes imageResource: Int, onClick: (View) -> Unit) {
        binding.containerExtraActions.addView(
            AppCompatImageView(context).apply {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    foreground = context.getSelectableItemBackgroundDrawable()
                }
                layoutParams = LinearLayout.LayoutParams(dp(32), dp(32)).apply {
                    setMargins(dp(8), 0, dp(8), 0)
                }
                setContentDescription(contentDescription)
                setImageResource(imageResource)
                setOnClickListener { view ->
                    onClick(view)
                }
            }
        )
    }
}