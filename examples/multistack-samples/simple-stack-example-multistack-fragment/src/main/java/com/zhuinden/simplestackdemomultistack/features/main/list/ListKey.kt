package com.zhuinden.simplestackdemomultistack.features.main.list

import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.core.navigation.BaseFragment
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackFragmentKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListKey(private val placeholder: String = "") : MultistackFragmentKey() {
    override fun stackIdentifier(): String = MainActivity.StackType.LIST.name

    override fun createFragment(): BaseFragment = ListFragment()
}

