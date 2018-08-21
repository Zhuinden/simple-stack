package com.zhuinden.simplestackdemoexamplefragments.util

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

inline fun <T: View> T.showIf(predicate: (T) -> Boolean) {
    if(predicate(this)) {
        show()
    } else {
        hide()
    }
}

fun <A, B> combineTwo(aSource: Observable<A>, bSource: Observable<B>): Observable<Pair<A, B>> =
    Observable.combineLatest(aSource, bSource, BiFunction { t1, t2 -> t1 to t2 })

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToParent: Boolean = false) =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToParent)