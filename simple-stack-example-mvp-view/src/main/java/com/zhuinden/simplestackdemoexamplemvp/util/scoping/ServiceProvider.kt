package com.zhuinden.simplestackdemoexamplemvp.util.scoping

import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestackdemoexamplemvp.core.navigation.ViewKey

class ServiceProvider: ScopedServices {
    interface HasServices: ScopeKey {
        fun bindServices(serviceBinder: ScopedServices.ServiceBinder)
    }

    override fun bindServices(serviceBinder: ScopedServices.ServiceBinder) {
        val key = serviceBinder.getKey<ViewKey>()
        if(key is HasServices) {
            key.bindServices(serviceBinder)
        }
    }
}