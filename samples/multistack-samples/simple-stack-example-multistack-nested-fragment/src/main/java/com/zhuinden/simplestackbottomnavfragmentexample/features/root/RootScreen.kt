package com.zhuinden.simplestackbottomnavfragmentexample.features.root

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackbottomnavfragmentexample.core.navigation.FragmentStackHost
import com.zhuinden.simplestackbottomnavfragmentexample.features.root.first.First1Screen
import com.zhuinden.simplestackbottomnavfragmentexample.features.root.second.SecondScreen
import com.zhuinden.simplestackbottomnavfragmentexample.features.root.third.ThirdScreen
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.add
import kotlinx.parcelize.Parcelize

@Parcelize
class RootScreen : DefaultFragmentKey(), DefaultServiceProvider.HasServices {
    companion object {
        const val FIRST_STACK = "FirstStack"
        const val SECOND_STACK = "SecondStack"
        const val THIRD_STACK = "ThirdStack"
    }

    override fun instantiateFragment(): Fragment = RootFragment()

    override fun getScopeTag(): String = javaClass.name

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(FragmentStackHost(First1Screen()), FIRST_STACK)
            add(FragmentStackHost(SecondScreen()), SECOND_STACK)
            add(FragmentStackHost(ThirdScreen()), THIRD_STACK)
        }
    }
}