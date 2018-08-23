package com.zhuinden.simplestackkotlindaggerexample

import android.support.v4.app.Fragment
import android.view.View
import io.reactivex.disposables.CompositeDisposable

fun CompositeDisposable.clearIfNotDisposed() {
    if (!isDisposed) {
        clear()
    }
}

val Fragment.requireArguments
    get() = this.arguments ?: throw IllegalStateException("Arguments should exist!")

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}