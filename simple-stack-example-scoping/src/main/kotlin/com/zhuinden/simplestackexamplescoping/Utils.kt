package com.zhuinden.simplestackexamplescoping

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestack.navigator.Navigator

/**
 * Created by zhuinden on 2018. 03. 03..
 */
val Fragment.requireArguments
    get() = this.arguments ?: throw IllegalStateException("Arguments should exist!")

val View.backstack
    get() = Navigator.getBackstack(context)

val Fragment.backstack
    get() = Navigator.getBackstack(requireContext())

@Suppress("NOTHING_TO_INLINE")
inline fun View.onClick(noinline clickListener: (View) -> Unit) {
    setOnClickListener(clickListener)
}

fun Unit.safe() {
}

fun Any.safe() {
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToParent: Boolean = false) =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToParent)

inline fun <reified T> Fragment.canFind(serviceTag: String = T::class.java.name) = Navigator.canFindService(requireContext(), serviceTag)

inline fun <reified T> Fragment.lookup(serviceTag: String = T::class.java.name) = Navigator.lookupService<T>(requireContext(), serviceTag)

inline fun <reified T> ServiceBinder.add(service: T, serviceTag: String = T::class.java.name) {
    addService(serviceTag, service as Any)
}

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

fun Fragment.showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
    requireContext().showToast(text, duration)
}