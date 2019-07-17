package com.zhuinden.simplestackexamplekotlinfragment.utils

import android.app.Activity
import android.support.v4.app.Fragment
import android.view.View
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator

/**
 * Created by zhuinden on 2018. 03. 03..
 */
val Fragment.requireArguments
    get() = this.arguments ?: throw IllegalStateException("Arguments should exist!")

inline fun View.onClick(crossinline click: (View) -> Unit) {
    setOnClickListener { view ->
        click(view)
    }
}

fun Backstack.replaceHistory(vararg keys: Any) {
    this.setHistory(keys.toList(), StateChange.REPLACE)
}

val Activity.backstack: Backstack get() = Navigator.getBackstack(this)

val Fragment.backstack: Backstack get() = Navigator.getBackstack(requireActivity())