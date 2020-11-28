package com.zhuinden.simplestackdemomultistack.features.main.cloudsync.another

import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.core.navigation.BaseFragment
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnotherKey(private val placeholder: String = "") : MultistackFragmentKey() {
    override fun stackIdentifier(): String = MainActivity.StackType.CLOUDSYNC.name

    override fun createFragment(): BaseFragment = AnotherFragment()
}
