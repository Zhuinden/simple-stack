package com.zhuinden.simplestackextensionscomposesample.app

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.simplestackextensionscomposesample.features.registration.RegistrationViewModel

class ServiceProvider : DefaultServiceProvider() {
    @Suppress("RemoveExplicitTypeArguments")
    override fun bindServices(serviceBinder: ServiceBinder) {
        super.bindServices(serviceBinder)

        val scope = serviceBinder.scopeTag

        with(serviceBinder) {
            when (scope) {
                RegistrationViewModel::class.java.name -> {
                    add(RegistrationViewModel(lookup<AuthenticationManager>(), backstack))
                }
                else -> {
                }
            }
        }
    }
}
