package com.zhuinden.simplestackbottomnavfragmentexample.features.root

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackbottomnavfragmentexample.core.navigation.FragmentStackHost
import com.zhuinden.simplestackbottomnavfragmentexample.features.root.first.First1Screen
import com.zhuinden.simplestackbottomnavfragmentexample.features.root.second.SecondScreen
import com.zhuinden.simplestackbottomnavfragmentexample.features.root.third.ThirdScreen
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.rebind
import kotlinx.parcelize.Parcelize

@Parcelize
class RootScreen : DefaultFragmentKey(), DefaultServiceProvider.HasServices {
    companion object {
        const val FIRST_STACK = "FirstStack"
        const val SECOND_STACK = "SecondStack"
        const val THIRD_STACK = "ThirdStack"

        const val FIRST_STACK_HOST = "$FIRST_STACK-HOST"
        const val SECOND_STACK_HOST = "$SECOND_STACK-HOST"
        const val THIRD_STACK_HOST = "$THIRD_STACK-HOST"
    }

    override fun instantiateFragment(): Fragment = RootFragment()

    override fun getScopeTag(): String = javaClass.name

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            val firstHost = FragmentStackHost(First1Screen())
            val secondHost = FragmentStackHost(SecondScreen())
            val thirdHost = FragmentStackHost(ThirdScreen())

            add(firstHost, FIRST_STACK_HOST)
            add(secondHost, SECOND_STACK_HOST)
            add(thirdHost, THIRD_STACK_HOST)

            rebind<Backstack>(firstHost.backstack, FIRST_STACK)
            rebind<Backstack>(secondHost.backstack, SECOND_STACK)
            rebind<Backstack>(thirdHost.backstack, THIRD_STACK)
        }
    }
}