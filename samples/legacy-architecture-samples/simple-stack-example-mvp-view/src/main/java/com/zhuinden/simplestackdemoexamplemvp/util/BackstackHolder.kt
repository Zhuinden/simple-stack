package com.zhuinden.simplestackdemoexamplemvp.util

import com.zhuinden.simplestack.Backstack

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Owner on 2017. 01. 27..
 */
@Singleton
@Deprecated(message = "Singleton outlives the Activity")
class BackstackHolder @Inject constructor() {
    lateinit var backstack: Backstack
}
