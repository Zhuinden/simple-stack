package com.zhuinden.simplestackdemomultistack.core.navigation

import android.content.Context
import android.os.Parcelable

import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestackdemomultistack.util.ServiceLocator

abstract class MultistackKey : Parcelable {
    abstract fun layout(): Int

    fun selectDelegate(context: Context): BackstackDelegate {
        return ServiceLocator.getService(context, stackIdentifier())
    }

    abstract fun stackIdentifier(): String
}
