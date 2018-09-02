package com.zhuinden.simplestackdemoexamplefragments.util

interface MvpPresenter<V> {
    fun attachView(view: V)

    fun detachView(view: V)
}