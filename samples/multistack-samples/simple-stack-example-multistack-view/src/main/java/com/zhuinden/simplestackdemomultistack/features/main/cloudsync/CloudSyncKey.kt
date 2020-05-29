package com.zhuinden.simplestackdemomultistack.features.main.cloudsync

import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackViewKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CloudSyncKey(private val placeholder: String = "") : MultistackViewKey() {
    override fun layout(): Int = R.layout.cloudsync_view
    override fun stackIdentifier(): String = MainActivity.StackType.CLOUDSYNC.name
}
