package com.zhuinden.simplestackdemoexamplemvp.util

import android.view.View

interface MvpPresenter<V: View> {
    fun attachView(view: V)

    fun detachView(view: V)
}