package com.zhuinden.simplestacktutorials.steps.step_7.features.login

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestacktutorials.steps.step_7.features.core.FragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginKey(private val placeholder: String = "") : FragmentKey() {
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(LoginViewModel(lookupService("appContext"), backstack))
        }
    }

    override fun instantiateFragment(): Fragment = LoginFragment()
}