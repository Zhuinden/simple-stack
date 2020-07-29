package com.zhuinden.navigationexamplekotlinview.utils

import android.view.View
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange

inline fun View.onClick(crossinline click: () -> Unit) {
    setOnClickListener { _ ->
        click()
    }
}

fun Backstack.replaceHistory(vararg keys: Any) {
    setHistory(History.of(*keys), StateChange.REPLACE)
}