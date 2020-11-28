package com.zhuinden.simplestackextensionsample.features.login

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginKey(private val placeholder: String = "") : DefaultFragmentKey(), DefaultServiceProvider.HasServices {
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(LoginViewModel(lookup(), backstack))
        }
    }

    override fun getScopeTag(): String = javaClass.name

    override fun instantiateFragment(): Fragment = LoginFragment()
}