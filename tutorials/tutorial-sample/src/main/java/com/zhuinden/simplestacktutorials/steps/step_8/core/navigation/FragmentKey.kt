package com.zhuinden.simplestacktutorials.steps.step_8.core.navigation

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

abstract class FragmentKey : DefaultFragmentKey(), DefaultServiceProvider.HasServices {
    override fun getScopeTag(): String = toString()

    override fun getFragmentTag(): String = toString()

    override fun bindServices(serviceBinder: ServiceBinder) {
    }
}