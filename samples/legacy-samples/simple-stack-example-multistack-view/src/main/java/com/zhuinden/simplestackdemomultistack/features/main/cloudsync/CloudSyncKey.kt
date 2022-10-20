package com.zhuinden.simplestackdemomultistack.features.main.cloudsync

import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackViewKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object CloudSyncKey : MultistackViewKey() {
    override fun layout(): Int = R.layout.cloudsync_view
    override fun stackIdentifier(): String = MainActivity.StackType.CLOUDSYNC.name
}
