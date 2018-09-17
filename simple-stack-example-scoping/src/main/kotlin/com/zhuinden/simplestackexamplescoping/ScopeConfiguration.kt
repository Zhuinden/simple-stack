package com.zhuinden.simplestackexamplescoping

import com.zhuinden.simplestack.ScopedServices

class ScopeConfiguration : ScopedServices {
    override fun bindServices(serviceBinder: ScopedServices.ServiceBinder) {
        when (serviceBinder.scopeTag) {
            WordScope.SCOPE_TAG -> {
                serviceBinder.addService(WordController())
            }
        }
    }
}