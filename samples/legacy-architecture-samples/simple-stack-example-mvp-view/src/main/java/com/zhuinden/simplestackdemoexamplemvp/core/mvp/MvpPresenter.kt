package com.zhuinden.simplestackdemoexamplemvp.core.mvp

import android.view.View

@Deprecated(message = "Manual view event dispatch is an anti-pattern.")
interface MvpPresenter<V: View> {
    fun attachView(view: V)

    fun detachView(view: V)
}