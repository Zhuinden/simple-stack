package com.zhuinden.simplestackdemomultistack.features.main.chromecast

import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.core.navigation.BaseFragment
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackFragmentKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChromeCastKey(private val placeholder: String = "") : MultistackFragmentKey() {
    override fun stackIdentifier(): String = MainActivity.StackType.CHROMECAST.name

    override fun createFragment(): BaseFragment = ChromeCastFragment()
}
