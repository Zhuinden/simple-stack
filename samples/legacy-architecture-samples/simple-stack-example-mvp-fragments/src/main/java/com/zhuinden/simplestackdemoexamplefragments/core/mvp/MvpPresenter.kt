package com.zhuinden.simplestackdemoexamplefragments.core.mvp

@Deprecated(message = "Manual view event dispatch is an anti-pattern.")
interface MvpPresenter<V> {
    fun attachView(view: V)

    fun detachView(view: V)
}