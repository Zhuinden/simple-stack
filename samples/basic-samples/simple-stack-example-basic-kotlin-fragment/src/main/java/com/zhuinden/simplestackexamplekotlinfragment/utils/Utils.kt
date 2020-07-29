package com.zhuinden.simplestackexamplekotlinfragment.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange

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