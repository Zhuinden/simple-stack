package com.zhuinden.simplestackdemoexample.common

import android.content.Context

import com.zhuinden.simplestack.Backstack

/**
 * Created by Owner on 2017. 01. 31..
 */

object BackstackService {
    val TAG = "BackstackService"

    fun get(context: Context): Backstack {
        return context.getSystemService(TAG) as Backstack
    }
}
