package com.zhuinden.simplestackdemomultistack.features.main.list

import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListKey(private val placeholder: String = "") : MultistackKey() {
    override fun layout(): Int = R.layout.list_view
    override fun stackIdentifier(): String = MainActivity.StackType.LIST.name
}

