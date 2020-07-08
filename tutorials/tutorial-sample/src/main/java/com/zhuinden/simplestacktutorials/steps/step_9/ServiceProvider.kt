package com.zhuinden.simplestacktutorials.steps.step_9

import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestacktutorials.steps.step_9.core.navigation.FragmentKey
import com.zhuinden.simplestacktutorials.steps.step_9.core.viewmodels.HasServices
import com.zhuinden.simplestacktutorials.steps.step_9.core.viewmodels.add
import com.zhuinden.simplestacktutorials.steps.step_9.features.registration.RegistrationViewModel

class ServiceProvider : ScopedServices {
    override fun bindServices(serviceBinder: ServiceBinder) {
        val key = serviceBinder.getKey<FragmentKey>()

        val scope = serviceBinder.scopeTag

        if (key is HasServices && key.scopeTag == scope) {
            key.bindServices(serviceBinder) // screen-bound shared services
        }

        with(serviceBinder) {
            when (scope) { // explicit shared services
                "registration" -> add(
                    RegistrationViewModel(
                        lookupService("appContext"),
                        backstack
                    )
                )
            }
        }
    }
}