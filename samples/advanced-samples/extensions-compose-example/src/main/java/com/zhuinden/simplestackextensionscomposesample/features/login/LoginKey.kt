package com.zhuinden.simplestackextensionscomposesample.features.login

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.simplestackextensionscomposesample.app.AuthenticationManager
import com.zhuinden.simplestackextensionscomposesample.app.FragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object LoginKey : FragmentKey() {
    @Suppress("RemoveExplicitTypeArguments")
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(LoginViewModel(lookup<AuthenticationManager>(), backstack))
        }
    }

    override fun instantiateFragment(): Fragment = LoginFragment()
}