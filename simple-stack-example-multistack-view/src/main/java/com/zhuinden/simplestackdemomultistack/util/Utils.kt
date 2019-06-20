package com.zhuinden.simplestackdemomultistack.util

import android.animation.Animator
import android.animation.AnimatorSet
import android.view.View
import android.view.ViewTreeObserver
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator

val Int.f: Float get() = this.toFloat()

typealias OnMeasuredCallback = (view: View, width: Int, height: Int) -> Unit

fun View.waitForMeasure(callback: OnMeasuredCallback) {
    val view = this
    val width = view.width
    val height = view.height

    if (width > 0 && height > 0) {
        callback(view, width, height)
        return
    }

    view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            val observer = view.viewTreeObserver
            if (observer.isAlive) {
                observer.removeOnPreDrawListener(this)
            }

            callback(view, view.width, view.height)

            return true
        }
    })
}

fun View.objectAnimate() = ViewPropertyObjectAnimator.animate(this)

fun animateTogether(vararg animators: Animator): AnimatorSet =
    AnimatorSet().apply {
        playTogether(*animators)
    }

inline fun View.onClick(crossinline clickListener: (View) -> Unit): View.OnClickListener {
    val listener = View.OnClickListener {
        clickListener.invoke(this@onClick)
    }
    setOnClickListener(listener)
    return listener
}