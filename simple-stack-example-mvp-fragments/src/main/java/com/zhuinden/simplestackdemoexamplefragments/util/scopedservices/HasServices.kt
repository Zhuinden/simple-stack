package com.zhuinden.simplestackdemoexamplefragments.util.scopedservices

import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestack.ScopedServices

interface HasServices : ScopeKey {
    fun bindServices(serviceBinder: ScopedServices.ServiceBinder)
}
