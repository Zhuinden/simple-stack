package com.zhuinden.simplestackdemomultistack.features.main.list

import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackViewKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object ListKey : MultistackViewKey() {
    override fun layout(): Int = R.layout.list_view
    override fun stackIdentifier(): String = MainActivity.StackType.LIST.name
}

