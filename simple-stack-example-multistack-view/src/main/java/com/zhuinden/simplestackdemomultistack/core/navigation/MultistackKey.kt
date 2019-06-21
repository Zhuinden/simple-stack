package com.zhuinden.simplestackdemomultistack.core.navigation

import android.content.Context
import android.os.Parcelable
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackdemomultistack.util.ServiceLocator

abstract class MultistackKey : Parcelable {
    abstract fun layout(): Int

    fun selectBackstack(context: Context): Backstack {
        return ServiceLocator.getService(context, stackIdentifier())
    }

    abstract fun stackIdentifier(): String
}
