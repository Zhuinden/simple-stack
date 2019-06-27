package com.zhuinden.simplestackdemomultistack.features.main.cloudsync.another

import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackViewKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AnotherKey(private val placeholder: String = "") : MultistackViewKey() {
    override fun layout(): Int = R.layout.another_view
    override fun stackIdentifier(): String = MainActivity.StackType.CLOUDSYNC.name
}
