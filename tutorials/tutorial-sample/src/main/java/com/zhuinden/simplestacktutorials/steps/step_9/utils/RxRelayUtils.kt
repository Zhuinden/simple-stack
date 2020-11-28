package com.zhuinden.simplestacktutorials.steps.step_9.utils

import com.jakewharton.rxrelay2.BehaviorRelay

fun <T> BehaviorRelay<T>.get(): T = value!!

fun <T> BehaviorRelay<T>.getOrNull(): T? = value

fun <T : Any> BehaviorRelay<T>.set(value: T) {
    this.accept(value)
}