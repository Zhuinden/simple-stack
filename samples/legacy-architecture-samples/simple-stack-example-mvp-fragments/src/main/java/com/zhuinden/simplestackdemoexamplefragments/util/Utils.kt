package com.zhuinden.simplestackdemoexamplefragments.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestack.navigator.Navigator

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

tailrec fun <T : Activity> Context.findActivity(): T {
    if (this is Activity) {
        @Suppress("UNCHECKED_CAST")
        return this as T
    }
    val baseContext = (this as ContextWrapper).baseContext
        ?: throw IllegalArgumentException("Thie context does not contain activity as base context")

    return baseContext.findActivity()
}

val Fragment.requireArguments: Bundle
    get() = arguments ?: throw Exception("No arguments found!")

inline fun <reified T> Fragment.lookup(serviceTag: String = T::class.java.name) =
    Navigator.lookupService<T>(requireContext(), serviceTag)

inline fun <T : View> T.showIf(predicate: (T) -> Boolean) {
    if (predicate(this)) {
        show()
    } else {
        hide()
    }
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToParent: Boolean = false) =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToParent)

inline fun TextView.onTextChanged(crossinline textChanged: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            textChanged(s.toString())
        }
    })
}

inline fun View.onClick(crossinline click: (View) -> Unit) {
    setOnClickListener { view ->
        click(view)
    }
}