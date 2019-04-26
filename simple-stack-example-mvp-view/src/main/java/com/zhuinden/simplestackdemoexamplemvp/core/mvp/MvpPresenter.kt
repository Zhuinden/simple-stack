package com.zhuinden.simplestackdemoexamplemvp.core.mvp

import android.view.View

interface MvpPresenter<V: View> {
    fun attachView(view: V)

    fun detachView(view: V)
}