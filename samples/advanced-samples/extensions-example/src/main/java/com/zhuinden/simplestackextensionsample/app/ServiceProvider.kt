package com.zhuinden.simplestackextensionsample.app

import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensionsample.features.registration.RegistrationViewModel

class ServiceProvider : DefaultServiceProvider() {
    override fun bindServices(serviceBinder: ServiceBinder) {
        super.bindServices(serviceBinder)

        val scope = serviceBinder.scopeTag

        with(serviceBinder) {
            when (scope) { // explicit shared services
                "registration" -> add(
                    RegistrationViewModel(
                        lookupService("appContext"),
                        backstack
                    )
                )
                else -> {
                }
            }
        }
    }
}
