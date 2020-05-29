package com.zhuinden.simplestackdemoexamplefragments.util

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zhuinden.simplestack.navigator.Navigator
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
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

fun <A, B> combineTwo(aSource: Observable<A>, bSource: Observable<B>): Observable<Pair<A, B>> =
    Observable.combineLatest(aSource, bSource, BiFunction { t1, t2 -> t1 to t2 })

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