package com.zhuinden.simplestackdemoexamplefragments.util

import com.zhuinden.simplestackdemoexamplefragments.application.Key

interface BaseViewContract {
    fun <T: Key> getKey(): T
}