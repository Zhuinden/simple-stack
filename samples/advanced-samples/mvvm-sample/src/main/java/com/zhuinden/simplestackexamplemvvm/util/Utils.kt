package com.zhuinden.simplestackexamplemvvm.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

tailrec fun Context.findActivity(): Activity {
    if (this is Activity) return this

    val parent = (this as? ContextWrapper)?.baseContext
        ?: throw IllegalArgumentException("No activity found")

    return parent.findActivity()
}

fun Context.dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
fun Context.pxToDp(px: Int): Int = (px / resources.displayMetrics.density).toInt()

fun Fragment.dpToPx(dp: Int): Int = requireContext().dpToPx(dp)
fun Fragment.pxToDp(px: Int): Int = requireContext().pxToDp(px)

fun Fragment.dp(dp: Int): Int = dpToPx(dp)

fun View.dpToPx(dp: Int): Int = context.dpToPx(dp)
fun View.pxToDp(px: Int): Int = context.pxToDp(px)

fun View.dp(dp: Int): Int = dpToPx(dp)

fun Context.getSelectableItemBackgroundResource(): Int {
    val outValue = TypedValue()
    getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
    return outValue.resourceId
}

fun Context.getSelectableItemBackgroundDrawable(): Drawable? {
    return ContextCompat.getDrawable(this, getSelectableItemBackgroundResource())
}
