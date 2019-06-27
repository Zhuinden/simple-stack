package com.zhuinden.simplestackdemomultistack.core.navigation

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackdemomultistack.util.ServiceLocator

abstract class MultistackFragmentKey : Parcelable {
    fun selectBackstack(context: Context): Backstack {
        return ServiceLocator.getService(context, stackIdentifier())
    }

    abstract fun stackIdentifier(): String

    protected open fun generateFragmentTag(): String = toString()

    val fragmentTag: String get() = "${stackIdentifier()}_${generateFragmentTag()}"

    fun newFragment(): BaseFragment = createFragment().apply {
        arguments = (arguments ?: Bundle()).also { bundle ->
            bundle.putParcelable("KEY", this@MultistackFragmentKey)
        }
    }

    protected abstract fun createFragment(): BaseFragment
}
