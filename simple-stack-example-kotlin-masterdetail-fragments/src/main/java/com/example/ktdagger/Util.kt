package com.example.ktdagger

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable

fun CompositeDisposable.clearIfNotDisposed() {
    if (!isDisposed) {
        clear();
    }
}

val Fragment.requireArguments
    get() = this.arguments ?: throw IllegalStateException("Arguments should exist!")
