package com.zhuinden.simplestacktutorials.steps.step_8.core.viewmodels

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestacktutorials.steps.step_8.core.navigation.backstack

inline fun <reified T> Backstack.lookup(serviceTag: String = T::class.java.name) = lookupService<T>(serviceTag)

inline fun <reified T> Fragment.lookup(serviceTag: String = T::class.java.name) = backstack.lookup<T>(serviceTag)

inline fun <reified T> ServiceBinder.add(service: T, serviceTag: String = T::class.java.name) {
    this.addService(serviceTag, service as Any)
}

inline fun <reified NAME> ServiceBinder.bindAs(service: Any, serviceTag: String = NAME::class.java.name) {
    this.addAlias(serviceTag, service)
}

inline fun <reified T> ServiceBinder.lookup(serviceTag: String = T::class.java.name) = lookupService<T>(serviceTag)

inline fun <reified T> ServiceBinder.get(serviceTag: String = T::class.java.name) = getService<T>(serviceTag)