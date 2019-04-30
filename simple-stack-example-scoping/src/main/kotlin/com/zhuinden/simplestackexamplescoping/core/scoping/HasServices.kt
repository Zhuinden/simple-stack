package com.zhuinden.simplestackexamplescoping.core.scoping

import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestack.ServiceBinder

interface HasServices : ScopeKey {
    override fun getScopeTag(): String = javaClass.name

    fun bindServices(serviceBinder: ServiceBinder)
}