package com.zhuinden.navigationexamplekotlinview.utils

import android.app.Activity
import android.view.View
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator

inline fun View.onClick(crossinline click: () -> Unit) {
    setOnClickListener { _ ->
        click()
    }
}

val View.backstack get() = Navigator.getBackstack(context)

val Activity.backstack get() = Navigator.getBackstack(this)

fun Backstack.replaceHistory(vararg keys: Any) {
    setHistory(History.of(*keys), StateChange.REPLACE)
}