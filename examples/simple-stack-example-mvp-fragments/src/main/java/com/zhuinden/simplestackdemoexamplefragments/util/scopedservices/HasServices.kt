package com.zhuinden.simplestackdemoexamplefragments.util.scopedservices

import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestack.ServiceBinder

interface HasServices : ScopeKey {
    fun bindServices(serviceBinder: ServiceBinder)
}
