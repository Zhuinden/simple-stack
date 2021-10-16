package com.zhuinden.simplestackextensionscomposesample.app

import android.os.Parcelable
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

abstract class FragmentKey : DefaultFragmentKey(), Parcelable, DefaultServiceProvider.HasServices {
    override fun getScopeTag(): String = toString()

    override fun bindServices(serviceBinder: ServiceBinder) {
    }
}