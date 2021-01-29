package com.zhuinden.simplestackdemomultistack.util

import android.content.Context

object ServiceLocator {
    fun <T> getService(context: Context, name: String): T {
        return context.getSystemService(name) as T
    }
}
