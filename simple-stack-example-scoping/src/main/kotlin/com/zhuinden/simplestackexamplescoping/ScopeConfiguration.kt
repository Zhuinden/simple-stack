package com.zhuinden.simplestackexamplescoping

import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.ServiceBinder

class ScopeConfiguration : ScopedServices {
    override fun bindServices(serviceBinder: ServiceBinder) {
        when (serviceBinder.scopeTag) {
            WordScope.SCOPE_TAG -> {
                serviceBinder.addService(WordController())
            }
        }
    }
}