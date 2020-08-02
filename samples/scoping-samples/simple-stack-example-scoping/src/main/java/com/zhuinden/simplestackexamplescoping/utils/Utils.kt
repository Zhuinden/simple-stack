package com.zhuinden.simplestackexamplescoping.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * Created by zhuinden on 2018. 03. 03..
 */
@Suppress("NOTHING_TO_INLINE")
inline fun View.onClick(noinline clickListener: (View) -> Unit) {
    setOnClickListener(clickListener)
}

fun Unit.safe() {
}

fun Any.safe() {
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToParent: Boolean = false) =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToParent)

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

fun Fragment.showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
    requireContext().showToast(text, duration)
}