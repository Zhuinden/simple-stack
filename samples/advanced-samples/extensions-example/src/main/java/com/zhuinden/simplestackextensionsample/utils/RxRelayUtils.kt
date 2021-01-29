package com.zhuinden.simplestackextensionsample.utils

import com.jakewharton.rxrelay2.BehaviorRelay

fun <T : Any> BehaviorRelay<T>.get(): T = value!!

fun <T : Any> BehaviorRelay<T>.getOrNull(): T? = value

fun <T : Any> BehaviorRelay<T>.set(value: T) {
    this.accept(value)
}